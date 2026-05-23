package com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity;

import com.spa.manager.usuarioadmin.domain.model.DiaSemana;
import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "Disponibilidad_Terapeuta")
public class DisponibilidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidad")
    private Integer idDisponibilidad;

    @Column(name = "id_terapeuta", nullable = false)
    private Integer idTerapeuta;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DiaSemana diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

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