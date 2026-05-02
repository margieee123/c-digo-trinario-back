package com.spa.manager.facturas.infrastructure.input.rest;

import com.spa.manager.facturas.application.dto.FacturaResponse;
import com.spa.manager.facturas.application.ports.input.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facturas")
@Tag(name = "Facturas", description = "Gestión de facturación")
@SecurityRequirement(name = "bearerAuth")
public class FacturaController {

    private final GenerarFacturaUseCase generarFacturaUseCase;
    private final ObtenerFacturaUseCase obtenerFacturaUseCase;
    private final RegistrarPagoUseCase registrarPagoUseCase;

    public FacturaController(GenerarFacturaUseCase generarFacturaUseCase,
                             ObtenerFacturaUseCase obtenerFacturaUseCase,
                             RegistrarPagoUseCase registrarPagoUseCase) {
        this.generarFacturaUseCase = generarFacturaUseCase;
        this.obtenerFacturaUseCase = obtenerFacturaUseCase;
        this.registrarPagoUseCase = registrarPagoUseCase;
    }

    @PostMapping("/reserva/{idReserva}")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    @Operation(summary = "Generar factura para una reserva")
    public ResponseEntity<FacturaResponse> generar(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(generarFacturaUseCase.generar(idReserva));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    @Operation(summary = "Listar todas las facturas")
    public ResponseEntity<List<FacturaResponse>> listarTodas() {
        return ResponseEntity.ok(obtenerFacturaUseCase.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    @Operation(summary = "Obtener factura por id")
    public ResponseEntity<FacturaResponse> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(obtenerFacturaUseCase.obtenerPorId(id));
    }

    @GetMapping("/reserva/{idReserva}")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'cliente')")
    @Operation(summary = "Obtener factura por reserva")
    public ResponseEntity<FacturaResponse> obtenerPorReserva(@PathVariable Integer idReserva) {
        return ResponseEntity.ok(obtenerFacturaUseCase.obtenerPorReserva(idReserva));
    }

    @PatchMapping("/{id}/pagar")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    @Operation(summary = "Registrar pago de una factura")
    public ResponseEntity<FacturaResponse> pagar(@PathVariable Integer id) {
        return ResponseEntity.ok(registrarPagoUseCase.registrarPago(id));
    }
}