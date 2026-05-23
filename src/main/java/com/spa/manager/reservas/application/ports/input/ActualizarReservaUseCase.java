package com.spa.manager.reservas.application.ports.input;

import com.spa.manager.reservas.application.dto.ActualizarReservaRequest;
import com.spa.manager.reservas.application.dto.ReservaResponse;

public interface ActualizarReservaUseCase {
    ReservaResponse actualizar(Integer idReserva, ActualizarReservaRequest request);
}