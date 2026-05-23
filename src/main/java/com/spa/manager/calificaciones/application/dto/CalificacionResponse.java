package com.spa.manager.calificaciones.application.dto;

import java.time.LocalDate;

public class CalificacionResponse {

    private Integer idCalificacion;
    private Integer idReserva;
    private Integer puntuacion;
    private String comentario;
    private LocalDate fecha;
    private String nombreCliente;
    private String nombreTerapeuta;

    public Integer getIdCalificacion() { return idCalificacion; }
    public void setIdCalificacion(Integer idCalificacion) { this.idCalificacion = idCalificacion; }

    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getNombreTerapeuta() { return nombreTerapeuta; }
    public void setNombreTerapeuta(String nombreTerapeuta) { this.nombreTerapeuta = nombreTerapeuta; }
}