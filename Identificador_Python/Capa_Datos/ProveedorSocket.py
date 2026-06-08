import json
import socket
from Capa_Negocio.Bitacora import Bitacora


class ProveedorSocket:

    HOST = "localhost"
    PUERTO = 6000

    def enviar(self, mensaje):
        try:
            with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as cliente:
                cliente.settimeout(10)
                cliente.connect((self.HOST, self.PUERTO))
                cliente.sendall((json.dumps(mensaje) + "\n").encode("utf-8"))

                respuesta = cliente.recv(4096).decode("utf-8").strip()

            return json.loads(respuesta)
        except Exception as error:
            Bitacora.registrar("ERROR", {
                "componente": "ProveedorSocket",
                "mensaje": str(error),
                "trama": mensaje
            })
            return {"status": "ERROR"}

    def consultarSaldo(self, telefono):
        return self.enviar({
            "tipo": "verificar_saldo",
            "telefono": telefono,
            "tipoTransaccion": 2
        })

    def autorizarLlamada(self, telefono, tipoLlamada, telefonoDestino):
        return self.enviar({
            "tipo": "verificar_saldo",
            "telefono": telefono,
            "tipoTransaccion": 1,
            "tipoLlamada": tipoLlamada,
            "telefonoDestino": telefonoDestino
        })

    def registrarLlamada(self, telefono, fecha, hora, telefonoDestino, costoTotal, duracion):
        return self.enviar({
            "tipo": "registrar_llamada",
            "telefono": telefono,
            "fecha": int(fecha),
            "hora": int(hora),
            "telefonoDestino": telefonoDestino,
            "costoTotal": int(costoTotal),
            "duracion": duracion
        })
