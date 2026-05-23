package com.spa.manager.servicios.application.ports.input;

import com.spa.manager.servicios.application.dto.ServicioResponse;
import com.spa.manager.servicios.domain.model.EstadoServicio;

public interface CambiarEstadoServicioUseCase {
    ServicioResponse cambiarEstado(Integer id, EstadoServicio estado);
}