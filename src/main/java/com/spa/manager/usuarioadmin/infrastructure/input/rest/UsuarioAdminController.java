package com.spa.manager.usuarioadmin.infrastructure.input.rest;

import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.usuarioadmin.application.dto.*;
import com.spa.manager.usuarioadmin.application.ports.input.*;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('administrador')")
@Tag(name = "Admin - Gestión de Staff", description = "Solo administrador")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioAdminController {

    private final CrearStaffUseCase crearStaffUseCase;
    private final GestionarDisponibilidadUseCase disponibilidadUseCase;
    private final GestionarTerapeutaServicioUseCase terapeutaServicioUseCase;
    private final ConsultarTerapeutasDisponiblesUseCase consultarDisponiblesUseCase;
    private final VerAgendaTerapeutaUseCase agendaUseCase;
    private final CambiarRolUsuarioUseCase cambiarRolUseCase;
    private final ListarStaffUseCase listarStaffUseCase;

    public UsuarioAdminController(CrearStaffUseCase crearStaffUseCase,
                                  GestionarDisponibilidadUseCase disponibilidadUseCase,
                                  GestionarTerapeutaServicioUseCase terapeutaServicioUseCase,
                                  ConsultarTerapeutasDisponiblesUseCase consultarDisponiblesUseCase,
                                  VerAgendaTerapeutaUseCase agendaUseCase,
                                  CambiarRolUsuarioUseCase cambiarRolUseCase,
                                  ListarStaffUseCase listarStaffUseCase) {
        this.crearStaffUseCase = crearStaffUseCase;
        this.disponibilidadUseCase = disponibilidadUseCase;
        this.terapeutaServicioUseCase = terapeutaServicioUseCase;
        this.consultarDisponiblesUseCase = consultarDisponiblesUseCase;
        this.agendaUseCase = agendaUseCase;
        this.cambiarRolUseCase = cambiarRolUseCase;
        this.listarStaffUseCase = listarStaffUseCase;
    }

    // ─── STAFF ────────────────────────────────────────────────────────────────

    @PostMapping("/staff")
    @Operation(summary = "Crear terapeuta o recepcionista")
    public ResponseEntity<UsuarioResponse> crearStaff(@RequestBody CrearStaffRequest request) {
        return ResponseEntity.ok(crearStaffUseCase.crearStaff(request));
    }

    @GetMapping("/terapeutas")
    @Operation(summary = "Listar todos los terapeutas")
    public ResponseEntity<List<UsuarioResponse>> listarTerapeutas() {
        return ResponseEntity.ok(listarStaffUseCase.listarTerapeutas());
    }

    @GetMapping("/recepcionistas")
    @Operation(summary = "Listar todos los recepcionistas")
    public ResponseEntity<List<UsuarioResponse>> listarRecepcionistas() {
        return ResponseEntity.ok(listarStaffUseCase.listarRecepcionistas());
    }

    @PatchMapping("/usuarios/{id}/rol")
    @Operation(summary = "Cambiar rol de un usuario")
    public ResponseEntity<UsuarioResponse> cambiarRol(
            @PathVariable Integer id,
            @RequestParam Rol nuevoRol) {
        return ResponseEntity.ok(cambiarRolUseCase.cambiarRol(id, nuevoRol));
    }

    // ─── DISPONIBILIDAD ───────────────────────────────────────────────────────

    @PostMapping("/terapeutas/{idTerapeuta}/disponibilidad")
    @Operation(summary = "Agregar disponibilidad a un terapeuta")
    public ResponseEntity<DisponibilidadResponse> agregarDisponibilidad(
            @PathVariable Integer idTerapeuta,
            @RequestBody DisponibilidadRequest request) {
        return ResponseEntity.ok(disponibilidadUseCase.agregar(idTerapeuta, request));
    }

    @GetMapping("/terapeutas/{idTerapeuta}/disponibilidad")
    @Operation(summary = "Ver disponibilidad de un terapeuta")
    public ResponseEntity<List<DisponibilidadResponse>> verDisponibilidad(
            @PathVariable Integer idTerapeuta) {
        return ResponseEntity.ok(disponibilidadUseCase.listarPorTerapeuta(idTerapeuta));
    }

    @DeleteMapping("/disponibilidad/{idDisponibilidad}")
    @Operation(summary = "Eliminar bloque de disponibilidad")
    public ResponseEntity<Void> eliminarDisponibilidad(@PathVariable Integer idDisponibilidad) {
        disponibilidadUseCase.eliminar(idDisponibilidad);
        return ResponseEntity.noContent().build();
    }

    // ─── TERAPEUTA-SERVICIO ───────────────────────────────────────────────────

    @PostMapping("/terapeutas/{idTerapeuta}/servicios/{idServicio}")
    @Operation(summary = "Asignar servicio a terapeuta")
    public ResponseEntity<Void> asignarServicio(
            @PathVariable Integer idTerapeuta,
            @PathVariable Integer idServicio) {
        terapeutaServicioUseCase.asignarServicio(idTerapeuta, idServicio);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/terapeutas/{idTerapeuta}/servicios/{idServicio}")
    @Operation(summary = "Remover servicio de terapeuta")
    public ResponseEntity<Void> removerServicio(
            @PathVariable Integer idTerapeuta,
            @PathVariable Integer idServicio) {
        terapeutaServicioUseCase.removerServicio(idTerapeuta, idServicio);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/terapeutas/{idTerapeuta}/servicios")
    @Operation(summary = "Ver servicios de un terapeuta")
    public ResponseEntity<List<Integer>> serviciosDeTerapeuta(@PathVariable Integer idTerapeuta) {
        return ResponseEntity.ok(terapeutaServicioUseCase.listarServiciosDeTerapeuta(idTerapeuta));
    }

    @GetMapping("/servicios/{idServicio}/terapeutas")
    @Operation(summary = "Ver terapeutas que atienden un servicio")
    public ResponseEntity<List<Integer>> terapeutasDeServicio(@PathVariable Integer idServicio) {
        return ResponseEntity.ok(terapeutaServicioUseCase.listarTerapeutasDeServicio(idServicio));
    }

    // ─── TERAPEUTAS DISPONIBLES ───────────────────────────────────────────────

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'cliente')")
    @Operation(summary = "Consultar terapeutas disponibles para un servicio y fecha")
    public ResponseEntity<List<TerapeutaDisponibleResponse>> consultarDisponibles(
            @RequestParam Integer idServicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(consultarDisponiblesUseCase.consultar(idServicio, fecha));
    }

    // ─── AGENDA ───────────────────────────────────────────────────────────────

    @GetMapping("/terapeutas/{idTerapeuta}/agenda")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'terapeuta')")
    @Operation(summary = "Ver agenda de un terapeuta por fecha")
    public ResponseEntity<List<AgendaResponse>> verAgenda(
            @PathVariable Integer idTerapeuta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(agendaUseCase.verAgenda(idTerapeuta, fecha));
    }
}