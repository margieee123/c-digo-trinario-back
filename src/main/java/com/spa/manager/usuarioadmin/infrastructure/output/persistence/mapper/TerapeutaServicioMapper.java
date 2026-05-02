package com.spa.manager.usuarioadmin.infrastructure.output.persistence.mapper;

import com.spa.manager.usuarioadmin.domain.model.TerapeutaServicio;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity.TerapeutaServicioEntity;
import org.springframework.stereotype.Component;

@Component
public class TerapeutaServicioMapper {

    public TerapeutaServicio toDomain(TerapeutaServicioEntity entity) {
        return new TerapeutaServicio(entity.getIdTerapeuta(), entity.getIdServicio());
    }

    public TerapeutaServicioEntity toEntity(TerapeutaServicio ts) {
        TerapeutaServicioEntity entity = new TerapeutaServicioEntity();
        entity.setIdTerapeuta(ts.getIdTerapeuta());
        entity.setIdServicio(ts.getIdServicio());
        return entity;
    }
}