package com.spa.manager.reservas.application.ports.input;

import com.spa.manager.reservas.application.dto.DisponibilidadSemanaResponse;
import java.time.LocalDate;

public interface ConsultarDisponibilidadSemanaUseCase {
    DisponibilidadSemanaResponse consultarDisponibilidad(
            LocalDate fechaInicio, LocalDate fechaFin, int duracionMinutos);
}