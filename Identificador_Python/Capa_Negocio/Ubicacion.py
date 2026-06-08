class Ubicacion:
    LATITUD_MINIMA = 8.0
    LATITUD_MAXIMA = 11.3
    LONGITUD_MINIMA = -86.2
    LONGITUD_MAXIMA = -82.4

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
        return (
            self.LATITUD_MINIMA <= self.__Latitud <= self.LATITUD_MAXIMA
            and self.LONGITUD_MINIMA <= self.__Longitud <= self.LONGITUD_MAXIMA
        )
