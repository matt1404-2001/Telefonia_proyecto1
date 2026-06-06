/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;

/**
 *
 * @author mathi
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import utilidades.Configuracion;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        String url = Configuracion.obtener(
                "db.url",
                "jdbc:sqlserver://localhost:1433;databaseName=Telefonia;encrypt=true;trustServerCertificate=true"
        );
        String usuario = Configuracion.obtener("db.user", "telefonia_user");
        String password = Configuracion.obtener("db.password", "Telefonia2025!");

        return DriverManager.getConnection(url, usuario, password);
    }
}
