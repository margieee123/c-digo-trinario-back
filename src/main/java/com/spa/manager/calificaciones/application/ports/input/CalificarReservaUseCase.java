package com.spa.manager.calificaciones.application.ports.input;

import com.spa.manager.calificaciones.application.dto.CalificacionRequest;
import com.spa.manager.calificaciones.application.dto.CalificacionResponse;

public interface CalificarReservaUseCase {
    CalificacionResponse calificar(CalificacionRequest request, Integer idCliente);
}