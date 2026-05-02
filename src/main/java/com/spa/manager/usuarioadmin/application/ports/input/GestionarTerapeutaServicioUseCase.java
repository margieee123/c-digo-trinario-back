package com.spa.manager.usuarioadmin.application.ports.input;

import java.util.List;

public interface GestionarTerapeutaServicioUseCase {
    void asignarServicio(Integer idTerapeuta, Integer idServicio);
    void removerServicio(Integer idTerapeuta, Integer idServicio);
    List<Integer> listarServiciosDeTerapeuta(Integer idTerapeuta);
    List<Integer> listarTerapeutasDeServicio(Integer idServicio);
}