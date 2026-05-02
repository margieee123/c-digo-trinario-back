package com.spa.manager.servicios.application.ports.input;

import com.spa.manager.servicios.application.dto.ServicioRequest;
import com.spa.manager.servicios.application.dto.ServicioResponse;

public interface ActualizarServicioUseCase {
    ServicioResponse actualizar(Integer id, ServicioRequest request);
}