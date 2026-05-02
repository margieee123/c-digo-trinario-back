package com.spa.manager.usuarioadmin.application.ports.input;

import com.spa.manager.usuarios.application.dto.UsuarioResponse;
import java.util.List;

public interface ListarStaffUseCase {
    List<UsuarioResponse> listarTerapeutas();
    List<UsuarioResponse> listarRecepcionistas();
}