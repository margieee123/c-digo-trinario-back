package com.spa.manager.calificaciones.application.ports.input;

import com.spa.manager.calificaciones.application.dto.CalificacionResponse;
import java.util.List;

public interface ListarCalificacionesUseCase {
    List<CalificacionResponse> listarTodas();
    List<CalificacionResponse> listarPorTerapeuta(Integer idTerapeuta);
    Double promedioTerapeuta(Integer idTerapeuta);
}
