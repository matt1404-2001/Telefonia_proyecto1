/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelos;

/**
 *
 * @author mathi
 */
public class Tarifa {
    
    private int tipoLlamada;
    private String destinoGrupo;
    private double costoPorMinuto;
    
    
    //setters y gett
    public int getTipoLlamada() {
        return tipoLlamada;
    }

    public void setTipoLlamada(int tipoLlamada) {
        this.tipoLlamada = tipoLlamada;
    }

    public String getDestinoGrupo() {
        return destinoGrupo;
    }

    public void setDestinoGrupo(String destinoGrupo) {
        this.destinoGrupo = destinoGrupo;
    }

    public double getCostoPorMinuto() {
        return costoPorMinuto;
    }

    public void setCostoPorMinuto(double costoPorMinuto) {
        this.costoPorMinuto = costoPorMinuto;
    }
    
}
