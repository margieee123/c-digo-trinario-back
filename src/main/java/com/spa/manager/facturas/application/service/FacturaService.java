package com.spa.manager.facturas.application.service;

import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import com.spa.manager.facturas.application.dto.FacturaResponse;
import com.spa.manager.facturas.application.ports.input.*;
import com.spa.manager.facturas.application.ports.output.FacturaRepositoryPort;
import com.spa.manager.facturas.domain.exception.FacturaNoEncontradaException;
import com.spa.manager.facturas.domain.model.EstadoPago;
import com.spa.manager.facturas.domain.model.Factura;
import com.spa.manager.reservas.application.ports.output.ReservaRepositoryPort;
import com.spa.manager.reservas.domain.exception.ReservaNoEncontradaException;
import com.spa.manager.reservas.domain.model.Reserva;
import com.spa.manager.servicios.application.ports.output.ServicioRepositoryPort;
import com.spa.manager.shared.email.EmailRequest;
import com.spa.manager.shared.email.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturaService implements GenerarFacturaUseCase, ObtenerFacturaUseCase,
        RegistrarPagoUseCase, AnularFacturaUseCase {

    private static final Logger log = LoggerFactory.getLogger(FacturaService.class);

    private final FacturaRepositoryPort facturaRepository;
    private final ReservaRepositoryPort reservaRepository;
    private final ServicioRepositoryPort servicioRepository;
    private final UsuarioJpaRepository usuarioRepository;
    private final EmailService emailService;

    public FacturaService(FacturaRepositoryPort facturaRepository,
                          ReservaRepositoryPort reservaRepository,
                          ServicioRepositoryPort servicioRepository,
                          UsuarioJpaRepository usuarioRepository,
                          EmailService emailService) {
        this.facturaRepository = facturaRepository;
        this.reservaRepository = reservaRepository;
        this.servicioRepository = servicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.emailService = emailService;
    }

    @Override
    public FacturaResponse generar(Integer idReserva) {
        if (facturaRepository.existsByIdReserva(idReserva)) {
            throw new IllegalStateException("Ya existe una factura para esta reserva.");
        }

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada."));

        BigDecimal montoTotal = reserva.getIdServicios().stream()
                .map(idServicio -> servicioRepository.findById(idServicio)
                        .map(s -> s.getPrecio())
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Factura factura = new Factura();
        factura.setIdReserva(idReserva);
        factura.setMonto(montoTotal);
        factura.setFechaEmision(LocalDate.now());
        factura.setEstadoPago(EstadoPago.pendiente);

        Factura guardada = facturaRepository.save(factura);
        return toResponse(guardada);
    }

    @Override
    public FacturaResponse obtenerPorId(Integer id) {
        Factura factura = facturaRepository.findById(id)
                .orElseThrow(() -> new FacturaNoEncontradaException("Factura no encontrada."));
        return toResponse(factura);
    }

    @Override
    public FacturaResponse obtenerPorReserva(Integer idReserva) {
        Factura factura = facturaRepository.findByIdReserva(idReserva)
                .orElseThrow(() -> new FacturaNoEncontradaException("No existe factura para esta reserva."));
        return toResponse(factura);
    }

    @Override
    public List<FacturaResponse> listarTodas() {
        return facturaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FacturaResponse registrarPago(Integer idFactura) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new FacturaNoEncontradaException("Factura no encontrada."));

        factura.setEstadoPago(EstadoPago.pagado);
        Factura actualizada = facturaRepository.save(factura);

        try {
            reservaRepository.findById(actualizada.getIdReserva()).ifPresent(reserva -> {
                List<String> nombresServicios = reserva.getIdServicios().stream()
                        .map(id -> servicioRepository.findById(id)
                                .map(s -> s.getNombre()).orElse("Servicio"))
                        .collect(Collectors.toList());

                usuarioRepository.findById(reserva.getIdCliente()).ifPresent(usuario ->
                        emailService.enviar(new EmailRequest(
                                usuario.getCorreo(),
                                "Pago confirmado - Spa Manager",
                                buildEmailPagoConfirmado(usuario, actualizada, nombresServicios)
                        ))
                );
            });
        } catch (Exception e) {
            log.warn("No se pudo enviar email de pago: {}", e.getMessage());
        }

        return toResponse(actualizada);
    }

    @Override
    public FacturaResponse anular(Integer idFactura) {
        Factura factura = facturaRepository.findById(idFactura)
                .orElseThrow(() -> new FacturaNoEncontradaException("Factura no encontrada."));

        if (factura.getEstadoPago().equals(EstadoPago.pagado)) {
            throw new IllegalStateException("No se puede anular una factura ya pagada.");
        }

        factura.setEstadoPago(EstadoPago.anulada);
        Factura actualizada = facturaRepository.save(factura);
        return toResponse(actualizada);
    }

    private FacturaResponse toResponse(Factura f) {
        FacturaResponse r = new FacturaResponse();
        r.setIdFactura(f.getIdFactura());
        r.setIdReserva(f.getIdReserva());
        r.setMonto(f.getMonto());
        r.setFechaEmision(f.getFechaEmision());
        r.setEstadoPago(f.getEstadoPago());

        reservaRepository.findById(f.getIdReserva()).ifPresent(reserva -> {
            r.setFechaReserva(reserva.getFecha());
            r.setHoraInicio(reserva.getHoraInicio());
            r.setHoraFin(reserva.getHoraFin());
            r.setEstadoReserva(reserva.getEstado().name());

            usuarioRepository.findById(reserva.getIdCliente())
                    .ifPresent(u -> r.setNombreCliente(u.getNombre()));

            usuarioRepository.findById(reserva.getIdTerapeuta())
                    .ifPresent(u -> r.setNombreTerapeuta(u.getNombre()));

            List<String> nombresServicios = reserva.getIdServicios().stream()
                    .map(idServicio -> servicioRepository.findById(idServicio)
                            .map(s -> s.getNombre()).orElse("Desconocido"))
                    .collect(Collectors.toList());
            r.setNombresServicios(nombresServicios);
        });

        return r;
    }

    private String buildEmailPagoConfirmado(UsuarioEntity usuario, Factura factura, List<String> servicios) {
        String listaServicios = servicios.stream()
                .map(s -> "<li>" + s + "</li>")
                .collect(Collectors.joining());

        return """
            <h2>¡Hola %s!</h2>
            <p>Tu pago ha sido confirmado exitosamente.</p>
            <table>
                <tr><td><b>ID Factura:</b></td><td>#%s</td></tr>
                <tr><td><b>Servicios:</b></td><td><ul>%s</ul></td></tr>
                <tr><td><b>Monto Pagado:</b></td><td>$%s</td></tr>
                <tr><td><b>Fecha de Pago:</b></td><td>%s</td></tr>
                <tr><td><b>Estado:</b></td><td>✓ Pagado</td></tr>
            </table>
            <p>Gracias por tu pago. ¡Esperamos verte pronto!</p>
            <p>Spa Manager</p>
            """.formatted(
                usuario.getNombre(),
                factura.getIdFactura(),
                listaServicios,
                factura.getMonto(),
                factura.getFechaEmision()
        );
    }
}