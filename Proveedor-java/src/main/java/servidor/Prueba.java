package servidor;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Prueba {
    private static final String HOST = "localhost";
    private static final int PUERTO = 6000;

    public static void main(String[] args) {
        String telefonoOrigen = "88889999";
        String telefonoAlemania = "4915112345678"; // prefijo 49, grupo D

        JSONObject saldoAntes = probar("1. Consultar saldo inicial PREPAGO", new JSONObject()
                .put("tipo", "verificar_saldo")
                .put("telefono", telefonoOrigen)
                .put("tipoTransaccion", 2)
        );

        JSONObject autorizacion = probar("2. Autorizar llamada internacional a Alemania", new JSONObject()
                .put("tipo", "verificar_saldo")
                .put("telefono", telefonoOrigen)
                .put("tipoTransaccion", 1)
                .put("tipoLlamada", 3)
                .put("telefonoDestino", telefonoAlemania)
        );

        if (!"OK".equals(autorizacion.optString("status"))) {
            System.out.println();
            System.out.println("No se registra la llamada porque la autorizacion no fue OK.");
            return;
        }

        long costoUnMinuto = Long.parseLong(autorizacion.getString("tarifa"));

        probar("3. Registrar llamada internacional de 1 minuto", new JSONObject()
                .put("tipo", "registrar_llamada")
                .put("telefono", telefonoOrigen)
                .put("fecha", 20250528)
                .put("hora", 101025)
                .put("telefonoDestino", telefonoAlemania)
                .put("costoTotal", costoUnMinuto)
                .put("duracion", "000100")
        );

        JSONObject saldoDespues = probar("4. Consultar saldo final PREPAGO", new JSONObject()
                .put("tipo", "verificar_saldo")
                .put("telefono", telefonoOrigen)
                .put("tipoTransaccion", 2)
        );

        imprimirResumen(saldoAntes, autorizacion, saldoDespues);
    }

    private static JSONObject probar(String nombrePrueba, JSONObject mensaje) {
        System.out.println();
        System.out.println("------------------------------");
        System.out.println("Prueba: " + nombrePrueba);
        System.out.println("Enviando: " + mensaje);

        try (
                Socket socket = new Socket(HOST, PUERTO);
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()))
        ) {
            salida.println(mensaje);
            String respuesta = entrada.readLine();
            System.out.println("Respuesta: " + respuesta);
            return new JSONObject(respuesta);
        } catch (Exception e) {
            System.out.println("ERROR conectando con Java: " + e.getMessage());
            return new JSONObject().put("status", "ERROR");
        }
    }

    private static void imprimirResumen(JSONObject saldoAntes,
                                        JSONObject autorizacion,
                                        JSONObject saldoDespues) {
        if (!saldoAntes.has("saldo") || !autorizacion.has("tarifa") || !saldoDespues.has("saldo")) {
            return;
        }

        long antes = Long.parseLong(saldoAntes.getString("saldo"));
        long tarifa = Long.parseLong(autorizacion.getString("tarifa"));
        long despues = Long.parseLong(saldoDespues.getString("saldo"));

        System.out.println();
        System.out.println("==============================");
        System.out.println("Resumen de rebajo");
        System.out.println("Saldo antes : " + formatearMonto(antes));
        System.out.println("Tarifa usada: " + formatearMonto(tarifa));
        System.out.println("Saldo final : " + formatearMonto(despues));
        System.out.println("Rebajo real : " + formatearMonto(antes - despues));
        System.out.println("==============================");
    }

    private static String formatearMonto(long centesimas) {
        return String.format("%.2f", centesimas / 100.0);
    }
}
