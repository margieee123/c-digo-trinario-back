package com.spa.manager.calificaciones.infrastructure.output.persistence.repository;

import com.spa.manager.calificaciones.infrastructure.output.persistence.entity.CalificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CalificacionJpaRepository extends JpaRepository<CalificacionEntity, Integer> {

    Optional<CalificacionEntity> findByIdReserva(Integer idReserva);

    boolean existsByIdReserva(Integer idReserva);

    @Query("""
        SELECT c FROM CalificacionEntity c
        JOIN ReservaEntity r ON c.idReserva = r.idReserva
        WHERE r.idTerapeuta = :idTerapeuta
    """)
    List<CalificacionEntity> findByIdTerapeuta(@Param("idTerapeuta") Integer idTerapeuta);

    @Query("""
        SELECT AVG(c.puntuacion) FROM CalificacionEntity c
        JOIN ReservaEntity r ON c.idReserva = r.idReserva
        WHERE r.idTerapeuta = :idTerapeuta
    """)
    Double promedioByIdTerapeuta(@Param("idTerapeuta") Integer idTerapeuta);
}