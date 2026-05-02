package com.spa.manager.facturas.infrastructure.output.persistence.mapper;

import com.spa.manager.facturas.domain.model.Factura;
import com.spa.manager.facturas.infrastructure.output.persistence.entity.FacturaEntity;
import org.springframework.stereotype.Component;

@Component
public class FacturaMapper {

    public Factura toDomain(FacturaEntity entity) {
        Factura f = new Factura();
        f.setIdFactura(entity.getIdFactura());
        f.setIdReserva(entity.getIdReserva());
        f.setMonto(entity.getMonto());
        f.setFechaEmision(entity.getFechaEmision());
        f.setEstadoPago(entity.getEstadoPago());
        return f;
    }

    public FacturaEntity toEntity(Factura f) {
        FacturaEntity entity = new FacturaEntity();
        entity.setIdFactura(f.getIdFactura());
        entity.setIdReserva(f.getIdReserva());
        entity.setMonto(f.getMonto());
        entity.setFechaEmision(f.getFechaEmision());
        entity.setEstadoPago(f.getEstadoPago());
        return entity;
    }
}