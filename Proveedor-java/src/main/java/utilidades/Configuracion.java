package utilidades;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuracion {
    private static final Properties PROPIEDADES = cargarPropiedades();

    private Configuracion() {
    }

    public static String obtener(String clave, String valorPorDefecto) {
        String valorSistema = System.getProperty(clave);
        if (valorSistema != null && !valorSistema.isBlank()) {
            return valorSistema;
        }

        String valor = PROPIEDADES.getProperty(clave);
        if (valor != null && !valor.isBlank()) {
            return valor;
        }

        return valorPorDefecto;
    }

    public static int obtenerEntero(String clave, int valorPorDefecto) {
        String valor = obtener(clave, String.valueOf(valorPorDefecto));
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }

    private static Properties cargarPropiedades() {
        Properties propiedades = new Properties();
        try (InputStream input = Configuracion.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                propiedades.load(input);
            }
        } catch (IOException e) {
            System.out.println("No se pudo cargar config.properties: " + e.getMessage());
        }
        return propiedades;
    }
}
