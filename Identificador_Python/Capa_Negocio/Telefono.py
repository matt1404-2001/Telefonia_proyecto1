from Capa_Datos.TelefonoDB import TelefonoDB


class Telefono:

    def __init__(self, numero):
        self.Numero = numero

    @property
    def Numero(self):
        return self.__Numero

    @Numero.setter
    def Numero(self, valor):
        if valor is None or len(str(valor).strip()) == 0:
            raise NameError("El numero no puede quedar vacio.")

        self.__Numero = str(valor).strip()

    def validarTelefono(self):
        telefono = TelefonoDB().buscarTelefono(self.__Numero)

        if telefono is None:
            return "NO EXISTE"

        if telefono[2] == 0:
            return "INACTIVO"

        return "ACTIVO"
