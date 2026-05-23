package com.spa.manager.usuarios.application.ports.input;

import com.spa.manager.usuarios.application.dto.UsuarioRequest;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;

public interface ActualizarUsuarioUseCase {
    UsuarioResponse actualizar(Integer id, UsuarioRequest request);
}