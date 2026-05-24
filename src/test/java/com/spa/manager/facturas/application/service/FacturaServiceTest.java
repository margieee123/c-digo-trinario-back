package com.spa.manager.facturas.application.service;

import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import com.spa.manager.facturas.application.dto.FacturaResponse;
import com.spa.manager.facturas.application.ports.output.FacturaRepositoryPort;
import com.spa.manager.facturas.domain.exception.FacturaNoEncontradaException;
import com.spa.manager.facturas.domain.model.EstadoPago;
import com.spa.manager.facturas.domain.model.Factura;
import com.spa.manager.reservas.application.ports.output.ReservaRepositoryPort;
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
@DisplayName("Pruebas unitarias - FacturaService")
class FacturaServiceTest {

    @Mock private FacturaRepositoryPort facturaRepository;
    @Mock private ReservaRepositoryPort reservaRepository;
    @Mock private ServicioRepositoryPort servicioRepository;
    @Mock private UsuarioJpaRepository usuarioRepository;
    @Mock private EmailService emailService;

    @InjectMocks
    private FacturaService facturaService;

    private Factura facturaMock;
    private Reserva reservaMock;
    private Servicio servicioMock;
    private UsuarioEntity usuarioMock;

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
        reservaMock.setFecha(LocalDate.now());
        reservaMock.setHoraInicio(LocalTime.of(9, 0));
        reservaMock.setHoraFin(LocalTime.of(10, 0));
        reservaMock.setEstado(EstadoReserva.finalizada);

        facturaMock = new Factura();
        facturaMock.setIdFactura(1);
        facturaMock.setIdReserva(1);
        facturaMock.setMonto(new BigDecimal("100000"));
        facturaMock.setFechaEmision(LocalDate.now());
        facturaMock.setEstadoPago(EstadoPago.pendiente);
    }

    // ─── GENERAR FACTURA ──────────────────────────────────────────

    @Test
    @DisplayName("Generar factura exitosamente")
    void generar_debeCrearFacturaExitosamente() {
        when(facturaRepository.existsByIdReserva(1)).thenReturn(false);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicioMock));
        when(facturaRepository.save(any())).thenReturn(facturaMock);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));

        FacturaResponse response = facturaService.generar(1);

        assertNotNull(response);
        assertEquals(1, response.getIdFactura());
        assertEquals(EstadoPago.pendiente, response.getEstadoPago());
        verify(facturaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Generar factura cuando ya existe debe lanzar excepcion")
    void generar_facturaYaExiste_debeLanzarExcepcion() {
        when(facturaRepository.existsByIdReserva(1)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> facturaService.generar(1));

        verify(facturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Generar factura con reserva inexistente debe lanzar excepcion")
    void generar_reservaInexistente_debeLanzarExcepcion() {
        when(facturaRepository.existsByIdReserva(1)).thenReturn(false);
        when(reservaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ReservaNoEncontradaException.class,
                () -> facturaService.generar(1));
    }

    // ─── OBTENER FACTURA ──────────────────────────────────────────

    @Test
    @DisplayName("Obtener factura por id existente")
    void obtenerPorId_facturaExistente_debeRetornarResponse() {
        when(facturaRepository.findById(1)).thenReturn(Optional.of(facturaMock));
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reservaMock));
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));
        when(servicioRepository.findById(anyInt())).thenReturn(Optional.of(servicioMock));

        FacturaResponse response = facturaService.obtenerPorId(1);

        assertNotNull(response);
        assertEquals(1, response.getIdFactura());
    }

    @Test
    @DisplayName("Obtener factura por id inexistente debe lanzar excepcion")
    void obtenerPorId_facturaInexistente_debeLanzarExcepcion() {
        when(facturaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(FacturaNoEncontradaException.class,
                () -> facturaService.obtenerPorId(99));
    }

    @Test
    @DisplayName("Listar todas las facturas")
    void listarTodas_debeRetornarLista() {
        when(facturaRepository.findAll()).thenReturn(List.of(facturaMock));
        when(reservaRepository.findById(anyInt())).thenReturn(Optional.of(reservaMock));
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));
        when(servicioRepository.findById(anyInt())).thenReturn(Optional.of(servicioMock));

        List<FacturaResponse> result = facturaService.listarTodas();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ─── REGISTRAR PAGO ───────────────────────────────────────────

    @Test
    @DisplayName("Registrar pago exitosamente")
    void registrarPago_debeActualizarEstadoAPagado() {
        when(facturaRepository.findById(1)).thenReturn(Optional.of(facturaMock));
        when(facturaRepository.save(any())).thenReturn(facturaMock);
        when(reservaRepository.findById(anyInt())).thenReturn(Optional.of(reservaMock));
        when(servicioRepository.findById(anyInt())).thenReturn(Optional.of(servicioMock));
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));

        FacturaResponse response = facturaService.registrarPago(1);

        assertNotNull(response);
        verify(facturaRepository, times(1)).save(any());
        verify(emailService, times(1)).enviar(any());
    }

    @Test
    @DisplayName("Registrar pago factura inexistente debe lanzar excepcion")
    void registrarPago_facturaInexistente_debeLanzarExcepcion() {
        when(facturaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(FacturaNoEncontradaException.class,
                () -> facturaService.registrarPago(99));
    }

    // ─── ANULAR FACTURA ───────────────────────────────────────────

    @Test
    @DisplayName("Anular factura pendiente exitosamente")
    void anular_facturaPendiente_debeAnularCorrectamente() {
        when(facturaRepository.findById(1)).thenReturn(Optional.of(facturaMock));
        when(facturaRepository.save(any())).thenReturn(facturaMock);
        when(reservaRepository.findById(anyInt())).thenReturn(Optional.of(reservaMock));
        when(usuarioRepository.findById(anyInt())).thenReturn(Optional.of(usuarioMock));
        when(servicioRepository.findById(anyInt())).thenReturn(Optional.of(servicioMock));

        FacturaResponse response = facturaService.anular(1);

        assertNotNull(response);
        verify(facturaRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Anular factura ya pagada debe lanzar excepcion")
    void anular_facturaYaPagada_debeLanzarExcepcion() {
        facturaMock.setEstadoPago(EstadoPago.pagado);
        when(facturaRepository.findById(1)).thenReturn(Optional.of(facturaMock));

        assertThrows(IllegalStateException.class,
                () -> facturaService.anular(1));

        verify(facturaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Anular factura inexistente debe lanzar excepcion")
    void anular_facturaInexistente_debeLanzarExcepcion() {
        when(facturaRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(FacturaNoEncontradaException.class,
                () -> facturaService.anular(99));
    }
}