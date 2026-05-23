package com.spa.manager.usuarioadmin.application.ports.output;

import com.spa.manager.usuarioadmin.domain.model.Disponibilidad;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadRepositoryPort {
    Disponibilidad save(Disponibilidad disponibilidad);
    Optional<Disponibilidad> findById(Integer id);
    List<Disponibilidad> findByIdTerapeuta(Integer idTerapeuta);
    void deleteById(Integer id);
}