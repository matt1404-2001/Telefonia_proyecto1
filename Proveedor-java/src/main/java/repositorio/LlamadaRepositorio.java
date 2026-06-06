/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;

/**
 *
 * @author mathi
 */


import modelos.Llamada;
import java.sql.*;

public class LlamadaRepositorio {

    public boolean registrarLlamada(Llamada llamada) {
        String sql = "INSERT INTO Llamadas (telefono_origen, fecha_llamada, hora_llamada, telefono_destino, costo_total, duracion_segundos) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, llamada.getTelefonoOrigen());
            stmt.setInt(2, llamada.getFecha());
            stmt.setInt(3, llamada.getHora());
            stmt.setString(4, llamada.getTelefonoDestino());
            stmt.setLong(5, llamada.getCostoTotal());
            stmt.setInt(6, llamada.getDuracionSegundos());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}