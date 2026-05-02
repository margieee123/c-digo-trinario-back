package com.spa.manager.reservas.infrastructure.input.rest;

import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.reservas.application.dto.ReservaRequest;
import com.spa.manager.reservas.application.dto.ReservaResponse;
import com.spa.manager.reservas.application.ports.input.*;
import com.spa.manager.reservas.domain.model.EstadoReserva;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas del spa")
@SecurityRequirement(name = "bearerAuth")
public class ReservaController {

    private final CrearReservaUseCase crearReservaUseCase;
    private final ListarReservasUseCase listarReservasUseCase;
    private final ObtenerReservaUseCase obtenerReservaUseCase;
    private final CambiarEstadoReservaUseCase cambiarEstadoUseCase;

    public ReservaController(CrearReservaUseCase crearReservaUseCase,
                             ListarReservasUseCase listarReservasUseCase,
                             ObtenerReservaUseCase obtenerReservaUseCase,
                             CambiarEstadoReservaUseCase cambiarEstadoUseCase) {
        this.crearReservaUseCase = crearReservaUseCase;
        this.listarReservasUseCase = listarReservasUseCase;
        this.obtenerReservaUseCase = obtenerReservaUseCase;
        this.cambiarEstadoUseCase = cambiarEstadoUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('cliente', 'recepcionista', 'administrador')")
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<ReservaResponse> crear(
            @RequestBody ReservaRequest request,
            @AuthenticationPrincipal UsuarioEntity usuario) {
        return ResponseEntity.ok(crearReservaUseCase.crear(request, usuario.getId()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    @Operation(summary = "Listar todas las reservas")
    public ResponseEntity<List<ReservaResponse>> listarTodas() {
        return ResponseEntity.ok(listarReservasUseCase.listarTodas());
    }

    @GetMapping("/mis-reservas")
    @PreAuthorize("hasAnyRole('cliente', 'terapeuta', 'recepcionista', 'administrador')")
    @Operation(summary = "Ver mis reservas según rol")
    public ResponseEntity<List<ReservaResponse>> misReservas(
            @AuthenticationPrincipal UsuarioEntity usuario) {
        if (usuario.getRol().name().equals("terapeuta")) {
            return ResponseEntity.ok(listarReservasUseCase.listarPorTerapeuta(usuario.getId()));
        }
        return ResponseEntity.ok(listarReservasUseCase.listarPorCliente(usuario.getId()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    @Operation(summary = "Obtener una reserva por id")
    public ResponseEntity<ReservaResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(obtenerReservaUseCase.obtener(id));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'terapeuta')")
    @Operation(summary = "Cambiar estado de una reserva")
    public ResponseEntity<ReservaResponse> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam EstadoReserva estado) {
        return ResponseEntity.ok(cambiarEstadoUseCase.cambiarEstado(id, estado));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('cliente', 'administrador', 'recepcionista')")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Integer id) {
        return ResponseEntity.ok(cambiarEstadoUseCase.cambiarEstado(id, EstadoReserva.cancelada));
    }
}