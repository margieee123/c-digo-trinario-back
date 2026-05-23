package com.spa.manager.facturas.application.ports.input;

import com.spa.manager.facturas.application.dto.FacturaResponse;

public interface AnularFacturaUseCase {
    FacturaResponse anular(Integer idFactura);
}