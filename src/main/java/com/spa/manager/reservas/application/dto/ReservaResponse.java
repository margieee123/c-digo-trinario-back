package com.spa.manager.reservas.application.dto;

import com.spa.manager.reservas.domain.model.EstadoReserva;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservaResponse {

    private Integer idReserva;
    private Integer idCliente;
    private String nombreCliente;
    private Integer idServicio;
    private String nombreServicio;
    private Integer idTerapeuta;
    private String nombreTerapeuta;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private EstadoReserva estado;

    public ReservaResponse() {}

    public ReservaResponse(Integer idReserva, Integer idCliente, String nombreCliente,
                           Integer idServicio, String nombreServicio,
                           Integer idTerapeuta, String nombreTerapeuta,
                           LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                           EstadoReserva estado) {
        this.idReserva = idReserva;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.idServicio = idServicio;
        this.nombreServicio = nombreServicio;
        this.idTerapeuta = idTerapeuta;
        this.nombreTerapeuta = nombreTerapeuta;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public Integer getIdServicio() { return idServicio; }
    public void setIdServicio(Integer idServicio) { this.idServicio = idServicio; }

    public String getNombreServicio() { return nombreServicio; }
    public void setNombreServicio(String nombreServicio) { this.nombreServicio = nombreServicio; }

    public Integer getIdTerapeuta() { return idTerapeuta; }
    public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }

    public String getNombreTerapeuta() { return nombreTerapeuta; }
    public void setNombreTerapeuta(String nombreTerapeuta) { this.nombreTerapeuta = nombreTerapeuta; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
}