package com.spa.manager.usuarioadmin.domain.model;

public class TerapeutaServicio {

    private Integer idTerapeuta;
    private Integer idServicio;

    public TerapeutaServicio() {}

    public TerapeutaServicio(Integer idTerapeuta, Integer idServicio) {
        this.idTerapeuta = idTerapeuta;
        this.idServicio = idServicio;
    }

    public Integer getIdTerapeuta() { return idTerapeuta; }
    public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }

    public Integer getIdServicio() { return idServicio; }
    public void setIdServicio(Integer idServicio) { this.idServicio = idServicio; }
}