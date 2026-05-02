package com.spa.manager.servicios.infrastructure.output.persistence.adapter;

import com.spa.manager.servicios.application.ports.output.ServicioRepositoryPort;
import com.spa.manager.servicios.domain.model.Servicio;
import com.spa.manager.servicios.infrastructure.output.persistence.mapper.ServicioMapper;
import com.spa.manager.servicios.infrastructure.output.persistence.repository.ServicioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ServicioRepositoryAdapter implements ServicioRepositoryPort {

    private final ServicioJpaRepository jpaRepository;
    private final ServicioMapper mapper;

    public ServicioRepositoryAdapter(ServicioJpaRepository jpaRepository, ServicioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Servicio save(Servicio servicio) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(servicio)));
    }

    @Override
    public Optional<Servicio> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Servicio> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Servicio> buscarPorNombre(String nombre) {
        return jpaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}