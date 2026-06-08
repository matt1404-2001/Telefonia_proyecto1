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

    def validarLlamada(self, datos):
        campos = [
            "telefono",
            "identificadorTelefono",
            "identificadorTarjeta",
            "latitud",
            "longitud",
            "tipo",
            "telefonoDestino",
            "tiempoMaximo"
        ]

        if not self.__camposObligatorios(datos, campos):
            return {"status": "ERROR", "motivo": 5}

        return self.__validarBase(datos, "llamada")

    def validarFinalizacion(self, datos):
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

        return self.__validarBase(datos, "finalizacion")

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

            if tipoEsperado == "solicitud":
                resultadoDestino = self.__validarDestino(datos["telefonoDestino"])

                if resultadoDestino != "OK":
                    return resultadoDestino

            transaccion = Transaccion(datos["tipo"])

            if tipoEsperado == "solicitud" and not transaccion.validarSolicitud():
                return {"status": "ERROR", "motivo": 4}

            if tipoEsperado == "saldo" and not transaccion.validarSaldo():
                return {"status": "ERROR", "motivo": 4}

            if tipoEsperado == "llamada" and not transaccion.validarLlamada():
                return {"status": "ERROR", "motivo": 4}

            if tipoEsperado == "finalizacion" and not transaccion.validarFinalizacion():
                return {"status": "ERROR", "motivo": 4}

            return {"status": "OK"}

        except Exception as error:
            try:
                from Capa_Negocio.Bitacora import Bitacora
                Bitacora.registrar("ERROR", {
                    "componente": "Validador",
                    "tipoEsperado": tipoEsperado,
                    "mensaje": str(error)
                })
            except Exception:
                pass

            return {"status": "ERROR", "motivo": 5}

    def __validarDestino(self, telefonoDestino):
        destino = str(telefonoDestino).replace("+", "").replace("-", "").replace(" ", "").strip()
        destino = destino.replace("(", "").replace(")", "")

        if len(destino) == 8:
            estadoDestino = Telefono(destino).validarTelefono()

            if estadoDestino != "ACTIVO":
                return {"status": "ERROR", "motivo": 1}

            return {"status": "OK"}

        if self.__codigoPaisValido(destino):
            return {"status": "OK"}

        return {"status": "ERROR", "motivo": 5, "detalle": "codigo_pais_invalido"}

    def __codigoPaisValido(self, destino):
        codigosValidos = [
            "1", "20", "34", "44", "49", "502", "503", "504", "505", "507",
            "51", "52", "53", "54", "55", "56", "57", "58", "593", "598",
            "61", "65", "66"
        ]

        return any(destino.startswith(codigo) for codigo in codigosValidos)

    def __camposObligatorios(self, datos, campos):
        for campo in campos:
            if campo not in datos:
                return False

            if datos[campo] is None:
                return False

            if len(str(datos[campo]).strip()) == 0:
                return False

        return True
