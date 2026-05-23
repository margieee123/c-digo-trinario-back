package com.spa.manager.reservas.application.dto;

public class TerapeutaDisponibleResponse {
    private Integer idTerapeuta;
    private String nombre;
    private int citasHoy;
    private int citasSemana;

    public TerapeutaDisponibleResponse(Integer idTerapeuta, String nombre, int citasHoy, int citasSemana) {
        this.idTerapeuta = idTerapeuta;
        this.nombre = nombre;
        this.citasHoy = citasHoy;
        this.citasSemana = citasSemana;
    }

    public Integer getIdTerapeuta() { return idTerapeuta; }
    public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getCitasHoy() { return citasHoy; }
    public void setCitasHoy(int citasHoy) { this.citasHoy = citasHoy; }
    public int getCitasSemana() { return citasSemana; }
    public void setCitasSemana(int citasSemana) { this.citasSemana = citasSemana; }
}