package com.spa.manager.usuarioadmin.infrastructure.output.persistence.mapper;

import com.spa.manager.usuarioadmin.domain.model.Disponibilidad;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity.DisponibilidadEntity;
import org.springframework.stereotype.Component;

@Component
public class DisponibilidadMapper {

    public Disponibilidad toDomain(DisponibilidadEntity entity) {
        Disponibilidad d = new Disponibilidad();
        d.setIdDisponibilidad(entity.getIdDisponibilidad());
        d.setIdTerapeuta(entity.getIdTerapeuta());
        d.setDiaSemana(entity.getDiaSemana());
        d.setHoraInicio(entity.getHoraInicio());
        d.setHoraFin(entity.getHoraFin());
        return d;
    }

    public DisponibilidadEntity toEntity(Disponibilidad d) {
        DisponibilidadEntity entity = new DisponibilidadEntity();
        entity.setIdDisponibilidad(d.getIdDisponibilidad());
        entity.setIdTerapeuta(d.getIdTerapeuta());
        entity.setDiaSemana(d.getDiaSemana());
        entity.setHoraInicio(d.getHoraInicio());
        entity.setHoraFin(d.getHoraFin());
        return entity;
    }
}