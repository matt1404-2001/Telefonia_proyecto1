from Capa_Datos.Conexion import Conexion
from Capa_Negocio.Bitacora import Bitacora


class TelefonoDB:

    def buscarTelefono(self, numero):
        try:
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
        except Exception as error:
            Bitacora.registrar("ERROR", {
                "componente": "TelefonoDB",
                "mensaje": str(error)
            })
            raise

    def buscarProveedorId(self, numero):
        telefono = self.buscarTelefono(numero)

        if telefono is None:
            return None

        return telefono[3]
