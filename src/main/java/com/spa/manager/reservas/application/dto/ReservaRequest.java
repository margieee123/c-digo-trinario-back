package com.spa.manager.reservas.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaRequest {

    private Integer idServicio;
    private Integer idTerapeuta;
    private LocalDate fecha;
    private LocalTime horaInicio;

    public Integer getIdServicio() { return idServicio; }
    public void setIdServicio(Integer idServicio) { this.idServicio = idServicio; }

    public Integer getIdTerapeuta() { return idTerapeuta; }
    public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
}