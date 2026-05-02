package com.spa.manager.reservas.infrastructure.output.persistence.repository;

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
            @Param("horaFin") LocalTime horaFin
    );
}