import json
import os
import socket
import sys
import threading

rutaProyecto = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.append(rutaProyecto)

from Capa_Datos.ProveedorSocket import ProveedorSocket
from Capa_Datos.TelefonoDB import TelefonoDB
from Capa_Negocio.Bitacora import Bitacora
from Capa_Negocio.Validador import Validador


HOST = "localhost"
PUERTO = 8000


def normalizarDatos(datos):
    if "tipo" not in datos and "tipoTransaccion" in datos:
        datos["tipo"] = datos["tipoTransaccion"]

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
        else:
            respuesta = {"status": "ERROR", "motivo": 4}

    except Exception:
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
