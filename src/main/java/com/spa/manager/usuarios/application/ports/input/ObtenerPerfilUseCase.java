package com.spa.manager.usuarios.application.ports.input;

import com.spa.manager.usuarios.application.dto.UsuarioResponse;

public interface ObtenerPerfilUseCase {
    UsuarioResponse obtenerPerfil(String correo);
}