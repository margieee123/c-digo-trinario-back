package com.spa.manager.usuarios.infrastructure.input.rest;

import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;
import com.spa.manager.usuarios.application.ports.input.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final ListarUsuariosUseCase listarUsuariosUseCase;
    private final ObtenerUsuarioUseCase obtenerUsuarioUseCase;
    private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
    private final ObtenerPerfilUseCase obtenerPerfilUseCase;

    public UsuarioController(ListarUsuariosUseCase listarUsuariosUseCase,
                             ObtenerUsuarioUseCase obtenerUsuarioUseCase,
                             CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase,
                             ObtenerPerfilUseCase obtenerPerfilUseCase) {
        this.listarUsuariosUseCase = listarUsuariosUseCase;
        this.obtenerUsuarioUseCase = obtenerUsuarioUseCase;
        this.cambiarEstadoUsuarioUseCase = cambiarEstadoUsuarioUseCase;
        this.obtenerPerfilUseCase = obtenerPerfilUseCase;
    }

    @GetMapping
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(listarUsuariosUseCase.listar());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('administrador')")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Integer id) {
        return ResponseEntity.ok(obtenerUsuarioUseCase.obtener(id));
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
}