package com.spa.manager.calificaciones.application.ports.output;

import com.spa.manager.calificaciones.domain.model.Calificacion;
import java.util.List;
import java.util.Optional;

public interface CalificacionRepositoryPort {
    Calificacion save(Calificacion calificacion);
    Optional<Calificacion> findById(Integer id);
    Optional<Calificacion> findByIdReserva(Integer idReserva);
    List<Calificacion> findAll();
    List<Calificacion> findByIdTerapeuta(Integer idTerapeuta);
    boolean existsByIdReserva(Integer idReserva);
    Double promedioByIdTerapeuta(Integer idTerapeuta);
}