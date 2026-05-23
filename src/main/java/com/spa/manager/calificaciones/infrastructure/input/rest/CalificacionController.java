package com.spa.manager.calificaciones.infrastructure.input.rest;

import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.calificaciones.application.dto.CalificacionRequest;
import com.spa.manager.calificaciones.application.dto.CalificacionResponse;
import com.spa.manager.calificaciones.application.ports.input.CalificarReservaUseCase;
import com.spa.manager.calificaciones.application.ports.input.ListarCalificacionesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calificaciones")
@Tag(name = "Calificaciones", description = "Rating y comentarios de terapeutas")
@SecurityRequirement(name = "bearerAuth")
public class CalificacionController {

    private final CalificarReservaUseCase calificarUseCase;
    private final ListarCalificacionesUseCase listarUseCase;

    public CalificacionController(CalificarReservaUseCase calificarUseCase,
                                  ListarCalificacionesUseCase listarUseCase) {
        this.calificarUseCase = calificarUseCase;
        this.listarUseCase = listarUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('cliente')")
    @Operation(summary = "Calificar una reserva finalizada")
    public ResponseEntity<CalificacionResponse> calificar(
            @RequestBody CalificacionRequest request,
            @AuthenticationPrincipal UsuarioEntity usuario) {
        return ResponseEntity.ok(calificarUseCase.calificar(request, usuario.getId()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    @Operation(summary = "Listar todas las calificaciones")
    public ResponseEntity<List<CalificacionResponse>> listarTodas() {
        return ResponseEntity.ok(listarUseCase.listarTodas());
    }

    @GetMapping("/terapeuta/{idTerapeuta}")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'terapeuta', 'cliente')")
    @Operation(summary = "Listar calificaciones de un terapeuta")
    public ResponseEntity<List<CalificacionResponse>> listarPorTerapeuta(
            @PathVariable Integer idTerapeuta) {
        return ResponseEntity.ok(listarUseCase.listarPorTerapeuta(idTerapeuta));
    }

    @GetMapping("/terapeuta/{idTerapeuta}/promedio")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'terapeuta', 'cliente')")
    @Operation(summary = "Promedio de calificaciones de un terapeuta")
    public ResponseEntity<Double> promedio(@PathVariable Integer idTerapeuta) {
        return ResponseEntity.ok(listarUseCase.promedioTerapeuta(idTerapeuta));
    }
}