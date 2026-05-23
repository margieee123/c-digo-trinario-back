package com.spa.manager.servicios.application.ports.input;

import com.spa.manager.servicios.application.dto.ServicioResponse;

public interface ObtenerServicioUseCase {
    ServicioResponse obtener(Integer id);
}