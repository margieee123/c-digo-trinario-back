package com.spa.manager.usuarioadmin.domain.model;

import java.time.LocalTime;

public class Disponibilidad {

    private Integer idDisponibilidad;
    private Integer idTerapeuta;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Disponibilidad() {}

    public Integer getIdDisponibilidad() { return idDisponibilidad; }
    public void setIdDisponibilidad(Integer idDisponibilidad) { this.idDisponibilidad = idDisponibilidad; }

    public Integer getIdTerapeuta() { return idTerapeuta; }
    public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }

    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}