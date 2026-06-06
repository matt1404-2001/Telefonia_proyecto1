from Capa_Datos.Conexion import Conexion


class TarjetaDB:

    def buscarTarjeta(self, telefono):
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
