package com.spa.manager.servicios.application.ports.output;

import com.spa.manager.servicios.domain.model.Servicio;
import java.util.List;
import java.util.Optional;

public interface ServicioRepositoryPort {
    Servicio save(Servicio servicio);
    Optional<Servicio> findById(Integer id);
    List<Servicio> findAll();
    void deleteById(Integer id);
    boolean existsById(Integer id);
    List<Servicio> buscarPorNombre(String nombre);
}