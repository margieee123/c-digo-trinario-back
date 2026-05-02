package com.spa.manager.facturas.application.ports.input;

import com.spa.manager.facturas.application.dto.FacturaResponse;

public interface GenerarFacturaUseCase {
    FacturaResponse generar(Integer idReserva);
}
