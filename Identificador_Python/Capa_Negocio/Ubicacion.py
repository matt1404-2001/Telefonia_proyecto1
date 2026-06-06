class Ubicacion:

    def __init__(self, latitud, longitud):
        self.Latitud = latitud
        self.Longitud = longitud

    @property
    def Latitud(self):
        return self.__Latitud

    @Latitud.setter
    def Latitud(self, valor):
        self.__Latitud = float(valor)

    @property
    def Longitud(self):
        return self.__Longitud

    @Longitud.setter
    def Longitud(self, valor):
        self.__Longitud = float(valor)

    def validarUbicacion(self):
        return 8 <= self.__Latitud <= 11 and -86 <= self.__Longitud <= -82
