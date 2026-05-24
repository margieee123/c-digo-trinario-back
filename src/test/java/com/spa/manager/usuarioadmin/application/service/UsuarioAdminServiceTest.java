package com.spa.manager.usuarioadmin.application.service;

import com.spa.manager.auth.application.ports.output.PasswordEncoderPort;
import com.spa.manager.auth.application.ports.output.UsuarioRepositoryPort;
import com.spa.manager.auth.domain.exception.CorreoYaRegistradoException;
import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.domain.model.Usuario;
import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import com.spa.manager.reservas.application.ports.output.ReservaRepositoryPort;
import com.spa.manager.reservas.domain.model.EstadoReserva;
import com.spa.manager.reservas.domain.model.Reserva;
import com.spa.manager.servicios.application.ports.output.ServicioRepositoryPort;
import com.spa.manager.servicios.domain.model.Servicio;
import com.spa.manager.usuarioadmin.application.dto.*;
import com.spa.manager.usuarioadmin.application.ports.output.DisponibilidadRepositoryPort;
import com.spa.manager.usuarioadmin.application.ports.output.TerapeutaServicioRepositoryPort;
import com.spa.manager.usuarioadmin.domain.model.Disponibilidad;
import com.spa.manager.usuarioadmin.domain.model.DiaSemana;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;
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
@DisplayName("Pruebas unitarias - UsuarioAdminService")
class UsuarioAdminServiceTest {

    @Mock private UsuarioRepositoryPort usuarioRepository;
    @Mock private PasswordEncoderPort passwordEncoder;
    @Mock private DisponibilidadRepositoryPort disponibilidadRepository;
    @Mock private TerapeutaServicioRepositoryPort terapeutaServicioRepository;
    @Mock private ReservaRepositoryPort reservaRepository;
    @Mock private ServicioRepositoryPort servicioRepository;
    @Mock private UsuarioJpaRepository usuarioJpaRepository;

    @InjectMocks
    private UsuarioAdminService usuarioAdminService;

    private Usuario usuarioMock;
    private UsuarioEntity usuarioEntityMock;
    private CrearStaffRequest crearStaffRequest;
    private Disponibilidad disponibilidadMock;
    private Reserva reservaMock;
    private Servicio servicioMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNombre("Terapeuta Test");
        usuarioMock.setCorreo("terapeuta@test.com");
        usuarioMock.setPasswordhash("$2a$10$hashedpassword");
        usuarioMock.setRol(Rol.terapeuta);
        usuarioMock.setEstado(Estado.activo);

        usuarioEntityMock = new UsuarioEntity();
        usuarioEntityMock.setIdUsuario(1);
        usuarioEntityMock.setNombre("Terapeuta Test");
        usuarioEntityMock.setCorreo("terapeuta@test.com");
        usuarioEntityMock.setRol(Rol.terapeuta);
        usuarioEntityMock.setEstado(Estado.activo);

        crearStaffRequest = new CrearStaffRequest();
        crearStaffRequest.setNombre("Terapeuta Test");
        crearStaffRequest.setCorreo("terapeuta@test.com");
        crearStaffRequest.setPassword("password123");
        crearStaffRequest.setRol(Rol.terapeuta);

        disponibilidadMock = new Disponibilidad();
        disponibilidadMock.setIdDisponibilidad(1);
        disponibilidadMock.setIdTerapeuta(1);
        disponibilidadMock.setDiaSemana(DiaSemana.lunes);
        disponibilidadMock.setHoraInicio(LocalTime.of(8, 0));
        disponibilidadMock.setHoraFin(LocalTime.of(17, 0));

        reservaMock = new Reserva();
        reservaMock.setIdReserva(1);
        reservaMock.setIdCliente(2);
        reservaMock.setIdTerapeuta(1);
        reservaMock.setIdServicios(List.of(1));
        reservaMock.setFecha(LocalDate.now());
        reservaMock.setHoraInicio(LocalTime.of(9, 0));
        reservaMock.setHoraFin(LocalTime.of(10, 0));
        reservaMock.setEstado(EstadoReserva.confirmada);

        servicioMock = new Servicio();
        servicioMock.setIdServicio(1);
        servicioMock.setNombre("Masaje Relajante");
        servicioMock.setPrecio(new BigDecimal("100000"));
        servicioMock.setDuracionMinutos(60);
    }

    // ─── CREAR STAFF ──────────────────────────────────────────────

    @Test
    @DisplayName("Crear staff exitosamente")
    void crearStaff_debeCrearUsuarioExitosamente() {
        when(usuarioRepository.existsBycorreo(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedpassword");
        when(usuarioRepository.save(any())).thenReturn(usuarioMock);

        UsuarioResponse response = usuarioAdminService.crearStaff(crearStaffRequest);

        assertNotNull(response);
        assertEquals("Terapeuta Test", response.getNombre());
        assertEquals(Rol.terapeuta, response.getRol());
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Crear staff con correo duplicado debe lanzar excepcion")
    void crearStaff_correoYaExiste_debeLanzarExcepcion() {
        when(usuarioRepository.existsBycorreo(anyString())).thenReturn(true);

        assertThrows(CorreoYaRegistradoException.class,
                () -> usuarioAdminService.crearStaff(crearStaffRequest));

        verify(usuarioRepository, never()).save(any());
    }

    // ─── DISPONIBILIDAD ───────────────────────────────────────────

    @Test
    @DisplayName("Agregar disponibilidad exitosamente")
    void agregar_debeGuardarDisponibilidad() {
        DisponibilidadRequest request = new DisponibilidadRequest();
        request.setDiaSemana(DiaSemana.lunes);
        request.setHoraInicio(LocalTime.of(8, 0));
        request.setHoraFin(LocalTime.of(17, 0));

        when(disponibilidadRepository.save(any())).thenReturn(disponibilidadMock);
        when(usuarioJpaRepository.findById(1)).thenReturn(Optional.of(usuarioEntityMock));

        DisponibilidadResponse response = usuarioAdminService.agregar(1, request);

        assertNotNull(response);
        assertEquals(1, response.getIdTerapeuta());
        verify(disponibilidadRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Listar disponibilidad por terapeuta")
    void listarPorTerapeuta_debeRetornarLista() {
        when(disponibilidadRepository.findByIdTerapeuta(1)).thenReturn(List.of(disponibilidadMock));
        when(usuarioJpaRepository.findById(1)).thenReturn(Optional.of(usuarioEntityMock));

        List<DisponibilidadResponse> result = usuarioAdminService.listarPorTerapeuta(1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Eliminar disponibilidad")
    void eliminar_debeEliminarDisponibilidad() {
        usuarioAdminService.eliminar(1);

        verify(disponibilidadRepository, times(1)).deleteById(1);
    }

    // ─── TERAPEUTA SERVICIO ───────────────────────────────────────

    @Test
    @DisplayName("Asignar servicio a terapeuta exitosamente")
    void asignarServicio_debeAsignarCorrectamente() {
        when(terapeutaServicioRepository.exists(1, 1)).thenReturn(false);

        usuarioAdminService.asignarServicio(1, 1);

        verify(terapeutaServicioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Asignar servicio ya asignado debe lanzar excepcion")
    void asignarServicio_yaAsignado_debeLanzarExcepcion() {
        when(terapeutaServicioRepository.exists(1, 1)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> usuarioAdminService.asignarServicio(1, 1));

        verify(terapeutaServicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Remover servicio de terapeuta")
    void removerServicio_debeRemoverCorrectamente() {
        usuarioAdminService.removerServicio(1, 1);

        verify(terapeutaServicioRepository, times(1)).delete(1, 1);
    }

    @Test
    @DisplayName("Listar servicios de terapeuta")
    void listarServiciosDeTerapeuta_debeRetornarLista() {
        when(terapeutaServicioRepository.findServiciosByIdTerapeuta(1)).thenReturn(List.of(1, 2));

        List<Integer> result = usuarioAdminService.listarServiciosDeTerapeuta(1);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ─── CAMBIAR ROL ──────────────────────────────────────────────

    @Test
    @DisplayName("Cambiar rol de usuario exitosamente")
    void cambiarRol_debeActualizarRol() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepository.save(any())).thenReturn(usuarioMock);

        UsuarioResponse response = usuarioAdminService.cambiarRol(1, Rol.recepcionista);

        assertNotNull(response);
        verify(usuarioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Cambiar rol usuario inexistente debe lanzar excepcion")
    void cambiarRol_usuarioInexistente_debeLanzarExcepcion() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> usuarioAdminService.cambiarRol(99, Rol.recepcionista));
    }

    // ─── LISTAR STAFF ─────────────────────────────────────────────

    @Test
    @DisplayName("Listar terapeutas")
    void listarTerapeutas_debeRetornarLista() {
        when(usuarioJpaRepository.findByRol(Rol.terapeuta)).thenReturn(List.of(usuarioEntityMock));

        List<UsuarioResponse> result = usuarioAdminService.listarTerapeutas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Rol.terapeuta, result.get(0).getRol());
    }

    @Test
    @DisplayName("Listar recepcionistas")
    void listarRecepcionistas_debeRetornarLista() {
        UsuarioEntity recepcionista = new UsuarioEntity();
        recepcionista.setIdUsuario(2);
        recepcionista.setNombre("Recepcionista Test");
        recepcionista.setCorreo("recep@test.com");
        recepcionista.setRol(Rol.recepcionista);
        recepcionista.setEstado(Estado.activo);

        when(usuarioJpaRepository.findByRol(Rol.recepcionista)).thenReturn(List.of(recepcionista));

        List<UsuarioResponse> result = usuarioAdminService.listarRecepcionistas();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Rol.recepcionista, result.get(0).getRol());
    }

    // ─── VER AGENDA ───────────────────────────────────────────────

    @Test
    @DisplayName("Ver agenda de terapeuta para una fecha")
    void verAgenda_debeRetornarReservasDelDia() {
        when(reservaRepository.findByIdTerapeuta(1)).thenReturn(List.of(reservaMock));
        when(usuarioJpaRepository.findById(2)).thenReturn(Optional.of(usuarioEntityMock));
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicioMock));

        List<AgendaResponse> result = usuarioAdminService.verAgenda(1, LocalDate.now());

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Ver agenda sin reservas debe retornar lista vacia")
    void verAgenda_sinReservas_debeRetornarListaVacia() {
        when(reservaRepository.findByIdTerapeuta(1)).thenReturn(List.of());

        List<AgendaResponse> result = usuarioAdminService.verAgenda(1, LocalDate.now());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}