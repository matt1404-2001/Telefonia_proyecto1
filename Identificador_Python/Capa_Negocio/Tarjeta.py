from Capa_Datos.TarjetaDB import TarjetaDB


class Tarjeta:

    def __init__(self, telefono, identificadorTelefono, identificadorTarjeta):
        self.Telefono = telefono
        self.IdentificadorTelefono = identificadorTelefono
        self.IdentificadorTarjeta = identificadorTarjeta

    @property
    def Telefono(self):
        return self.__Telefono

    @Telefono.setter
    def Telefono(self, valor):
        self.__Telefono = str(valor).strip()

    @property
    def IdentificadorTelefono(self):
        return self.__IdentificadorTelefono

    @IdentificadorTelefono.setter
    def IdentificadorTelefono(self, valor):
        valor = str(valor).strip()

        if len(valor) != 16:
            raise NameError("El identificador debe tener 16 digitos.")

        self.__IdentificadorTelefono = valor

    @property
    def IdentificadorTarjeta(self):
        return self.__IdentificadorTarjeta

    @IdentificadorTarjeta.setter
    def IdentificadorTarjeta(self, valor):
        valor = str(valor).strip()

        if len(valor) != 19:
            raise NameError("El identificador de tarjeta debe tener 19 digitos.")

        self.__IdentificadorTarjeta = valor

    def validarTarjeta(self):
        datos = TarjetaDB().buscarTarjeta(self.__Telefono)

        if datos is None:
            return "TELEFONO NO ENCONTRADO"

        if datos[1] != self.__IdentificadorTelefono:
            return "IDENTIFICADOR TELEFONO INCORRECTO"

        if datos[2] != self.__IdentificadorTarjeta:
            return "IDENTIFICADOR TARJETA INCORRECTO"

        return "DATOS CORRECTOS"
