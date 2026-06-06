import json
import socket


class ProveedorSocket:

    HOST = "localhost"
    PUERTO = 6000

    def enviar(self, mensaje):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as cliente:
            cliente.settimeout(10)
            cliente.connect((self.HOST, self.PUERTO))
            cliente.sendall((json.dumps(mensaje) + "\n").encode("utf-8"))

            respuesta = cliente.recv(4096).decode("utf-8").strip()

        return json.loads(respuesta)

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
