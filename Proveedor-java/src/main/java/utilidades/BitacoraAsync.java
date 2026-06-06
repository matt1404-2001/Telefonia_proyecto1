package utilidades;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BitacoraAsync {
    private static final BitacoraAsync INSTANCIA = new BitacoraAsync();
    private static final DateTimeFormatter FECHA_LINEA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final BlockingQueue<String> cola = new LinkedBlockingQueue<>();
    private final Path archivoBitacora;

    private BitacoraAsync() {
        String ruta = Configuracion.obtener("bitacora.archivo", "bitacora_proveedor.txt");
        archivoBitacora = Paths.get(ruta);

        Thread hilo = new Thread(this::procesarCola, "bitacora-proveedor");
        hilo.setDaemon(true);
        hilo.start();
    }

    public static BitacoraAsync obtenerInstancia() {
        return INSTANCIA;
    }

    public void registrarEntrada(String mensajeJson) {
        registrar("ENTRADA", mensajeJson);
    }

    public void registrarSalida(String mensajeJson) {
        registrar("SALIDA", mensajeJson);
    }

    public void registrar(String direccion, String mensajeJson) {
        JSONObject registro = new JSONObject();
        registro.put("direccion", direccion);
        registro.put("trama", convertirAJsonSiEsPosible(mensajeJson));

        String fecha = LocalDateTime.now().format(FECHA_LINEA);
        cola.offer(fecha + ": " + registro);
    }

    private Object convertirAJsonSiEsPosible(String mensajeJson) {
        try {
            return new JSONObject(mensajeJson);
        } catch (Exception e) {
            return mensajeJson;
        }
    }

    private void procesarCola() {
        while (true) {
            try {
                String linea = cola.take();
                escribirLinea(linea);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void escribirLinea(String linea) {
        try {
            Path padre = archivoBitacora.getParent();
            if (padre != null) {
                Files.createDirectories(padre);
            }

            Files.writeString(
                    archivoBitacora,
                    linea + System.lineSeparator(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.out.println("No se pudo escribir en bitacora: " + e.getMessage());
        }
    }
}
