package com.spa.manager.reservas.application.ports.input;

import com.spa.manager.reservas.application.dto.ReservaResponse;

public interface ObtenerReservaUseCase {
    ReservaResponse obtener(Integer id);
}