package com.spa.manager.calificaciones.application.service;

import com.spa.manager.calificaciones.application.dto.CalificacionRequest;
import com.spa.manager.calificaciones.application.dto.CalificacionResponse;
import com.spa.manager.calificaciones.application.ports.input.CalificarReservaUseCase;
import com.spa.manager.calificaciones.application.ports.input.ListarCalificacionesUseCase;
import com.spa.manager.calificaciones.application.ports.output.CalificacionRepositoryPort;
import com.spa.manager.calificaciones.domain.model.Calificacion;
import com.spa.manager.reservas.application.ports.output.ReservaRepositoryPort;
import com.spa.manager.reservas.domain.exception.ReservaNoEncontradaException;
import com.spa.manager.reservas.domain.model.EstadoReserva;
import com.spa.manager.reservas.domain.model.Reserva;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CalificacionService implements CalificarReservaUseCase, ListarCalificacionesUseCase {

    private final CalificacionRepositoryPort calificacionRepository;
    private final ReservaRepositoryPort reservaRepository;
    private final UsuarioJpaRepository usuarioRepository;

    public CalificacionService(CalificacionRepositoryPort calificacionRepository,
                               ReservaRepositoryPort reservaRepository,
                               UsuarioJpaRepository usuarioRepository) {
        this.calificacionRepository = calificacionRepository;
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public CalificacionResponse calificar(CalificacionRequest request, Integer idCliente) {
        Reserva reserva = reservaRepository.findById(request.getIdReserva())
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada."));

        if (!reserva.getIdCliente().equals(idCliente)) {
            throw new IllegalArgumentException("No puedes calificar una reserva que no es tuya.");
        }
        if (!reserva.getEstado().equals(EstadoReserva.finalizada)) {
            throw new IllegalStateException("Solo puedes calificar reservas finalizadas.");
        }
        if (calificacionRepository.existsByIdReserva(request.getIdReserva())) {
            throw new IllegalStateException("Esta reserva ya fue calificada.");
        }
        if (request.getPuntuacion() < 1 || request.getPuntuacion() > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }

        Calificacion calificacion = new Calificacion();
        calificacion.setIdReserva(request.getIdReserva());
        calificacion.setPuntuacion(request.getPuntuacion());
        calificacion.setComentario(request.getComentario());
        calificacion.setFecha(LocalDate.now());

        Calificacion guardada = calificacionRepository.save(calificacion);
        return toResponse(guardada, reserva);
    }

    @Override
    public List<CalificacionResponse> listarTodas() {
        return calificacionRepository.findAll().stream()
                .map(c -> {
                    Reserva reserva = reservaRepository.findById(c.getIdReserva()).orElse(null);
                    return toResponse(c, reserva);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CalificacionResponse> listarPorTerapeuta(Integer idTerapeuta) {
        return calificacionRepository.findByIdTerapeuta(idTerapeuta).stream()
                .map(c -> {
                    Reserva reserva = reservaRepository.findById(c.getIdReserva()).orElse(null);
                    return toResponse(c, reserva);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Double promedioTerapeuta(Integer idTerapeuta) {
        return calificacionRepository.promedioByIdTerapeuta(idTerapeuta);
    }

    private CalificacionResponse toResponse(Calificacion c, Reserva reserva) {
        CalificacionResponse r = new CalificacionResponse();
        r.setIdCalificacion(c.getIdCalificacion());
        r.setIdReserva(c.getIdReserva());
        r.setPuntuacion(c.getPuntuacion());
        r.setComentario(c.getComentario());
        r.setFecha(c.getFecha());

        if (reserva != null) {
            usuarioRepository.findById(reserva.getIdCliente())
                    .ifPresent(u -> r.setNombreCliente(u.getNombre()));
            usuarioRepository.findById(reserva.getIdTerapeuta())
                    .ifPresent(u -> r.setNombreTerapeuta(u.getNombre()));
        }

        return r;
    }
}