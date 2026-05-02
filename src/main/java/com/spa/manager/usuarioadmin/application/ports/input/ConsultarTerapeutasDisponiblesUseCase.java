package com.spa.manager.usuarioadmin.application.ports.input;

import com.spa.manager.usuarioadmin.application.dto.TerapeutaDisponibleResponse;
import java.time.LocalDate;
import java.util.List;

public interface ConsultarTerapeutasDisponiblesUseCase {
    List<TerapeutaDisponibleResponse> consultar(Integer idServicio, LocalDate fecha);
}