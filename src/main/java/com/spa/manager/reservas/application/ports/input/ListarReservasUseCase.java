package com.spa.manager.reservas.application.ports.input;

import com.spa.manager.reservas.application.dto.ReservaResponse;
import java.util.List;

public interface ListarReservasUseCase {
    List<ReservaResponse> listarTodas();
    List<ReservaResponse> listarPorCliente(Integer idCliente);
    List<ReservaResponse> listarPorTerapeuta(Integer idTerapeuta);
}