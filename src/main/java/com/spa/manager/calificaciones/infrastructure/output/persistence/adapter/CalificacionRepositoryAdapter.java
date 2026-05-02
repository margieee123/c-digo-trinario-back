package com.spa.manager.calificaciones.infrastructure.output.persistence.adapter;

import com.spa.manager.calificaciones.application.ports.output.CalificacionRepositoryPort;
import com.spa.manager.calificaciones.domain.model.Calificacion;
import com.spa.manager.calificaciones.infrastructure.output.persistence.mapper.CalificacionMapper;
import com.spa.manager.calificaciones.infrastructure.output.persistence.repository.CalificacionJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CalificacionRepositoryAdapter implements CalificacionRepositoryPort {

    private final CalificacionJpaRepository jpaRepository;
    private final CalificacionMapper mapper;

    public CalificacionRepositoryAdapter(CalificacionJpaRepository jpaRepository,
                                         CalificacionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Calificacion save(Calificacion calificacion) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(calificacion)));
    }

    @Override
    public Optional<Calificacion> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Calificacion> findByIdReserva(Integer idReserva) {
        return jpaRepository.findByIdReserva(idReserva).map(mapper::toDomain);
    }

    @Override
    public List<Calificacion> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Calificacion> findByIdTerapeuta(Integer idTerapeuta) {
        return jpaRepository.findByIdTerapeuta(idTerapeuta).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByIdReserva(Integer idReserva) {
        return jpaRepository.existsByIdReserva(idReserva);
    }

    @Override
    public Double promedioByIdTerapeuta(Integer idTerapeuta) {
        return jpaRepository.promedioByIdTerapeuta(idTerapeuta);
    }
}