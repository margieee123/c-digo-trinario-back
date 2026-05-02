package com.spa.manager.reservas.infrastructure.output.persistence.adapter;

import com.spa.manager.reservas.application.ports.output.ReservaRepositoryPort;
import com.spa.manager.reservas.domain.model.Reserva;
import com.spa.manager.reservas.infrastructure.output.persistence.mapper.ReservaMapper;
import com.spa.manager.reservas.infrastructure.output.persistence.repository.ReservaJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ReservaRepositoryAdapter implements ReservaRepositoryPort {

    private final ReservaJpaRepository jpaRepository;
    private final ReservaMapper mapper;

    public ReservaRepositoryAdapter(ReservaJpaRepository jpaRepository, ReservaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Reserva save(Reserva reserva) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(reserva)));
    }

    @Override
    public Optional<Reserva> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Reserva> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reserva> findByIdCliente(Integer idCliente) {
        return jpaRepository.findByIdCliente(idCliente).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reserva> findByIdTerapeuta(Integer idTerapeuta) {
        return jpaRepository.findByIdTerapeuta(idTerapeuta).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existeConflicto(Integer idTerapeuta, LocalDate fecha,
                                   LocalTime horaInicio, LocalTime horaFin) {
        return jpaRepository.existeConflicto(idTerapeuta, fecha, horaInicio, horaFin);
    }
}