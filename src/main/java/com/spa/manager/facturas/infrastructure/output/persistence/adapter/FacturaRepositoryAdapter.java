package com.spa.manager.facturas.infrastructure.output.persistence.adapter;

import com.spa.manager.facturas.application.ports.output.FacturaRepositoryPort;
import com.spa.manager.facturas.domain.model.Factura;
import com.spa.manager.facturas.infrastructure.output.persistence.mapper.FacturaMapper;
import com.spa.manager.facturas.infrastructure.output.persistence.repository.FacturaJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FacturaRepositoryAdapter implements FacturaRepositoryPort {

    private final FacturaJpaRepository jpaRepository;
    private final FacturaMapper mapper;

    public FacturaRepositoryAdapter(FacturaJpaRepository jpaRepository, FacturaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Factura save(Factura factura) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(factura)));
    }

    @Override
    public Optional<Factura> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Factura> findByIdReserva(Integer idReserva) {
        return jpaRepository.findByIdReserva(idReserva).map(mapper::toDomain);
    }

    @Override
    public List<Factura> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByIdReserva(Integer idReserva) {
        return jpaRepository.existsByIdReserva(idReserva);
    }
}