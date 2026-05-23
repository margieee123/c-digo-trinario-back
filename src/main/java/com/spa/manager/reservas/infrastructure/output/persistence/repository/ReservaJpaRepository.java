package com.spa.manager.reservas.infrastructure.output.persistence.repository;

import com.spa.manager.reservas.domain.model.EstadoReserva;
import com.spa.manager.reservas.infrastructure.output.persistence.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaJpaRepository extends JpaRepository<ReservaEntity, Integer> {

    List<ReservaEntity> findByIdCliente(Integer idCliente);
    List<ReservaEntity> findByIdTerapeuta(Integer idTerapeuta);

    @Query("""
        SELECT r FROM ReservaEntity r
        WHERE (:idTerapeuta IS NULL OR r.idTerapeuta = :idTerapeuta)
          AND (:estado IS NULL OR r.estado = :estado)
          AND (:fechaInicio IS NULL OR r.fecha >= :fechaInicio)
          AND (:fechaFin IS NULL OR r.fecha <= :fechaFin)
        ORDER BY r.fecha ASC, r.horaInicio ASC
    """)
    List<ReservaEntity> findConFiltros(
            @Param("idTerapeuta") Integer idTerapeuta,
            @Param("estado") EstadoReserva estado,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("""
        SELECT r FROM ReservaEntity r
        WHERE r.estado = com.spa.manager.reservas.domain.model.EstadoReserva.finalizada
        AND r.idReserva NOT IN (
            SELECT f.idReserva FROM com.spa.manager.facturas.infrastructure.output.persistence.entity.FacturaEntity f
        )
        ORDER BY r.fecha DESC
    """)
    List<ReservaEntity> findFinalizadasSinFactura();

    @Query("""
        SELECT COUNT(r) > 0 FROM ReservaEntity r
        WHERE r.idTerapeuta = :idTerapeuta
          AND r.fecha = :fecha
          AND r.estado NOT IN (
              com.spa.manager.reservas.domain.model.EstadoReserva.cancelada,
              com.spa.manager.reservas.domain.model.EstadoReserva.finalizada
          )
          AND r.horaInicio < :horaFin
          AND r.horaFin > :horaInicio
    """)
    boolean existeConflicto(
            @Param("idTerapeuta") Integer idTerapeuta,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin);

    @Query("""
        SELECT COUNT(r) > 0 FROM ReservaEntity r
        WHERE r.idTerapeuta = :idTerapeuta
          AND r.fecha = :fecha
          AND r.idReserva <> :idReservaExcluida
          AND r.estado NOT IN (
              com.spa.manager.reservas.domain.model.EstadoReserva.cancelada,
              com.spa.manager.reservas.domain.model.EstadoReserva.finalizada
          )
          AND r.horaInicio < :horaFin
          AND r.horaFin > :horaInicio
    """)
    boolean existeConflictoExcluyendo(
            @Param("idTerapeuta") Integer idTerapeuta,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin,
            @Param("idReservaExcluida") Integer idReservaExcluida);

    @Query("""
        SELECT r FROM ReservaEntity r
        WHERE r.idTerapeuta = :idTerapeuta
        AND r.fecha = :fecha
        AND r.estado NOT IN (
            com.spa.manager.reservas.domain.model.EstadoReserva.cancelada
        )
    """)
    List<ReservaEntity> findByIdTerapeutaAndFecha(
            @Param("idTerapeuta") Integer idTerapeuta,
            @Param("fecha") LocalDate fecha);

    @Query("""
        SELECT r FROM ReservaEntity r
        WHERE r.idTerapeuta = :idTerapeuta
        AND r.fecha BETWEEN :fechaInicio AND :fechaFin
        AND r.estado NOT IN (
            com.spa.manager.reservas.domain.model.EstadoReserva.cancelada
        )
    """)
    List<ReservaEntity> findByIdTerapeutaAndFechaBetween(
            @Param("idTerapeuta") Integer idTerapeuta,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);

    @Query("""
        SELECT r FROM ReservaEntity r
        WHERE r.fecha BETWEEN :fechaInicio AND :fechaFin
        AND r.estado NOT IN (
            com.spa.manager.reservas.domain.model.EstadoReserva.cancelada
        )
        ORDER BY r.fecha ASC, r.horaInicio ASC
    """)
    List<ReservaEntity> findByFechaBetween(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin);
}