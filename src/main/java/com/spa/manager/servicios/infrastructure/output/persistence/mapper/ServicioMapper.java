package com.spa.manager.servicios.infrastructure.output.persistence.mapper;

import com.spa.manager.servicios.domain.model.Servicio;
import com.spa.manager.servicios.infrastructure.output.persistence.entity.ServicioEntity;
import org.springframework.stereotype.Component;

@Component
public class ServicioMapper {

    public Servicio toDomain(ServicioEntity entity) {
        Servicio servicio = new Servicio();
        servicio.setIdServicio(entity.getIdServicio());
        servicio.setNombre(entity.getNombre());
        servicio.setDescripcion(entity.getDescripcion());
        servicio.setPrecio(entity.getPrecio());
        servicio.setDuracionMinutos(entity.getDuracionMinutos());
        servicio.setEstado(entity.getEstado());
        return servicio;
    }

    public ServicioEntity toEntity(Servicio servicio) {
        ServicioEntity entity = new ServicioEntity();
        entity.setIdServicio(servicio.getIdServicio());
        entity.setNombre(servicio.getNombre());
        entity.setDescripcion(servicio.getDescripcion());
        entity.setPrecio(servicio.getPrecio());
        entity.setDuracionMinutos(servicio.getDuracionMinutos());
        entity.setEstado(servicio.getEstado());
        return entity;
    }
}