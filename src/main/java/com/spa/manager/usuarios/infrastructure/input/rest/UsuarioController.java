package com.spa.manager.usuarios.infrastructure.input.rest;

import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.usuarios.application.dto.CambiarPasswordRequest;
import com.spa.manager.usuarios.application.dto.UsuarioRequest;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;
import com.spa.manager.usuarios.application.ports.input.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final ObtenerUsuarioUseCase obtenerUsuarioUseCase;
    private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
    private final ObtenerPerfilUseCase obtenerPerfilUseCase;
    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
    private final CambiarPasswordUseCase cambiarPasswordUseCase;
    private final RecuperarPasswordUseCase recuperarPasswordUseCase;

    public UsuarioController(ListarUsuariosUseCase listarUsuariosUseCase,
                             ObtenerUsuarioUseCase obtenerUsuarioUseCase,
                             CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase,
                             ObtenerPerfilUseCase obtenerPerfilUseCase,
                             CrearUsuarioUseCase crearUsuarioUseCase,
                             ActualizarUsuarioUseCase actualizarUsuarioUseCase,
                             EliminarUsuarioUseCase eliminarUsuarioUseCase,
                             CambiarPasswordUseCase cambiarPasswordUseCase,
                             RecuperarPasswordUseCase recuperarPasswordUseCase) {
        this.listarUsuariosUseCase = listarUsuariosUseCase;
        this.obtenerUsuarioUseCase = obtenerUsuarioUseCase;
        this.cambiarEstadoUsuarioUseCase = cambiarEstadoUsuarioUseCase;
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
        this.crearUsuarioUseCase = crearUsuarioUseCase;
        this.actualizarUsuarioUseCase = actualizarUsuarioUseCase;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
        this.cambiarPasswordUseCase = cambiarPasswordUseCase;
        this.recuperarPasswordUseCase = recuperarPasswordUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(listarUsuariosUseCase.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(obtenerUsuarioUseCase.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<UsuarioResponse> crear(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(crearUsuarioUseCase.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Integer id,
                                                      @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(actualizarUsuarioUseCase.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        eliminarUsuarioUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<UsuarioResponse> cambiarEstado(@PathVariable Integer id,
                                                         @RequestParam Estado estado) {
        return ResponseEntity.ok(cambiarEstadoUsuarioUseCase.cambiarEstado(id, estado));
    }

    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponse> perfil(Authentication authentication) {
        return ResponseEntity.ok(obtenerPerfilUseCase.obtenerPerfil(authentication.getName()));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAnyRole('administrador', 'recepcionista')")
    public ResponseEntity<List<UsuarioResponse>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(listarUsuariosUseCase.buscarPorNombre(nombre));
    }

    @PatchMapping("/cambiar-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cambiarPassword(
            @RequestBody CambiarPasswordRequest request,
            Authentication authentication) {
        cambiarPasswordUseCase.cambiarPassword(
                authentication.getName(),
                request.getPasswordActual(),
                request.getPasswordNueva()
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<Void> recuperarPassword(@RequestBody Map<String, String> body) {
        recuperarPasswordUseCase.recuperarPassword(body.get("correo"));
        return ResponseEntity.noContent().build();
    }
}