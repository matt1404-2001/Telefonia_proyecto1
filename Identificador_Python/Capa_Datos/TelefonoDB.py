from Capa_Datos.Conexion import Conexion


class TelefonoDB:

    def buscarTelefono(self, numero):
        conexion = Conexion().conectar()
        cursor = conexion.cursor()

        sql = """
        SELECT id, numero, activo, proveedor_id
        FROM telefono
        WHERE numero = %s
        """

        cursor.execute(sql, (numero,))
        resultado = cursor.fetchone()

        cursor.close()
        conexion.close()

        return resultado

    def buscarProveedorId(self, numero):
        telefono = self.buscarTelefono(numero)

        if telefono is None:
            return None

        return telefono[3]
