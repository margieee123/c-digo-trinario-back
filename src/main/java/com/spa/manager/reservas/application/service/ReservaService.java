package com.spa.manager.reservas.application.service;

import com.spa.manager.logs.application.ports.input.RegistrarLogUseCase;
import com.spa.manager.logs.domain.model.TipoLog;
import com.spa.manager.reservas.application.dto.ReservaRequest;
import com.spa.manager.reservas.application.dto.ReservaResponse;
import com.spa.manager.reservas.application.ports.input.*;
import com.spa.manager.reservas.application.ports.output.ReservaRepositoryPort;
import com.spa.manager.reservas.domain.exception.HorarioNoDisponibleException;
import com.spa.manager.reservas.domain.exception.ReservaNoEncontradaException;
import com.spa.manager.reservas.domain.model.EstadoReserva;
import com.spa.manager.reservas.domain.model.Reserva;
import com.spa.manager.servicios.application.ports.output.ServicioRepositoryPort;
import com.spa.manager.servicios.domain.exception.ServicioNoEncontradoException;
import com.spa.manager.servicios.domain.model.Servicio;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Service;
import com.spa.manager.logs.domain.model.Log;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService implements CrearReservaUseCase, ListarReservasUseCase,
        ObtenerReservaUseCase, CambiarEstadoReservaUseCase {

    private final ReservaRepositoryPort reservaRepository;
    private final ServicioRepositoryPort servicioRepository;
    private final UsuarioJpaRepository usuarioRepository;
    private final RegistrarLogUseCase logService;

    public ReservaService(ReservaRepositoryPort reservaRepository,
                          ServicioRepositoryPort servicioRepository,
                          UsuarioJpaRepository usuarioRepository,
                          RegistrarLogUseCase logService) {
        this.reservaRepository = reservaRepository;
        this.servicioRepository = servicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.logService = logService;
    }

    @Override
    public ReservaResponse crear(ReservaRequest request, Integer idCliente) {
        Servicio servicio = servicioRepository.findById(request.getIdServicio())
                .orElseThrow(() -> new ServicioNoEncontradoException("Servicio no encontrado."));

        LocalTime horaFin = request.getHoraInicio().plusMinutes(servicio.getDuracionMinutos());

        boolean conflicto = reservaRepository.existeConflicto(
                request.getIdTerapeuta(), request.getFecha(),
                request.getHoraInicio(), horaFin);

        if (conflicto) {
            throw new HorarioNoDisponibleException(
                    "El terapeuta no está disponible en el horario solicitado.");
        }

        Reserva reserva = new Reserva();
        reserva.setIdCliente(idCliente);
        reserva.setIdServicio(request.getIdServicio());
        reserva.setIdTerapeuta(request.getIdTerapeuta());
        reserva.setFecha(request.getFecha());
        reserva.setHoraInicio(request.getHoraInicio());
        reserva.setHoraFin(horaFin);
        reserva.setEstado(EstadoReserva.pendiente);

        Reserva guardada = reservaRepository.save(reserva);

        Log log = new Log();
        log.setTipo(TipoLog.ACCION_USUARIO);
        log.setIdUsuario(idCliente);
        log.setDescripcion("Reserva creada con id: " + guardada.getIdReserva());
        logService.registrar(log);

        return toResponse(guardada);
    }

    @Override
    public List<ReservaResponse> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservaResponse> listarPorCliente(Integer idCliente) {
        return reservaRepository.findByIdCliente(idCliente).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservaResponse> listarPorTerapeuta(Integer idTerapeuta) {
        return reservaRepository.findByIdTerapeuta(idTerapeuta).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservaResponse obtener(Integer id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada."));
        return toResponse(reserva);
    }

    @Override
    public ReservaResponse cambiarEstado(Integer idReserva, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada."));

        reserva.setEstado(nuevoEstado);
        Reserva actualizada = reservaRepository.save(reserva);

        Log log = new Log();
        log.setTipo(TipoLog.ACCION_USUARIO);
        log.setDescripcion("Estado de reserva " + idReserva + " cambiado a: " + nuevoEstado);
        logService.registrar(log);

        return toResponse(actualizada);
    }

    private ReservaResponse toResponse(Reserva r) {
        String nombreCliente = usuarioRepository.findById(r.getIdCliente())
                .map(u -> u.getNombre()).orElse(null);
        String nombreTerapeuta = usuarioRepository.findById(r.getIdTerapeuta())
                .map(u -> u.getNombre()).orElse(null);
        String nombreServicio = servicioRepository.findById(r.getIdServicio())
                .map(s -> s.getNombre()).orElse(null);

        return new ReservaResponse(
                r.getIdReserva(),
                r.getIdCliente(), nombreCliente,
                r.getIdServicio(), nombreServicio,
                r.getIdTerapeuta(), nombreTerapeuta,
                r.getFecha(),
                r.getHoraInicio(),
                r.getHoraFin(),
                r.getEstado()
        );
    }
}