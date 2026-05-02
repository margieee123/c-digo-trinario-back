package com.spa.manager.usuarioadmin.application.dto;

import com.spa.manager.usuarioadmin.domain.model.DiaSemana;
import java.time.LocalTime;

public class DisponibilidadRequest {

    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }
}