package com.spa.manager.servicios.infrastructure.input.rest;

import com.spa.manager.servicios.application.dto.ServicioRequest;
import com.spa.manager.servicios.application.dto.ServicioResponse;
import com.spa.manager.servicios.application.ports.input.*;
import com.spa.manager.servicios.domain.model.EstadoServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicios")
public class ServicioController {

    private final CrearServicioUseCase crearServicioUseCase;
    private final ListarServiciosUseCase listarServiciosUseCase;
    private final ObtenerServicioUseCase obtenerServicioUseCase;
    private final ActualizarServicioUseCase actualizarServicioUseCase;
    private final EliminarServicioUseCase eliminarServicioUseCase;
    private final CambiarEstadoServicioUseCase cambiarEstadoServicioUseCase;

    public ServicioController(CrearServicioUseCase crearServicioUseCase,
                              ListarServiciosUseCase listarServiciosUseCase,
                              ObtenerServicioUseCase obtenerServicioUseCase,
                              ActualizarServicioUseCase actualizarServicioUseCase,
                              EliminarServicioUseCase eliminarServicioUseCase,
                              CambiarEstadoServicioUseCase cambiarEstadoServicioUseCase) {
        this.crearServicioUseCase = crearServicioUseCase;
        this.listarServiciosUseCase = listarServiciosUseCase;
        this.obtenerServicioUseCase = obtenerServicioUseCase;
        this.actualizarServicioUseCase = actualizarServicioUseCase;
        this.eliminarServicioUseCase = eliminarServicioUseCase;
        this.cambiarEstadoServicioUseCase = cambiarEstadoServicioUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<ServicioResponse> crear(@Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(crearServicioUseCase.crear(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'cliente', 'terapeuta')")
    public ResponseEntity<List<ServicioResponse>> listar(
            @RequestParam(required = false) String nombre) {
        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(listarServiciosUseCase.buscarPorNombre(nombre));
        }
        return ResponseEntity.ok(listarServiciosUseCase.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista', 'cliente', 'terapeuta')")
    public ResponseEntity<ServicioResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(obtenerServicioUseCase.obtener(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<ServicioResponse> actualizar(@PathVariable Integer id,
                                                       @Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.ok(actualizarServicioUseCase.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        eliminarServicioUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<ServicioResponse> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam EstadoServicio estado) {
        return ResponseEntity.ok(cambiarEstadoServicioUseCase.cambiarEstado(id, estado));
    }
}