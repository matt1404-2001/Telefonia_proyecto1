from Capa_Datos.Conexion import Conexion
from Capa_Negocio.Bitacora import Bitacora


class TarjetaDB:

    def buscarTarjeta(self, telefono):
        try:
            conexion = Conexion().conectar()
            cursor = conexion.cursor()

            sql = """
            SELECT
                t.numero,
                ta.identificador_telefono,
                ta.identificador_tarjeta
            FROM telefono t
            INNER JOIN tarjeta ta
                ON t.id = ta.telefono_id
            WHERE t.numero = %s
            """

            cursor.execute(sql, (telefono,))
            resultado = cursor.fetchone()

            cursor.close()
            conexion.close()

            return resultado
        except Exception as error:
            Bitacora.registrar("ERROR", {
                "componente": "TarjetaDB",
                "mensaje": str(error)
            })
            raise
