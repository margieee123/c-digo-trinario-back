package com.spa.manager.reservas.application.ports.input;

import com.spa.manager.reservas.application.dto.ReservaResponse;
import com.spa.manager.reservas.domain.model.EstadoReserva;

public interface CambiarEstadoReservaUseCase {
    ReservaResponse cambiarEstado(Integer idReserva, EstadoReserva nuevoEstado);
}