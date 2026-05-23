package com.spa.manager.reservas.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ActualizarReservaRequest {
    private List<Integer> idServicios;
    private LocalDate fecha;
    private LocalTime horaInicio;

    public List<Integer> getIdServicios() { return idServicios; }
    public void setIdServicios(List<Integer> idServicios) { this.idServicios = idServicios; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }
}