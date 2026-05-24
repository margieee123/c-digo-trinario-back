package com.spa.manager.reservas.application.service;

import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import com.spa.manager.facturas.application.ports.output.FacturaRepositoryPort;
import com.spa.manager.logs.application.ports.input.RegistrarLogUseCase;
import com.spa.manager.reservas.application.dto.ReservaRequest;
import com.spa.manager.reservas.application.dto.ReservaResponse;
import com.spa.manager.reservas.application.dto.TerapeutaDisponibleResponse;
import com.spa.manager.reservas.application.ports.output.ReservaRepositoryPort;
import com.spa.manager.reservas.domain.exception.HorarioNoDisponibleException;
import com.spa.manager.reservas.domain.exception.ReservaNoEncontradaException;
import com.spa.manager.reservas.domain.model.EstadoReserva;
import com.spa.manager.reservas.domain.model.Reserva;
import com.spa.manager.servicios.application.ports.output.ServicioRepositoryPort;
import com.spa.manager.servicios.domain.model.Servicio;
import com.spa.manager.shared.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - ReservaService")
class ReservaServiceTest {

    @Mock private ReservaRepositoryPort reservaRepository;
    @Mock private ServicioRepositoryPort servicioRepository;
    @Mock private UsuarioJpaRepository usuarioRepository;
    @Mock private RegistrarLogUseCase logService;
    @Mock private EmailService emailService;
    @Mock private FacturaRepositoryPort facturaRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Reserva reservaMock;
    private Servicio servicioMock;
    private UsuarioEntity usuarioMock;
    private ReservaRequest requestMock;

    @BeforeEach
    void setUp() {
        servicioMock = new Servicio();
        servicioMock.setIdServicio(1);
        servicioMock.setNombre("Masaje Relajante");
        servicioMock.setPrecio(new BigDecimal("100000"));
        servicioMock.setDuracionMinutos(60);

        usuarioMock = new UsuarioEntity();
        usuarioMock.setIdUsuario(1);
        usuarioMock.setNombre("Cliente Test");
        usuarioMock.setCorreo("cliente@test.com");

        reservaMock = new Reserva();
        reservaMock.setIdReserva(1);
        reservaMock.setIdCliente(1);
        reservaMock.setIdTerapeuta(2);
        reservaMock.setIdServicios(List.of(1));
        reservaMock.setFecha(LocalDate.now().plusDays(1));
        reservaMock.setHoraInicio(LocalTime.of(9, 0));
        reservaMock.setHoraFin(LocalTime.of(10, 0));
        reservaMock.setEstado(EstadoReserva.pendiente);

        requestMock = new ReservaRequest();
        requestMock.setIdTerapeuta(2);
        requestMock.setIdServicios(List.of(1));
        requestMock.setFecha(LocalDate.now().plusDays(1));
        requestMock.setHoraInicio(LocalTime.of(9, 0));
    }

    // ─── CREAR RESERVA ────────────────────────────────────────────

    @Test
    @DisplayName("Crear reserva exitosamente")
    void crear_debeCrearReservaExitosamente() {
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicioMock));
        when(reservaRepository.existeConflicto(any(), any(), any(), any())).thenReturn(false);
        when(reservaRepository.save(any())).thenReturn(reservaMock);
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(usuarioMock));
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicioMock));

        ReservaResponse response = reservaService.crear(requestMock, 1);

        assertNotNull(response);
        assertEquals(1, response.getIdReserva());
        verify(reservaRepository, times(1)).save(any());
        verify(logService, times(1)).registrar(any());
    }

    @Test
    @DisplayName("Crear reserva sin servicios debe lanzar excepcion")
    void crear_sinServicios_debeLanzarExcepcion() {
        requestMock.setIdServicios(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservaService.crear(requestMock, 1));

        assertEquals("Debe seleccionar al menos un servicio.", ex.getMessage());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear reserva con conflicto de horario debe lanzar excepcion")
    void crear_conConflictoHorario_debeLanzarExcepcion() {
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicioMock));
        when(reservaRepository.existeConflicto(any(), any(), any(), any())).thenReturn(true);

        assertThrows(HorarioNoDisponibleException.class,
                () -> reservaService.crear(requestMock, 1));

        verify(reservaRepository, never()).save(any());
    }

    // ─── OBTENER RESERVA ──────────────────────────────────────────

    @Test
    @DisplayName("Obtener reserva por id existente")
    void obtener_reservaExistente_debeRetornarResponse() {
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));
        when(servicioRepository.findById(anyInt())).thenReturn(Optional.of(servicioMock));

        ReservaResponse response = reservaService.obtener(1);

        assertNotNull(response);
        assertEquals(1, response.getIdReserva());
    }

    @Test
    @DisplayName("Obtener reserva por id inexistente debe lanzar excepcion")
    void obtener_reservaInexistente_debeLanzarExcepcion() {
        when(reservaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ReservaNoEncontradaException.class,
                () -> reservaService.obtener(99));
    }

    // ─── CAMBIAR ESTADO ───────────────────────────────────────────

    @Test
    @DisplayName("Cambiar estado de reserva exitosamente")
    void cambiarEstado_debeActualizarEstado() {
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any())).thenReturn(reservaMock);
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));
        when(servicioRepository.findById(anyInt())).thenReturn(Optional.of(servicioMock));

        ReservaResponse response = reservaService.cambiarEstado(1, EstadoReserva.confirmada);

        assertNotNull(response);
        verify(reservaRepository, times(1)).save(any());
        verify(logService, times(1)).registrar(any());
    }

    @Test
    @DisplayName("Cambiar estado a cancelada debe enviar email")
    void cambiarEstado_aCancelada_debeEnviarEmail() {
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(reservaRepository.save(any())).thenReturn(reservaMock);
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));
        when(servicioRepository.findById(anyInt())).thenReturn(Optional.of(servicioMock));

        reservaService.cambiarEstado(1, EstadoReserva.cancelada);

        verify(emailService, times(1)).enviar(any());
    }

    @Test
    @DisplayName("Cambiar estado reserva inexistente debe lanzar excepcion")
    void cambiarEstado_reservaInexistente_debeLanzarExcepcion() {
        when(reservaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ReservaNoEncontradaException.class,
                () -> reservaService.cambiarEstado(99, EstadoReserva.confirmada));
    }

    // ─── LISTAR RESERVAS ──────────────────────────────────────────

    @Test
    @DisplayName("Listar todas las reservas")
    void listarTodas_debeRetornarLista() {
        when(reservaRepository.findAll()).thenReturn(List.of(reservaMock));
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));
        when(servicioRepository.findById(anyInt())).thenReturn(Optional.of(servicioMock));

        List<ReservaResponse> result = reservaService.listarTodas();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Listar reservas por cliente")
    void listarPorCliente_debeRetornarReservasDelCliente() {
        when(reservaRepository.findByIdCliente(1)).thenReturn(List.of(reservaMock));
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));
        when(servicioRepository.findById(anyInt())).thenReturn(Optional.of(servicioMock));

        List<ReservaResponse> result = reservaService.listarPorCliente(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ─── BUSCAR MEJOR TERAPEUTA ───────────────────────────────────

    @Test
    @DisplayName("Buscar mejor terapeuta sin terapeutas activos retorna empty")
    void buscarMejorTerapeuta_sinTerapeutas_retornaEmpty() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        Optional<TerapeutaDisponibleResponse> result = reservaService.buscarMejorTerapeuta(
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Buscar mejor terapeuta con todos ocupados retorna empty")
    void buscarMejorTerapeuta_todosOcupados_retornaEmpty() {
        UsuarioEntity terapeuta = crearTerapeutaMock(2, "Terapeuta Test");
        when(usuarioRepository.findAll()).thenReturn(List.of(terapeuta));
        when(reservaRepository.existeConflicto(any(), any(), any(), any())).thenReturn(true);

        Optional<TerapeutaDisponibleResponse> result = reservaService.buscarMejorTerapeuta(
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Buscar mejor terapeuta retorna el de menor carga")
    void buscarMejorTerapeuta_retornaElDeMenorCarga() {
        UsuarioEntity terapeuta1 = crearTerapeutaMock(2, "Terapeuta 1");
        UsuarioEntity terapeuta2 = crearTerapeutaMock(3, "Terapeuta 2");

        when(usuarioRepository.findAll()).thenReturn(List.of(terapeuta1, terapeuta2));
        when(reservaRepository.existeConflicto(any(), any(), any(), any())).thenReturn(false);
        when(reservaRepository.findByIdTerapeutaAndFecha(eq(2), any())).thenReturn(List.of(reservaMock, reservaMock));
        when(reservaRepository.findByIdTerapeutaAndFecha(eq(3), any())).thenReturn(List.of());
        when(reservaRepository.findByIdTerapeutaAndFechaBetween(any(), any(), any())).thenReturn(List.of());

        Optional<TerapeutaDisponibleResponse> result = reservaService.buscarMejorTerapeuta(
                LocalDate.now(), LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertTrue(result.isPresent());
        assertEquals(3, result.get().getIdTerapeuta()); // terapeuta2 tiene menos carga
    }

    // ─── ACTUALIZAR SERVICIOS ─────────────────────────────────────

    @Test
    @DisplayName("Actualizar servicios de reserva exitosamente")
    void actualizarServicios_debeActualizarCorrectamente() {
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicioMock));
        when(reservaRepository.save(any())).thenReturn(reservaMock);
        when(facturaRepository.findByIdReserva(1)).thenReturn(Optional.empty());
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));

        ReservaResponse response = reservaService.actualizarServicios(1, List.of(1));

        assertNotNull(response);
        verify(reservaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Actualizar servicios con lista vacia debe lanzar excepcion")
    void actualizarServicios_listaVacia_debeLanzarExcepcion() {
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));

        assertThrows(RuntimeException.class,
                () -> reservaService.actualizarServicios(1, List.of()));
    }

    // ─── HELPER ───────────────────────────────────────────────────

    private UsuarioEntity crearTerapeutaMock(int id, String nombre) {
        UsuarioEntity u = new UsuarioEntity();
        u.setIdUsuario(id);
        u.setNombre(nombre);
        u.setCorreo(nombre.toLowerCase().replace(" ", "") + "@test.com");
        u.setRol(com.spa.manager.auth.domain.model.Rol.terapeuta);
        u.setEstado(com.spa.manager.auth.domain.model.Estado.activo);
        return u;
    }
}