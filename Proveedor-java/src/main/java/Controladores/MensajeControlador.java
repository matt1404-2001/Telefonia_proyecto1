package Controladores;

import Servicios.ProveedorServicio;
import org.json.JSONObject;
import utilidades.Formateador;

public class MensajeControlador {
    private final ProveedorServicio servicio = new ProveedorServicio();

    public String procesarMensaje(String mensajeJson) {
        try {
            JSONObject json = new JSONObject(mensajeJson);
            String tipo = json.getString("tipo");

            switch (tipo) {
                case "verificar_saldo":
                    return procesarVerificacionSaldo(json).toString();

                case "registrar_llamada":
                    return procesarRegistroLlamada(json).toString();

                default:
                    return respuestaError().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return respuestaError().toString();
        }
    }

    private JSONObject procesarVerificacionSaldo(JSONObject json) {
        String telefono = json.getString("telefono");
        int tipoTransaccion = json.getInt("tipoTransaccion"); // 1=llamada, 2=consulta
        int tipoLlamada = json.optInt("tipoLlamada", 1);      // 1,2,3
        String telefonoDestino = json.optString("telefonoDestino", "");

        if (tipoTransaccion == 1 && telefonoDestino.isBlank()) {
            return respuestaError();
        }

        return servicio.verificarSaldo(telefono, tipoTransaccion, tipoLlamada, telefonoDestino);
    }

    private JSONObject procesarRegistroLlamada(JSONObject json) {
        String telefonoOrigen = json.getString("telefono");
        int fecha = json.getInt("fecha"); // YYYYMMDD
        int hora = json.getInt("hora");   // HHMMSS
        String telefonoDestino = json.getString("telefonoDestino");
        long costoTotal = json.getLong("costoTotal"); // centesimas
        int duracionSegundos = obtenerDuracionEnSegundos(json);

        return servicio.registrarLlamada(
                telefonoOrigen,
                fecha,
                hora,
                telefonoDestino,
                costoTotal,
                duracionSegundos
        );
    }

    private int obtenerDuracionEnSegundos(JSONObject json) {
        Object valor = json.get("duracion");

        if (valor instanceof Number) {
            return ((Number) valor).intValue();
        }

        String duracion = String.valueOf(valor);
        if (!duracion.matches("\\d{6}")) {
            throw new IllegalArgumentException("La duracion debe venir en formato HHMMSS");
        }

        return Formateador.hhmmssASegundos(duracion);
    }

    private JSONObject respuestaError() {
        return new JSONObject().put("status", "ERROR");
    }
}
