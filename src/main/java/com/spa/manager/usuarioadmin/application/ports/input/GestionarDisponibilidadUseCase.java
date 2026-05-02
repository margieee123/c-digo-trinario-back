package com.spa.manager.usuarioadmin.application.ports.input;

import com.spa.manager.usuarioadmin.application.dto.DisponibilidadRequest;
import com.spa.manager.usuarioadmin.application.dto.DisponibilidadResponse;
import java.util.List;

public interface GestionarDisponibilidadUseCase {
    DisponibilidadResponse agregar(Integer idTerapeuta, DisponibilidadRequest request);
    List<DisponibilidadResponse> listarPorTerapeuta(Integer idTerapeuta);
    void eliminar(Integer idDisponibilidad);
}