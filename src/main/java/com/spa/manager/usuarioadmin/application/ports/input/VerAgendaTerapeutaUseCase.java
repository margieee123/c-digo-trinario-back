package com.spa.manager.usuarioadmin.application.ports.input;

import com.spa.manager.usuarioadmin.application.dto.AgendaResponse;
import java.time.LocalDate;
import java.util.List;

public interface VerAgendaTerapeutaUseCase {
    List<AgendaResponse> verAgenda(Integer idTerapeuta, LocalDate fecha);
}