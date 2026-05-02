package com.spa.manager.facturas.application.ports.input;

import com.spa.manager.facturas.application.dto.FacturaResponse;
import java.util.List;

public interface ObtenerFacturaUseCase {
    FacturaResponse obtenerPorId(Integer id);
    FacturaResponse obtenerPorReserva(Integer idReserva);
    List<FacturaResponse> listarTodas();
}