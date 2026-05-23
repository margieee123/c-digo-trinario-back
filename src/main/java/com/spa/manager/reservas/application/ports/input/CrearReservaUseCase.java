package com.spa.manager.reservas.application.ports.input;

import com.spa.manager.reservas.application.dto.ReservaRequest;
import com.spa.manager.reservas.application.dto.ReservaResponse;

public interface CrearReservaUseCase {
    ReservaResponse crear(ReservaRequest request, Integer idCliente);
}