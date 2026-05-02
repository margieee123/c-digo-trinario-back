package com.spa.manager.facturas.application.service;

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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FacturaService implements GenerarFacturaUseCase, ObtenerFacturaUseCase, RegistrarPagoUseCase {

    private final FacturaRepositoryPort facturaRepository;
    private final ReservaRepositoryPort reservaRepository;
    private final ServicioRepositoryPort servicioRepository;

    public FacturaService(FacturaRepositoryPort facturaRepository,
                          ReservaRepositoryPort reservaRepository,
                          ServicioRepositoryPort servicioRepository) {
        this.facturaRepository = facturaRepository;
        this.reservaRepository = reservaRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    public FacturaResponse generar(Integer idReserva) {
        if (facturaRepository.existsByIdReserva(idReserva)) {
            throw new IllegalStateException("Ya existe una factura para esta reserva.");
        }

        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNoEncontradaException("Reserva no encontrada."));

        var servicio = servicioRepository.findById(reserva.getIdServicio())
                .orElseThrow(() -> new IllegalStateException("Servicio no encontrado."));

        Factura factura = new Factura();
        factura.setIdReserva(idReserva);
        factura.setMonto(servicio.getPrecio());
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
        return toResponse(actualizada);
    }

    private FacturaResponse toResponse(Factura f) {
        FacturaResponse r = new FacturaResponse();
        r.setIdFactura(f.getIdFactura());
        r.setIdReserva(f.getIdReserva());
        r.setMonto(f.getMonto());
        r.setFechaEmision(f.getFechaEmision());
        r.setEstadoPago(f.getEstadoPago());
        return r;
    }
}