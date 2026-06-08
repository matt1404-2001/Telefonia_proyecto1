class Transaccion:

    def __init__(self, tipo):
        self.Tipo = tipo

    @property
    def Tipo(self):
        return self.__Tipo

    @Tipo.setter
    def Tipo(self, valor):
        if valor is None or len(str(valor).strip()) == 0:
            raise NameError("Tipo invalido")

        self.__Tipo = str(valor).strip().lower()

    def validarSolicitud(self):
        return self.__Tipo == "solicitud"

    def validarSaldo(self):
        return self.__Tipo == "saldo"

    def validarLlamada(self):
        return self.__Tipo == "llamada"

    def validarFinalizacion(self):
        return self.__Tipo == "finalizacion"
