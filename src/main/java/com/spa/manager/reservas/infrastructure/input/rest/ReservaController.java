package com.spa.manager.reservas.infrastructure.input.rest;

import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import com.spa.manager.reservas.application.dto.*;
import com.spa.manager.reservas.application.ports.input.*;
import com.spa.manager.reservas.domain.model.EstadoReserva;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private final ActualizarServiciosReservaUseCase actualizarServiciosUseCase;
    private final BuscarTerapeutaDisponibleUseCase buscarTerapeutaUseCase;
    private final ConsultarDisponibilidadSemanaUseCase disponibilidadUseCase;
    private final ActualizarReservaUseCase actualizarReservaUseCase;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public ReservaController(CrearReservaUseCase crearReservaUseCase,
                             ListarReservasUseCase listarReservasUseCase,
                             ObtenerReservaUseCase obtenerReservaUseCase,
                             CambiarEstadoReservaUseCase cambiarEstadoUseCase,
                             ActualizarServiciosReservaUseCase actualizarServiciosUseCase,
                             BuscarTerapeutaDisponibleUseCase buscarTerapeutaUseCase,
                             ConsultarDisponibilidadSemanaUseCase disponibilidadUseCase,
                             ActualizarReservaUseCase actualizarReservaUseCase,
                             UsuarioJpaRepository usuarioJpaRepository) {
        this.crearReservaUseCase = crearReservaUseCase;
        this.listarReservasUseCase = listarReservasUseCase;
        this.obtenerReservaUseCase = obtenerReservaUseCase;
        this.cambiarEstadoUseCase = cambiarEstadoUseCase;
        this.actualizarServiciosUseCase = actualizarServiciosUseCase;
        this.buscarTerapeutaUseCase = buscarTerapeutaUseCase;
        this.disponibilidadUseCase = disponibilidadUseCase;
        this.actualizarReservaUseCase = actualizarReservaUseCase;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Crear una nueva reserva")
    public ResponseEntity<ReservaResponse> crear(
            @RequestBody ReservaRequest request,
            Authentication authentication) {
        Integer idCliente = request.getIdCliente();
        if (idCliente == null) {
            String correo = authentication.getName();
            idCliente = usuarioJpaRepository.findByCorreo(correo)
                    .map(u -> u.getId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        }
        return ResponseEntity.ok(crearReservaUseCase.crear(request, idCliente));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'terapeuta')")
    @Operation(summary = "Listar todas las reservas")
    public ResponseEntity<List<ReservaResponse>> listarTodas() {
        return ResponseEntity.ok(listarReservasUseCase.listarTodas());
    }

    @GetMapping("/filtrar")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'terapeuta')")
    @Operation(summary = "Listar reservas con filtros")
    public ResponseEntity<List<ReservaResponse>> listarConFiltros(
            @RequestParam(required = false) Integer idTerapeuta,
            @RequestParam(required = false) EstadoReserva estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(listarReservasUseCase.listarConFiltros(idTerapeuta, estado, fechaInicio, fechaFin));
    }

    @GetMapping("/finalizadas-sin-factura")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    @Operation(summary = "Listar reservas finalizadas sin factura")
    public ResponseEntity<List<ReservaResponse>> finalizadasSinFactura() {
        return ResponseEntity.ok(listarReservasUseCase.listarFinalizadasSinFactura());
    }

    @GetMapping("/mis-reservas")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Ver mis reservas según rol")
    public ResponseEntity<List<ReservaResponse>> misReservas(Authentication authentication) {
        String correo = authentication.getName();
        var usuario = usuarioJpaRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (usuario.getRol().name().equals("terapeuta")) {
            return ResponseEntity.ok(listarReservasUseCase.listarPorTerapeuta(usuario.getId()));
        }
        return ResponseEntity.ok(listarReservasUseCase.listarPorCliente(usuario.getId()));
    }

    @GetMapping("/terapeuta-disponible")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Buscar terapeuta disponible para una fecha y hora")
    public ResponseEntity<?> buscarTerapeutaDisponible(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime horaInicio,
            @RequestParam int duracionMinutos) {
        LocalTime horaFin = horaInicio.plusMinutes(duracionMinutos);
        return buscarTerapeutaUseCase.buscarMejorTerapeuta(fecha, horaInicio, horaFin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/disponibilidad-semana")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Consultar disponibilidad de slots en una semana")
    public ResponseEntity<DisponibilidadSemanaResponse> disponibilidadSemana(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam int duracionMinutos) {
        return ResponseEntity.ok(disponibilidadUseCase.consultarDisponibilidad(
                fechaInicio, fechaFin, duracionMinutos));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'terapeuta')")
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
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancelar una reserva")
    public ResponseEntity<ReservaResponse> cancelar(@PathVariable Integer id) {
        return ResponseEntity.ok(cambiarEstadoUseCase.cambiarEstado(id, EstadoReserva.cancelada));
    }

    @PatchMapping("/{id}/servicios")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'terapeuta')")
    @Operation(summary = "Actualizar servicios de una reserva")
    public ResponseEntity<ReservaResponse> actualizarServicios(
            @PathVariable Integer id,
            @RequestBody List<Integer> idServicios) {
        return ResponseEntity.ok(actualizarServiciosUseCase.actualizarServicios(id, idServicios));
    }

    @PutMapping("/{id}/cliente")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Actualizar reserva desde el cliente")
    public ResponseEntity<ReservaResponse> actualizarReservaCliente(
            @PathVariable Integer id,
            @RequestBody ActualizarReservaRequest request) {
        return ResponseEntity.ok(actualizarReservaUseCase.actualizar(id, request));
    }
}