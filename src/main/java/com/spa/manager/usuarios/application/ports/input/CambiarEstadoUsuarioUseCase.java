package com.spa.manager.usuarios.application.ports.input;

import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;

public interface CambiarEstadoUsuarioUseCase {
    UsuarioResponse cambiarEstado(Integer id, Estado estado);
}