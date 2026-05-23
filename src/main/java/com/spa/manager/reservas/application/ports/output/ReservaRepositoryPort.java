package com.spa.manager.reservas.application.ports.output;

import com.spa.manager.reservas.domain.model.EstadoReserva;
import com.spa.manager.reservas.domain.model.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservaRepositoryPort {
    Reserva save(Reserva reserva);
    Optional<Reserva> findById(Integer id);
    List<Reserva> findAll();
    List<Reserva> findByIdCliente(Integer idCliente);
    List<Reserva> findByIdTerapeuta(Integer idTerapeuta);
    List<Reserva> findConFiltros(Integer idTerapeuta, EstadoReserva estado,
                                 LocalDate fechaInicio, LocalDate fechaFin);
    List<Reserva> findFinalizadasSinFactura();
    boolean existeConflicto(Integer idTerapeuta, LocalDate fecha,
                            LocalTime horaInicio, LocalTime horaFin);
    boolean existeConflictoExcluyendo(Integer idTerapeuta, LocalDate fecha,
                                      LocalTime horaInicio, LocalTime horaFin,
                                      Integer idReservaExcluida);
    List<Reserva> findByIdTerapeutaAndFecha(Integer idTerapeuta, LocalDate fecha);
    List<Reserva> findByIdTerapeutaAndFechaBetween(Integer idTerapeuta,
                                                   LocalDate fechaInicio,
                                                   LocalDate fechaFin);
    List<Reserva> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
}