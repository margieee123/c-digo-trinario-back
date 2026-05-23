package com.spa.manager.reservas.application.ports.input;

import com.spa.manager.reservas.application.dto.ReservaResponse;
import com.spa.manager.reservas.domain.model.EstadoReserva;
import java.time.LocalDate;
import java.util.List;

public interface ListarReservasUseCase {
    List<ReservaResponse> listarTodas();
    List<ReservaResponse> listarPorCliente(Integer idCliente);
    List<ReservaResponse> listarPorTerapeuta(Integer idTerapeuta);
    List<ReservaResponse> listarConFiltros(Integer idTerapeuta, EstadoReserva estado,
                                           LocalDate fechaInicio, LocalDate fechaFin);
    List<ReservaResponse> listarFinalizadasSinFactura();
}