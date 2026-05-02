package com.spa.manager.facturas.application.ports.output;

import com.spa.manager.facturas.domain.model.Factura;
import java.util.List;
import java.util.Optional;

public interface FacturaRepositoryPort {
    Factura save(Factura factura);
    Optional<Factura> findById(Integer id);
    Optional<Factura> findByIdReserva(Integer idReserva);
    List<Factura> findAll();
    boolean existsByIdReserva(Integer idReserva);
}