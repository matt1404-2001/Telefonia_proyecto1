package servidor;

import Controladores.MensajeControlador;
import org.json.JSONObject;
import utilidades.BitacoraAsync;
import utilidades.Configuracion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServidor {
    private final int puerto;
    private final BitacoraAsync bitacora;

    public SocketServidor() {
        puerto = Configuracion.obtenerEntero("servidor.puerto", 6000);
        bitacora = BitacoraAsync.obtenerInstancia();
    }

    public void iniciarServidor() {
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado en puerto " + puerto);

            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("Cliente conectado");
                new Thread(() -> manejarCliente(cliente)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void manejarCliente(Socket cliente) {
        try (
                Socket socket = cliente;
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String mensaje = entrada.readLine();

            if (mensaje == null || mensaje.isBlank()) {
                String respuesta = new JSONObject().put("status", "ERROR").toString();
                bitacora.registrarEntrada(String.valueOf(mensaje));
                bitacora.registrarSalida(respuesta);
                salida.println(respuesta);
                return;
            }

            System.out.println("JSON recibido: " + mensaje);
            bitacora.registrarEntrada(mensaje);

            MensajeControlador controlador = new MensajeControlador();
            String respuesta = controlador.procesarMensaje(mensaje);

            bitacora.registrarSalida(respuesta);
            salida.println(respuesta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
