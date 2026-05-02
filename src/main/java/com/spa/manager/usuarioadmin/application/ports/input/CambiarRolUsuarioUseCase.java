package com.spa.manager.usuarioadmin.application.ports.input;

import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;

public interface CambiarRolUsuarioUseCase {
    UsuarioResponse cambiarRol(Integer idUsuario, Rol nuevoRol);
}