package com.spa.manager.usuarioadmin.application.ports.input;

import com.spa.manager.usuarioadmin.application.dto.CrearStaffRequest;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;

public interface CrearStaffUseCase {
    UsuarioResponse crearStaff(CrearStaffRequest request);
}