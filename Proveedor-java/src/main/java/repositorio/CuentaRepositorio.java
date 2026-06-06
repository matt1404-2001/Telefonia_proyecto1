/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;

/**
 *
 * @author mathi
 */
import modelos.Cuenta;
import java.sql.*;

public class CuentaRepositorio {

    public Cuenta obtenerPorTelefono(String telefono) {
        String sql = "SELECT numero_telefono, tipo_servicio, saldo, bono_mismo_proveedor, proveedor FROM Cuentas WHERE numero_telefono = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, telefono);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cuenta c = new Cuenta();
                c.setNumeroTelefono(rs.getString("numero_telefono"));
                c.setTipoServicio(rs.getString("tipo_servicio"));
                c.setSaldo(rs.getDouble("saldo"));
                c.setBonoMismoProveedor(rs.getDouble("bono_mismo_proveedor"));
                c.setProveedor(rs.getString("proveedor"));
                return c;
            }
        } catch (SQLException e) {
        }
        return null;
    }

    public boolean actualizarSaldo(String telefono, double nuevoSaldo) {
        String sql = "UPDATE Cuentas SET saldo = ? WHERE numero_telefono = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, nuevoSaldo);
            stmt.setString(2, telefono);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean actualizarSaldoYBono(String telefono, double nuevoSaldo, double nuevoBono) {
        String sql = "UPDATE Cuentas SET saldo = ?, bono_mismo_proveedor = ? WHERE numero_telefono = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, nuevoSaldo);
            stmt.setDouble(2, nuevoBono);
            stmt.setString(3, telefono);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
