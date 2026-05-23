package com.spa.manager.reservas.infrastructure.output.persistence.mapper;

import com.spa.manager.reservas.domain.model.Reserva;
import com.spa.manager.reservas.infrastructure.output.persistence.entity.ReservaEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapper {

    public Reserva toDomain(ReservaEntity entity) {
        Reserva r = new Reserva();
        r.setIdReserva(entity.getIdReserva());
        r.setIdCliente(entity.getIdCliente());
        r.setIdServicios(entity.getIdServicios());
        r.setIdTerapeuta(entity.getIdTerapeuta());
        r.setFecha(entity.getFecha());
        r.setHoraInicio(entity.getHoraInicio());
        r.setHoraFin(entity.getHoraFin());
        r.setEstado(entity.getEstado());
        return r;
    }

    public ReservaEntity toEntity(Reserva r) {
        ReservaEntity entity = new ReservaEntity();
        entity.setIdReserva(r.getIdReserva());
        entity.setIdCliente(r.getIdCliente());
        entity.setIdServicios(r.getIdServicios());
        entity.setIdTerapeuta(r.getIdTerapeuta());
        entity.setFecha(r.getFecha());
        entity.setHoraInicio(r.getHoraInicio());
        entity.setHoraFin(r.getHoraFin());
        entity.setEstado(r.getEstado());
        return entity;
    }
}