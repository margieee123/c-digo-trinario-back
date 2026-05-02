package com.spa.manager.usuarios.application.ports.input;

import com.spa.manager.usuarios.application.dto.UsuarioResponse;
import java.util.List;

public interface ListarUsuariosUseCase {
    List<UsuarioResponse> listar();
}