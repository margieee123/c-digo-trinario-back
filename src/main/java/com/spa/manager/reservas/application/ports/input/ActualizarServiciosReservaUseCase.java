package com.spa.manager.reservas.application.ports.input;

import com.spa.manager.reservas.application.dto.ReservaResponse;
import java.util.List;

public interface ActualizarServiciosReservaUseCase {
    ReservaResponse actualizarServicios(Integer idReserva, List<Integer> idServicios);
}