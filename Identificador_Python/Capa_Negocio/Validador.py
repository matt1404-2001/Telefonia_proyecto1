from Capa_Negocio.Telefono import Telefono
from Capa_Negocio.Tarjeta import Tarjeta
from Capa_Negocio.Transaccion import Transaccion
from Capa_Negocio.Ubicacion import Ubicacion


class Validador:

    def validarSolicitud(self, datos):
        campos = [
            "telefono",
            "identificadorTelefono",
            "identificadorTarjeta",
            "latitud",
            "longitud",
            "tipo",
            "telefonoDestino"
        ]

        if not self.__camposObligatorios(datos, campos):
            return {"status": "ERROR", "motivo": 5}

        return self.__validarBase(datos, "solicitud")

    def validarSaldo(self, datos):
        campos = [
            "telefono",
            "identificadorTelefono",
            "identificadorTarjeta",
            "latitud",
            "longitud",
            "tipo"
        ]

        if not self.__camposObligatorios(datos, campos):
            return {"status": "ERROR", "motivo": 5}

        return self.__validarBase(datos, "saldo")

    def __validarBase(self, datos, tipoEsperado):
        try:
            estadoTelefono = Telefono(datos["telefono"]).validarTelefono()

            if estadoTelefono != "ACTIVO":
                return {"status": "ERROR", "motivo": 1}

            tarjeta = Tarjeta(
                datos["telefono"],
                datos["identificadorTelefono"],
                datos["identificadorTarjeta"]
            )

            if tarjeta.validarTarjeta() != "DATOS CORRECTOS":
                return {"status": "ERROR", "motivo": 2}

            ubicacion = Ubicacion(datos["latitud"], datos["longitud"])

            if not ubicacion.validarUbicacion():
                return {"status": "ERROR", "motivo": 3}

            transaccion = Transaccion(datos["tipo"])

            if tipoEsperado == "solicitud" and not transaccion.validarSolicitud():
                return {"status": "ERROR", "motivo": 4}

            if tipoEsperado == "saldo" and not transaccion.validarSaldo():
                return {"status": "ERROR", "motivo": 4}

            return {"status": "OK"}

        except Exception:
            return {"status": "ERROR", "motivo": 5}

    def __camposObligatorios(self, datos, campos):
        for campo in campos:
            if campo not in datos:
                return False

            if datos[campo] is None:
                return False

            if len(str(datos[campo]).strip()) == 0:
                return False

        return True
