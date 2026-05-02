package com.spa.manager.usuarioadmin.application.ports.output;

import com.spa.manager.usuarioadmin.domain.model.TerapeutaServicio;
import java.util.List;

public interface TerapeutaServicioRepositoryPort {
    void save(TerapeutaServicio terapeutaServicio);
    void delete(Integer idTerapeuta, Integer idServicio);
    List<Integer> findServiciosByIdTerapeuta(Integer idTerapeuta);
    List<Integer> findTerapeutasByIdServicio(Integer idServicio);
    boolean exists(Integer idTerapeuta, Integer idServicio);
}