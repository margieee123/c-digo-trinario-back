package com.spa.manager.reservas.application.ports.output;

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
    boolean existeConflicto(Integer idTerapeuta, LocalDate fecha,
                            LocalTime horaInicio, LocalTime horaFin);
}