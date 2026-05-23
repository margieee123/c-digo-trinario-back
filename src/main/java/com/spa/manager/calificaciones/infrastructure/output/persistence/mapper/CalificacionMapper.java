package com.spa.manager.calificaciones.infrastructure.output.persistence.mapper;

import com.spa.manager.calificaciones.domain.model.Calificacion;
import com.spa.manager.calificaciones.infrastructure.output.persistence.entity.CalificacionEntity;
import org.springframework.stereotype.Component;

@Component
public class CalificacionMapper {

    public Calificacion toDomain(CalificacionEntity entity) {
        Calificacion c = new Calificacion();
        c.setIdCalificacion(entity.getIdCalificacion());
        c.setIdReserva(entity.getIdReserva());
        c.setPuntuacion(entity.getPuntuacion());
        c.setComentario(entity.getComentario());
        c.setFecha(entity.getFecha());
        return c;
    }

    public CalificacionEntity toEntity(Calificacion c) {
        CalificacionEntity entity = new CalificacionEntity();
        entity.setIdCalificacion(c.getIdCalificacion());
        entity.setIdReserva(c.getIdReserva());
        entity.setPuntuacion(c.getPuntuacion());
        entity.setComentario(c.getComentario());
        entity.setFecha(c.getFecha());
        return entity;
    }
}