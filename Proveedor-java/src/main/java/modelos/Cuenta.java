/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

/**
 *
 * @author mathi
 */
public class Cuenta {
    private String numeroTelefono;
    private String tipoServicio;
    private double saldo;
    private double bonoMismoProveedor;
    private String proveedor;
    
    
    // Getters y Setters 
    public String getNumeroTelefono() {
        return numeroTelefono;
    }

    public void setNumeroTelefono(String numeroTelefono) {
        this.numeroTelefono = numeroTelefono;
    }

    public String getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(String tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double getBonoMismoProveedor() {
        return bonoMismoProveedor;
    }

    public void setBonoMismoProveedor(double bonoMismoProveedor) {
        this.bonoMismoProveedor = bonoMismoProveedor;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    // Constructores
    public Cuenta() {}
    public Cuenta(String numeroTelefono, String tipoServicio, double saldo,
                  double bonoMismoProveedor, String proveedor) {
        this.numeroTelefono = numeroTelefono;
        this.tipoServicio = tipoServicio;
        this.saldo = saldo;
        this.bonoMismoProveedor = bonoMismoProveedor;
        this.proveedor = proveedor;
    }
    
    public boolean esPostpago() {
        return "POSTPAGO".equalsIgnoreCase(tipoServicio);
    }
}
