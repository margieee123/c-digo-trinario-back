package com.spa.manager.reservas.application.ports.input;

import com.spa.manager.reservas.application.dto.TerapeutaDisponibleResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface BuscarTerapeutaDisponibleUseCase {
    Optional<TerapeutaDisponibleResponse> buscarMejorTerapeuta(
            LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
}