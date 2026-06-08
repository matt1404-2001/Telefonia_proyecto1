import json
import os
import socket
import sys
import threading
import time
from datetime import datetime, timedelta

rutaProyecto = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(rutaProyecto)

from Capa_Datos.ProveedorSocket import ProveedorSocket
from Capa_Datos.TelefonoDB import TelefonoDB
from Capa_Negocio.Bitacora import Bitacora
from Capa_Negocio.CifradorAES import CifradorAES
from Capa_Negocio.Validador import Validador


HOST = "localhost"
PUERTO = 8000
LLAMADAS_ACTIVAS = []
LLAMADAS_LOCK = threading.Lock()


def normalizarDatos(datos):
    descifrarCamposSensibles(datos)

    if "tipo" not in datos and "tipoTransaccion" in datos:
        datos["tipo"] = datos["tipoTransaccion"]

    if "tiempo" in datos and "tiempoMaximo" not in datos:
        datos["tiempoMaximo"] = datos["tiempo"]

    if "ubicacion" in datos and ("latitud" not in datos or "longitud" not in datos):
        partes = str(datos["ubicacion"]).split(",")
        if len(partes) == 2:
            datos["latitud"] = partes[0].strip()
            datos["longitud"] = partes[1].strip()

    if "telefonoDestino" in datos:
        datos["telefonoDestino"] = normalizarTelefonoDestino(datos["telefonoDestino"])

    if "telefono" in datos and "telefonoDestino" in datos:
        datos["tipoLlamada"] = calcularTipoLlamada(datos["telefono"], datos["telefonoDestino"])

    return datos


def descifrarCamposSensibles(datos):
    for campo in ["telefono", "identificadorTelefono", "identificadorTarjeta"]:
        if campo not in datos:
            continue

        valor = str(datos[campo]).strip()
        if len(valor) == 0:
            continue

        try:
            datos[campo] = CifradorAES.descifrar(valor)
        except Exception:
            datos[campo] = valor


def normalizarTelefonoDestino(telefono):
    destino = str(telefono).replace("+", "").replace("-", "").replace(" ", "").strip()
    destino = destino.replace("(", "").replace(")", "")

    if destino.startswith("506") and len(destino) == 11:
        return destino[3:]

    return destino


def calcularTipoLlamada(telefonoOrigen, telefonoDestino):
    origen = normalizarTelefonoDestino(telefonoOrigen)
    destino = normalizarTelefonoDestino(telefonoDestino)

    if len(destino) != 8:
        return 3

    telefonoDB = TelefonoDB()
    proveedorOrigen = telefonoDB.buscarProveedorId(origen)
    proveedorDestino = telefonoDB.buscarProveedorId(destino)

    if proveedorOrigen is not None and proveedorOrigen == proveedorDestino:
        return 1

    return 2


def atenderCliente(cliente, direccion):
    try:
        mensaje = cliente.recv(8192).decode("utf-8").strip()

        if len(mensaje) == 0:
            return

        datos = normalizarDatos(json.loads(mensaje))
        Bitacora.registrar("ENTRADA", datos)

        tipo = datos.get("tipo", "").lower()

        if tipo == "solicitud":
            respuesta = procesarSolicitud(datos)
        elif tipo == "saldo":
            respuesta = procesarSaldo(datos)
        elif tipo == "llamada":
            respuesta = procesarInicioLlamada(datos)
        elif tipo == "finalizacion":
            respuesta = procesarFinalizacion(datos)
        else:
            respuesta = {"status": "ERROR", "motivo": 4}

    except Exception as error:
        Bitacora.registrar("ERROR", {
            "componente": "ServidorIdentificador",
            "mensaje": str(error)
        })
        respuesta = {"status": "ERROR", "motivo": 5}

    Bitacora.registrar("SALIDA", respuesta)
    cliente.sendall(json.dumps(respuesta).encode("utf-8"))
    cliente.close()


def procesarSolicitud(datos):
    resultado = Validador().validarSolicitud(datos)

    if resultado["status"] != "OK":
        return resultado

    respuestaProveedor = ProveedorSocket().autorizarLlamada(
        datos["telefono"],
        int(datos["tipoLlamada"]),
        datos["telefonoDestino"]
    )

    if respuestaProveedor.get("status") == "OK":
        return {
            "status": "OK",
            "tiempo": respuestaProveedor.get("tiempo", "000000"),
            "tarifa": respuestaProveedor.get("tarifa", "0000000000")
        }

    if respuestaProveedor.get("status") == "INSUF":
        return {"status": "INSUF"}

    return {"status": "ERROR", "motivo": 5}


def procesarInicioLlamada(datos):
    resultado = Validador().validarLlamada(datos)

    if resultado["status"] != "OK":
        return resultado

    inicio = datetime.now()
    segundos = tiempoASegundos(datos.get("tiempoMaximo", "000000"))
    finMaximo = inicio + timedelta(seconds=max(0, segundos))

    llamada = {
        "telefono": datos["telefono"],
        "telefonoDestino": datos["telefonoDestino"],
        "fechaInicio": inicio,
        "fechaFinMaxima": finMaximo,
        "tarifa": datos.get("tarifa", "0000000000"),
        "tiempoMaximo": datos.get("tiempoMaximo", "000000")
    }

    with LLAMADAS_LOCK:
        LLAMADAS_ACTIVAS.append(llamada)
        LLAMADAS_ACTIVAS.sort(key=lambda item: item["fechaFinMaxima"])

    return {"status": "OK", "estado": "ok"}


def procesarFinalizacion(datos):
    resultado = Validador().validarFinalizacion(datos)

    if resultado["status"] != "OK":
        return resultado

    llamada = extraerLlamadaActiva(datos["telefono"], datos["telefonoDestino"])

    if llamada is None:
        return {"status": "ERROR", "estado": "fallido", "motivo": 5}

    respuestaProveedor = registrarMovimientoProveedor(llamada, "usuario")

    if respuestaProveedor.get("status") == "OK":
        return {"status": "OK", "estado": "ok"}

    return {"status": "ERROR", "estado": "fallido", "motivo": 5}


def extraerLlamadaActiva(telefono, telefonoDestino):
    with LLAMADAS_LOCK:
        for indice, llamada in enumerate(LLAMADAS_ACTIVAS):
            if llamada["telefono"] == telefono and llamada["telefonoDestino"] == telefonoDestino:
                return LLAMADAS_ACTIVAS.pop(indice)

    return None


def registrarMovimientoProveedor(llamada, razon):
    fin = datetime.now()
    duracionSegundos = max(1, int((fin - llamada["fechaInicio"]).total_seconds()))
    duracion = segundosATiempo(duracionSegundos)
    costoTotal = calcularCostoTotal(llamada.get("tarifa", "0000000000"), duracionSegundos)

    Bitacora.registrar("EVENTO", {
        "tipo": "cierre_llamada",
        "razon": razon,
        "telefono": llamada["telefono"],
        "telefonoDestino": llamada["telefonoDestino"],
        "duracion": duracion,
        "costoTotal": costoTotal
    })

    return ProveedorSocket().registrarLlamada(
        llamada["telefono"],
        llamada["fechaInicio"].strftime("%Y%m%d"),
        llamada["fechaInicio"].strftime("%H%M%S"),
        llamada["telefonoDestino"],
        costoTotal,
        duracion
    )


def vigilarLlamadasActivas():
    while True:
        vencidas = []
        ahora = datetime.now()

        with LLAMADAS_LOCK:
            while len(LLAMADAS_ACTIVAS) > 0 and LLAMADAS_ACTIVAS[0]["fechaFinMaxima"] <= ahora:
                vencidas.append(LLAMADAS_ACTIVAS.pop(0))

        for llamada in vencidas:
            try:
                registrarMovimientoProveedor(llamada, "saldo agotado")
            except Exception as error:
                Bitacora.registrar("ERROR", {
                    "tipo": "cierre_automatico",
                    "mensaje": str(error),
                    "telefono": llamada.get("telefono")
                })

        time.sleep(1)


def tiempoASegundos(valor):
    texto = str(valor).zfill(6)[-6:]
    return int(texto[0:2]) * 3600 + int(texto[2:4]) * 60 + int(texto[4:6])


def segundosATiempo(segundos):
    horas = min(24, segundos // 3600)
    minutos = (segundos % 3600) // 60
    segundosRestantes = segundos % 60
    return f"{horas:02d}{minutos:02d}{segundosRestantes:02d}"


def calcularCostoTotal(tarifa, duracionSegundos):
    if str(tarifa) == "9999999999":
        return 0

    centesimasPorMinuto = int(str(tarifa).strip() or "0")
    minutosCobrados = max(1, (duracionSegundos + 59) // 60)
    return centesimasPorMinuto * minutosCobrados


def procesarSaldo(datos):
    resultado = Validador().validarSaldo(datos)

    if resultado["status"] != "OK":
        return resultado

    respuestaProveedor = ProveedorSocket().consultarSaldo(datos["telefono"])

    if respuestaProveedor.get("status") == "OK":
        return {
            "status": "OK",
            "saldo": respuestaProveedor.get("saldo", "0")
        }

    return {"status": "ERROR", "motivo": 5}


def iniciarServidor():
    Bitacora.iniciarHilo()
    threading.Thread(target=vigilarLlamadasActivas, daemon=True).start()

    socketServidor = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    socketServidor.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    socketServidor.bind((HOST, PUERTO))
    socketServidor.listen(5)

    print(f"Servidor Identificador iniciado en {HOST}:{PUERTO}")

    while True:
        cliente, direccion = socketServidor.accept()
        hilo = threading.Thread(
            target=atenderCliente,
            args=(cliente, direccion),
            daemon=True
        )
        hilo.start()


if __name__ == "__main__":
    iniciarServidor()
