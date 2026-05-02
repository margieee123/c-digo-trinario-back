package com.spa.manager.usuarioadmin.application.dto;

import java.util.List;

public class TerapeutaDisponibleResponse {

    private Integer idTerapeuta;
    private String nombre;
    private List<String> horariosDisponibles;

    public Integer getIdTerapeuta() { return idTerapeuta; }
    public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public List<String> getHorariosDisponibles() { return horariosDisponibles; }
    public void setHorariosDisponibles(List<String> horariosDisponibles) { this.horariosDisponibles = horariosDisponibles; }
}