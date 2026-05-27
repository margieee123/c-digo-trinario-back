package com.spa.manager.calificaciones.application.service;

import com.spa.manager.calificaciones.application.dto.CalificacionRequest;
import com.spa.manager.calificaciones.application.dto.CalificacionResponse;
import com.spa.manager.calificaciones.application.ports.output.CalificacionRepositoryPort;
import com.spa.manager.calificaciones.domain.model.Calificacion;
import com.spa.manager.reservas.application.ports.output.ReservaRepositoryPort;
import com.spa.manager.reservas.domain.exception.ReservaNoEncontradaException;
import com.spa.manager.reservas.domain.model.EstadoReserva;
import com.spa.manager.reservas.domain.model.Reserva;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - CalificacionService")
class CalificacionServiceTest {

    @Mock private CalificacionRepositoryPort calificacionRepository;
    @Mock private ReservaRepositoryPort reservaRepository;
    @Mock private UsuarioJpaRepository usuarioRepository;

    @InjectMocks private CalificacionService calificacionService;

    private Reserva reservaMock;
    private CalificacionRequest requestMock;

    @BeforeEach
    void setUp() {
        reservaMock = new Reserva();
        reservaMock.setIdReserva(1);
        reservaMock.setIdCliente(1);
        reservaMock.setIdTerapeuta(2);
        reservaMock.setEstado(EstadoReserva.finalizada);
        reservaMock.setFecha(LocalDate.now());
        reservaMock.setHoraInicio(LocalTime.of(9, 0));
        reservaMock.setHoraFin(LocalTime.of(10, 0));

        requestMock = new CalificacionRequest();
        requestMock.setIdReserva(1);
        requestMock.setPuntuacion(5);
        requestMock.setComentario("Excelente servicio");
    }

    @Test
    @DisplayName("calificar debe crear calificacion exitosamente")
    void calificar_debeCrearCalificacionExitosamente() {
        Calificacion calificacionGuardada = new Calificacion();
        calificacionGuardada.setIdCalificacion(1);
        calificacionGuardada.setIdReserva(1);
        calificacionGuardada.setPuntuacion(5);
        calificacionGuardada.setComentario("Excelente servicio");
        calificacionGuardada.setFecha(LocalDate.now());

        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(calificacionRepository.existsByIdReserva(1)).thenReturn(false);
        when(calificacionRepository.save(any())).thenReturn(calificacionGuardada);
        when(usuarioRepository.findById(any())).thenReturn(Optional.empty());

        CalificacionResponse response = calificacionService.calificar(requestMock, 1);

        assertNotNull(response);
        assertEquals(5, response.getPuntuacion());
        verify(calificacionRepository).save(any());
    }

    @Test
    @DisplayName("calificar debe lanzar excepcion si reserva no existe")
    void calificar_reservaNoExiste_debeLanzarExcepcion() {
        when(reservaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ReservaNoEncontradaException.class,
                () -> calificacionService.calificar(requestMock, 1));
    }

    @Test
    @DisplayName("calificar debe lanzar excepcion si reserva no pertenece al cliente")
    void calificar_reservaNoEsDelCliente_debeLanzarExcepcion() {
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));

        assertThrows(IllegalArgumentException.class,
                () -> calificacionService.calificar(requestMock, 99));
    }

    @Test
    @DisplayName("calificar debe lanzar excepcion si reserva no esta finalizada")
    void calificar_reservaNoFinalizada_debeLanzarExcepcion() {
        reservaMock.setEstado(EstadoReserva.pendiente);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));

        assertThrows(IllegalStateException.class,
                () -> calificacionService.calificar(requestMock, 1));
    }

    @Test
    @DisplayName("calificar debe lanzar excepcion si ya fue calificada")
    void calificar_yaFueCalificada_debeLanzarExcepcion() {
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(calificacionRepository.existsByIdReserva(1)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> calificacionService.calificar(requestMock, 1));
    }

    @Test
    @DisplayName("calificar debe lanzar excepcion si puntuacion es invalida")
    void calificar_puntuacionInvalida_debeLanzarExcepcion() {
        requestMock.setPuntuacion(6);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(calificacionRepository.existsByIdReserva(1)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> calificacionService.calificar(requestMock, 1));
    }

    @Test
    @DisplayName("listarTodas debe retornar lista de calificaciones")
    void listarTodas_debeRetornarLista() {
        Calificacion c = new Calificacion();
        c.setIdCalificacion(1);
        c.setIdReserva(1);
        c.setPuntuacion(4);

        when(calificacionRepository.findAll()).thenReturn(List.of(c));
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(usuarioRepository.findById(any())).thenReturn(Optional.empty());

        List<CalificacionResponse> result = calificacionService.listarTodas();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("listarPorTerapeuta debe retornar calificaciones del terapeuta")
    void listarPorTerapeuta_debeRetornarCalificaciones() {
        Calificacion c = new Calificacion();
        c.setIdCalificacion(1);
        c.setIdReserva(1);
        c.setPuntuacion(5);

        when(calificacionRepository.findByIdTerapeuta(2)).thenReturn(List.of(c));
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(usuarioRepository.findById(any())).thenReturn(Optional.empty());

        List<CalificacionResponse> result = calificacionService.listarPorTerapeuta(2);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("promedioTerapeuta debe retornar promedio correctamente")
    void promedioTerapeuta_debeRetornarPromedio() {
        when(calificacionRepository.promedioByIdTerapeuta(2)).thenReturn(4.5);

        Double promedio = calificacionService.promedioTerapeuta(2);

        assertEquals(4.5, promedio);
    }
}