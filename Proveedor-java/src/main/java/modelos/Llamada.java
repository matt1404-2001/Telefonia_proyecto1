/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

/**
 *
 * @author mathi
 */
public class Llamada {
    private String telefonoOrigen;
    private int fecha;   // YYYYMMDD
    private int hora;    // HHMMSS
    private String telefonoDestino;
    private long costoTotal;   // en centesimas
    private int duracionSegundos;

    // constructores
    public Llamada() {}
    public Llamada(String telefonoOrigen, int fecha, int hora, String telefonoDestino, long costoTotal, int duracionSegundos) {
        this.telefonoOrigen = telefonoOrigen;
        this.fecha = fecha;
        this.hora = hora;
        this.telefonoDestino = telefonoDestino;
        this.costoTotal = costoTotal;
        this.duracionSegundos = duracionSegundos;
    }

    public String getTelefonoOrigen() {
        return telefonoOrigen;
    }

    public void setTelefonoOrigen(String telefonoOrigen) {
        this.telefonoOrigen = telefonoOrigen;
    }

    public int getFecha() {
        return fecha;
    }

    public void setFecha(int fecha) {
        this.fecha = fecha;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public String getTelefonoDestino() {
        return telefonoDestino;
    }

    public void setTelefonoDestino(String telefonoDestino) {
        this.telefonoDestino = telefonoDestino;
    }

    public long getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(long costoTotal) {
        this.costoTotal = costoTotal;
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(int duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }
    
    
    
}
