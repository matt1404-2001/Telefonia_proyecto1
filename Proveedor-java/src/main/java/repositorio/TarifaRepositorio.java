/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package repositorio;

/**
 *
 * @author mathi
 */
import modelos.Tarifa;
import java.sql.*;


public class TarifaRepositorio {
    /**
     * Obtiene la tarifa según tipo de llamada.
     * @param tipoLlamada  1=Mismo prov, 2=Otro prov, 3=Internacional
     * @param tipoDestino  Para nacional: "fijo" o "movil". Para internacional: ignorar.
     * @param prefijo      Solo para internacional: prefijo del país destino (ej: "502")
     */
    public Tarifa obtenerTarifa(int tipoLlamada, String tipoDestino, String prefijo) {
        if (tipoLlamada == 1 || tipoLlamada == 2) {
            // ── Llamada nacional ───────────────────────────────────────
            String sql = "SELECT costo_por_min FROM Tarifas " +
                         "WHERE tipo_llamada = ? AND destino_grupo = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, tipoLlamada);
                stmt.setString(2, tipoDestino);   // "fijo" o "movil"
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Tarifa t = new Tarifa();
                    t.setCostoPorMinuto(rs.getDouble("costo_por_min"));
                    t.setDestinoGrupo(tipoDestino);
                    return t;
                }
            } catch (SQLException e) { e.printStackTrace(); }
        } else if (tipoLlamada == 3) {
            // ── Llamada internacional ──────────────────────────────────
            // 1. Buscar el grupo del prefijo en la BD
            String grupo = obtenerGrupoDesdeBD(prefijo);
            // 2. Buscar la tarifa de ese grupo
            String sql = "SELECT costo_por_min FROM Tarifas " +
                         "WHERE tipo_llamada = 3 AND destino_grupo = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, grupo);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Tarifa t = new Tarifa();
                    t.setCostoPorMinuto(rs.getDouble("costo_por_min"));
                    t.setDestinoGrupo(grupo);
                    return t;
                }
            } catch (SQLException e) { e.printStackTrace(); }
        }
        return null;
    }
    /**
     * Consulta la BD para determinar el grupo tarifario de un prefijo internacional.
     * Si no lo encuentra, devuelve "RESTO".
     */
    private String obtenerGrupoDesdeBD(String prefijo) {
        if (prefijo == null || prefijo.isEmpty()) return "RESTO";
        // Busca primero el prefijo exacto, luego los primeros 3, 2 y 1 dígitos
        // (porque prefijos como "1876" deben encontrarse antes que "1")
        String[] candidatos = {
            prefijo,
            prefijo.length() > 3 ? prefijo.substring(0, 3) : null,
            prefijo.length() > 2 ? prefijo.substring(0, 2) : null,
            prefijo.length() > 1 ? prefijo.substring(0, 1) : null
        };
        String sql = "SELECT gt.nombre_grupo " +
                     "FROM CodigosPais cp " +
                     "JOIN GruposTarifarios gt ON cp.id_grupo = gt.id " +
                     "WHERE cp.prefijo = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String candidato : candidatos) {
                if (candidato == null) continue;
                stmt.setString(1, candidato);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("nombre_grupo");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "RESTO";
    }
}
