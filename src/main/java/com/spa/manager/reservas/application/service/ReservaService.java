package com.spa.manager.reservas.application.service;

import com.spa.manager.logs.application.ports.input.RegistrarLogUseCase;
import com.spa.manager.logs.domain.model.TipoLog;
import com.spa.manager.reservas.application.dto.*;
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
import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.shared.email.EmailService;
import com.spa.manager.shared.email.EmailRequest;
import com.spa.manager.facturas.application.ports.output.FacturaRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.spa.manager.logs.domain.model.Log;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservaService implements CrearReservaUseCase, ListarReservasUseCase,
        ObtenerReservaUseCase, CambiarEstadoReservaUseCase, ActualizarServiciosReservaUseCase,
        BuscarTerapeutaDisponibleUseCase, ConsultarDisponibilidadSemanaUseCase,
        ActualizarReservaUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepositoryPort reservaRepository;
    private final ServicioRepositoryPort servicioRepository;
    private final UsuarioJpaRepository usuarioRepository;
    private final RegistrarLogUseCase logService;
    private final EmailService emailService;
    private final FacturaRepositoryPort facturaRepository;

    public ReservaService(ReservaRepositoryPort reservaRepository,
                          ServicioRepositoryPort servicioRepository,
                          UsuarioJpaRepository usuarioRepository,
                          RegistrarLogUseCase logService,
                          EmailService emailService,
                          FacturaRepositoryPort facturaRepository) {
        this.reservaRepository = reservaRepository;
        this.servicioRepository = servicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.logService = logService;
        this.emailService = emailService;
        this.facturaRepository = facturaRepository;
    }

    @Override
    public ReservaResponse crear(ReservaRequest request, Integer idCliente) {
        if (request.getIdServicios() == null || request.getIdServicios().isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos un servicio.");
        }

        List<Servicio> servicios = request.getIdServicios().stream()
                .map(id -> servicioRepository.findById(id)
                        .orElseThrow(() -> new ServicioNoEncontradoException("Servicio no encontrado: " + id)))
                .collect(Collectors.toList());

        int duracionTotal = servicios.stream()
                .mapToInt(Servicio::getDuracionMinutos)
                .sum();

        LocalTime horaFin = request.getHoraInicio().plusMinutes(duracionTotal);

        boolean conflicto = reservaRepository.existeConflicto(
                request.getIdTerapeuta(), request.getFecha(),
                request.getHoraInicio(), horaFin);

        if (conflicto) {
            throw new HorarioNoDisponibleException(
                    "El terapeuta no está disponible en el horario solicitado.");
        }

        Reserva reserva = new Reserva();
        reserva.setIdCliente(idCliente);
        reserva.setIdServicios(request.getIdServicios());
        reserva.setIdTerapeuta(request.getIdTerapeuta());
        reserva.setFecha(request.getFecha());
        reserva.setHoraInicio(request.getHoraInicio());
        reserva.setHoraFin(horaFin);
        reserva.setEstado(EstadoReserva.pendiente);

        Reserva guardada = reservaRepository.save(reserva);

        Log logEntry = new Log();
        logEntry.setTipo(TipoLog.ACCION_USUARIO);
        logEntry.setIdUsuario(idCliente);
        logEntry.setDescripcion("Reserva creada con id: " + guardada.getIdReserva());
        logService.registrar(logEntry);

        try {
            usuarioRepository.findById(idCliente).ifPresent(usuario ->
                    emailService.enviar(new EmailRequest(
                            usuario.getCorreo(),
                            "Reserva confirmada - Spa Manager",
                            buildEmailReservaCreada(usuario, guardada, servicios)
                    ))
            );
        } catch (Exception e) {
            log.warn("No se pudo enviar email de confirmación: {}", e.getMessage());
        }

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
    public List<ReservaResponse> listarConFiltros(Integer idTerapeuta, EstadoReserva estado,
                                                  LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaRepository.findConFiltros(idTerapeuta, estado, fechaInicio, fechaFin)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservaResponse> listarFinalizadasSinFactura() {
        return reservaRepository.findFinalizadasSinFactura().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReservaResponse actualizarServicios(Integer idReserva, List<Integer> idServicios) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada."));

        if (idServicios == null || idServicios.isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos un servicio.");
        }

        int duracionTotal = idServicios.stream()
                .mapToInt(id -> servicioRepository.findById(id)
                        .map(Servicio::getDuracionMinutos)
                        .orElse(0))
                .sum();

        reserva.setIdServicios(idServicios);
        reserva.setHoraFin(reserva.getHoraInicio().plusMinutes(duracionTotal));

        Reserva actualizada = reservaRepository.save(reserva);

        facturaRepository.findByIdReserva(idReserva).ifPresent(factura -> {
            if (factura.getEstadoPago().name().equals("pendiente")) {
                BigDecimal nuevoMonto = idServicios.stream()
                        .map(id -> servicioRepository.findById(id)
                                .map(s -> s.getPrecio())
                                .orElse(BigDecimal.ZERO))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                factura.setMonto(nuevoMonto);
                facturaRepository.save(factura);
            }
        });

        Log logEntry = new Log();
        logEntry.setTipo(TipoLog.ACCION_USUARIO);
        logEntry.setDescripcion("Servicios de reserva " + idReserva + " actualizados.");
        logService.registrar(logEntry);

        return toResponse(actualizada);
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

        Log logEntry = new Log();
        logEntry.setTipo(TipoLog.ACCION_USUARIO);
        logEntry.setDescripcion("Estado de reserva " + idReserva + " cambiado a: " + nuevoEstado);
        logService.registrar(logEntry);

        try {
            if (nuevoEstado.equals(EstadoReserva.cancelada)) {
                usuarioRepository.findById(reserva.getIdCliente()).ifPresent(usuario ->
                        emailService.enviar(new EmailRequest(
                                usuario.getCorreo(),
                                "Reserva cancelada - Spa Manager",
                                buildEmailReservaCancelada(usuario, actualizada)
                        ))
                );
            }
        } catch (Exception e) {
            log.warn("No se pudo enviar email de cancelación: {}", e.getMessage());
        }

        return toResponse(actualizada);
    }

    @Override
    public Optional<TerapeutaDisponibleResponse> buscarMejorTerapeuta(
            LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {

        List<UsuarioEntity> terapeutas = usuarioRepository.findAll().stream()
                .filter(u -> u.getRol().name().equals("terapeuta") && u.getEstado().name().equals("activo"))
                .collect(Collectors.toList());

        if (terapeutas.isEmpty()) return Optional.empty();

        LocalDate lunes = fecha.minusDays(fecha.getDayOfWeek().getValue() - 1L);
        LocalDate domingo = lunes.plusDays(6);

        List<UsuarioEntity> disponibles = terapeutas.stream()
                .filter(t -> !reservaRepository.existeConflicto(t.getId(), fecha, horaInicio, horaFin))
                .collect(Collectors.toList());

        if (disponibles.isEmpty()) return Optional.empty();

        return disponibles.stream()
                .map(t -> {
                    int citasHoy = reservaRepository.findByIdTerapeutaAndFecha(t.getId(), fecha).size();
                    int citasSemana = reservaRepository.findByIdTerapeutaAndFechaBetween(t.getId(), lunes, domingo).size();
                    return new TerapeutaDisponibleResponse(t.getId(), t.getNombre(), citasHoy, citasSemana);
                })
                .min(Comparator.comparingInt(TerapeutaDisponibleResponse::getCitasHoy)
                        .thenComparingInt(TerapeutaDisponibleResponse::getCitasSemana))
                .map(Optional::of)
                .orElse(Optional.empty());
    }

    @Override
    public DisponibilidadSemanaResponse consultarDisponibilidad(
            LocalDate fechaInicio, LocalDate fechaFin, int duracionMinutos) {

        List<UsuarioEntity> terapeutas = usuarioRepository.findAll().stream()
                .filter(u -> u.getRol().name().equals("terapeuta") && u.getEstado().name().equals("activo"))
                .collect(Collectors.toList());

        Map<String, Boolean> slots = new HashMap<>();

        LocalDate fechaIteracion = fechaInicio;
        while (!fechaIteracion.isAfter(fechaFin)) {
            final LocalDate fechaActual = fechaIteracion; // ← final para el lambda
            for (int hora = 7; hora <= 20; hora++) {
                final LocalTime horaInicio = LocalTime.of(hora, 0);
                final LocalTime horaFinSlot = horaInicio.plusMinutes(duracionMinutos);
                if (horaFinSlot.isAfter(LocalTime.of(21, 0))) break;

                String key = fechaActual + "T" + String.format("%02d:00", hora);

                boolean hayDisponible = terapeutas.stream()
                        .anyMatch(t -> !reservaRepository.existeConflicto(
                                t.getId(), fechaActual, horaInicio, horaFinSlot));

                slots.put(key, hayDisponible);
            }
            fechaIteracion = fechaIteracion.plusDays(1);
        }

        return new DisponibilidadSemanaResponse(slots);
    }

    @Override
    public ReservaResponse actualizar(Integer idReserva, ActualizarReservaRequest request) {
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada."));

        if (request.getIdServicios() == null || request.getIdServicios().isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos un servicio.");
        }

        int duracionTotal = request.getIdServicios().stream()
                .mapToInt(id -> servicioRepository.findById(id)
                        .map(Servicio::getDuracionMinutos)
                        .orElse(0))
                .sum();

        LocalTime horaFin = request.getHoraInicio().plusMinutes(duracionTotal);

        LocalDate lunes = request.getFecha().minusDays(request.getFecha().getDayOfWeek().getValue() - 1L);
        LocalDate domingo = lunes.plusDays(6);

        List<UsuarioEntity> terapeutas = usuarioRepository.findAll().stream()
                .filter(u -> u.getRol().name().equals("terapeuta") && u.getEstado().name().equals("activo"))
                .collect(Collectors.toList());

        List<UsuarioEntity> disponibles = terapeutas.stream()
                .filter(t -> !reservaRepository.existeConflictoExcluyendo(
                        t.getId(), request.getFecha(), request.getHoraInicio(), horaFin, idReserva))
                .collect(Collectors.toList());

        if (disponibles.isEmpty()) {
            throw new HorarioNoDisponibleException("No hay terapeutas disponibles en el nuevo horario.");
        }

        TerapeutaDisponibleResponse mejorTerapeuta = disponibles.stream()
                .map(t -> {
                    int citasHoy = reservaRepository.findByIdTerapeutaAndFecha(t.getId(), request.getFecha()).size();
                    int citasSemana = reservaRepository.findByIdTerapeutaAndFechaBetween(t.getId(), lunes, domingo).size();
                    return new TerapeutaDisponibleResponse(t.getId(), t.getNombre(), citasHoy, citasSemana);
                })
                .min(Comparator.comparingInt(TerapeutaDisponibleResponse::getCitasHoy)
                        .thenComparingInt(TerapeutaDisponibleResponse::getCitasSemana))
                .orElseThrow(() -> new HorarioNoDisponibleException("No hay terapeutas disponibles."));

        reserva.setIdServicios(request.getIdServicios());
        reserva.setFecha(request.getFecha());
        reserva.setHoraInicio(request.getHoraInicio());
        reserva.setHoraFin(horaFin);
        reserva.setIdTerapeuta(mejorTerapeuta.getIdTerapeuta());

        Reserva actualizada = reservaRepository.save(reserva);

        facturaRepository.findByIdReserva(idReserva).ifPresent(factura -> {
            if (factura.getEstadoPago().name().equals("pendiente")) {
                BigDecimal nuevoMonto = request.getIdServicios().stream()
                        .map(id -> servicioRepository.findById(id)
                                .map(s -> s.getPrecio())
                                .orElse(BigDecimal.ZERO))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                factura.setMonto(nuevoMonto);
                facturaRepository.save(factura);
            }
        });

        Log logEntry = new Log();
        logEntry.setTipo(TipoLog.ACCION_USUARIO);
        logEntry.setDescripcion("Reserva " + idReserva + " actualizada por cliente.");
        logService.registrar(logEntry);

        return toResponse(actualizada);
    }

    private ReservaResponse toResponse(Reserva r) {
        String nombreCliente = usuarioRepository.findById(r.getIdCliente())
                .map(u -> u.getNombre()).orElse(null);
        String nombreTerapeuta = usuarioRepository.findById(r.getIdTerapeuta())
                .map(u -> u.getNombre()).orElse(null);

        List<String> nombresServicios = r.getIdServicios().stream()
                .map(id -> servicioRepository.findById(id)
                        .map(s -> s.getNombre()).orElse("Desconocido"))
                .collect(Collectors.toList());

        double total = r.getIdServicios().stream()
                .mapToDouble(id -> servicioRepository.findById(id)
                        .map(s -> s.getPrecio().doubleValue())
                        .orElse(0.0))
                .sum();

        return new ReservaResponse(
                r.getIdReserva(),
                r.getIdCliente(), nombreCliente,
                r.getIdServicios(), nombresServicios,
                r.getIdTerapeuta(), nombreTerapeuta,
                r.getFecha(),
                r.getHoraInicio(),
                r.getHoraFin(),
                r.getEstado(),
                total
        );
    }

    private String buildEmailReservaCreada(UsuarioEntity usuario, Reserva reserva, List<Servicio> servicios) {
        String listaServicios = servicios.stream()
                .map(s -> "<tr><td>" + s.getNombre() + "</td><td>$" + s.getPrecio() + "</td></tr>")
                .collect(Collectors.joining());

        double total = servicios.stream()
                .mapToDouble(s -> s.getPrecio().doubleValue())
                .sum();

        return """
            <h2>¡Hola %s!</h2>
            <p>Tu reserva ha sido creada exitosamente.</p>
            <table>
                %s
                <tr><td><b>Total:</b></td><td>$%.0f</td></tr>
                <tr><td><b>Fecha:</b></td><td>%s</td></tr>
                <tr><td><b>Hora:</b></td><td>%s - %s</td></tr>
                <tr><td><b>Estado:</b></td><td>Pendiente</td></tr>
            </table>
            <p>Gracias por elegir Spa Manager.</p>
            """.formatted(
                usuario.getNombre(),
                listaServicios,
                total,
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin()
        );
    }

    private String buildEmailReservaCancelada(UsuarioEntity usuario, Reserva reserva) {
        return """
            <h2>¡Hola %s!</h2>
            <p>Tu reserva ha sido cancelada.</p>
            <table>
                <tr><td><b>ID Reserva:</b></td><td>%s</td></tr>
                <tr><td><b>Fecha:</b></td><td>%s</td></tr>
                <tr><td><b>Hora:</b></td><td>%s - %s</td></tr>
            </table>
            <p>Si tienes alguna duda, contáctanos.</p>
            """.formatted(
                usuario.getNombre(),
                reserva.getIdReserva(),
                reserva.getFecha(),
                reserva.getHoraInicio(),
                reserva.getHoraFin()
        );
    }
}