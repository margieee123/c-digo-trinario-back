package com.spa.manager.usuarioadmin.infrastructure.output.persistence.adapter;

import com.spa.manager.usuarioadmin.application.ports.output.DisponibilidadRepositoryPort;
import com.spa.manager.usuarioadmin.domain.model.Disponibilidad;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.mapper.DisponibilidadMapper;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.repository.DisponibilidadJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DisponibilidadRepositoryAdapter implements DisponibilidadRepositoryPort {

    private final DisponibilidadJpaRepository jpaRepository;
    private final DisponibilidadMapper mapper;

    public DisponibilidadRepositoryAdapter(DisponibilidadJpaRepository jpaRepository,
                                           DisponibilidadMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Disponibilidad save(Disponibilidad disponibilidad) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(disponibilidad)));
    }

    @Override
    public Optional<Disponibilidad> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Disponibilidad> findByIdTerapeuta(Integer idTerapeuta) {
        return jpaRepository.findByIdTerapeuta(idTerapeuta).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }
}