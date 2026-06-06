/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utilidades;

/**
 *
 * @author mathi
 */
public class Formateador {

    // Convierte un numero de segundos a formato HHMMSS (6 dígitos)
    public static String segundosAHHMMSS(int segundos) {
        int horas = segundos / 3600;
        int minutos = (segundos % 3600) / 60;
        int segs = segundos % 60;
        return String.format("%02d%02d%02d", horas, minutos, segs);
    }

    // Convierte una cadena HHMMSS a segundos
    public static int hhmmssASegundos(String hhmmss) {
        if (hhmmss == null || hhmmss.length() != 6) return 0;
        int horas = Integer.parseInt(hhmmss.substring(0, 2));
        int minutos = Integer.parseInt(hhmmss.substring(2, 4));
        int segs = Integer.parseInt(hhmmss.substring(4, 6));
        return horas * 3600 + minutos * 60 + segs;
    }

    // Formatea un costo  a 10 dígitos asumiendo los ultimos 2 como decimales.
    // Ejemplp: 8.72 -> "0000000872"
    public static String formatearTarifa10Digitos(double costo) {
        long entero = Math.round(costo * 100); // centesimas
        return String.format("%010d", entero);
    }

    // Formatea un saldo para consulta (19 digitos, ultimos 2 decimales)
    public static String formatearSaldo19Digitos(double saldo) {
        long entero = Math.round(saldo * 100);
        return String.format("%019d", entero);
    }
}
