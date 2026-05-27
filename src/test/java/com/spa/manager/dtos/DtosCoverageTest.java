package com.spa.manager.dtos;

import com.spa.manager.calificaciones.application.dto.CalificacionRequest;
import com.spa.manager.calificaciones.application.dto.CalificacionResponse;
import com.spa.manager.calificaciones.domain.exception.CalificacionNoEncontradaException;
import com.spa.manager.calificaciones.domain.model.Calificacion;
import com.spa.manager.auth.domain.exception.UsuarioInactivoException;
import com.spa.manager.facturas.application.dto.FacturaRequest;
import com.spa.manager.logs.application.dto.LogResponse;
import com.spa.manager.logs.domain.model.TipoLog;
import com.spa.manager.reservas.application.dto.ActualizarReservaRequest;
import com.spa.manager.reservas.application.dto.DisponibilidadSemanaResponse;
import com.spa.manager.reservas.application.dto.TerapeutaDisponibleResponse;
import com.spa.manager.shared.configuracion.application.dto.ConfiguracionSpaDto;
import com.spa.manager.shared.configuracion.infrastructure.output.persistence.entity.ConfiguracionSpaEntity;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity.TerapeutaServicioEntity;
import com.spa.manager.usuarios.application.dto.CambiarPasswordRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias - DTOs Coverage")
class DtosCoverageTest {

    // ─── ActualizarReservaRequest ─────────────────────────────

    @Test
    @DisplayName("ActualizarReservaRequest debe setear y obtener campos correctamente")
    void actualizarReservaRequest_debeSetearYObtenerCampos() {
        ActualizarReservaRequest request = new ActualizarReservaRequest();
        request.setIdServicios(List.of(1, 2, 3));
        request.setFecha(LocalDate.of(2026, 6, 1));
        request.setHoraInicio(LocalTime.of(9, 0));

        assertEquals(List.of(1, 2, 3), request.getIdServicios());
        assertEquals(LocalDate.of(2026, 6, 1), request.getFecha());
        assertEquals(LocalTime.of(9, 0), request.getHoraInicio());
    }

    // ─── CalificacionRequest ──────────────────────────────────

    @Test
    @DisplayName("CalificacionRequest debe setear y obtener campos correctamente")
    void calificacionRequest_debeSetearYObtenerCampos() {
        CalificacionRequest request = new CalificacionRequest();
        request.setIdReserva(1);
        request.setPuntuacion(5);
        request.setComentario("Excelente servicio");

        assertEquals(1, request.getIdReserva());
        assertEquals(5, request.getPuntuacion());
        assertEquals("Excelente servicio", request.getComentario());
    }

    // ─── CambiarPasswordRequest ───────────────────────────────

    @Test
    @DisplayName("CambiarPasswordRequest debe setear y obtener campos correctamente")
    void cambiarPasswordRequest_debeSetearYObtenerCampos() {
        CambiarPasswordRequest request = new CambiarPasswordRequest();
        request.setPasswordActual("password123");
        request.setPasswordNueva("newpassword123");

        assertEquals("password123", request.getPasswordActual());
        assertEquals("newpassword123", request.getPasswordNueva());
    }

    // ─── FacturaRequest ───────────────────────────────────────

    @Test
    @DisplayName("FacturaRequest debe setear y obtener idReserva correctamente")
    void facturaRequest_debeSetearYObtenerIdReserva() {
        FacturaRequest request = new FacturaRequest();
        request.setIdReserva(5);

        assertEquals(5, request.getIdReserva());
    }

    // ─── DisponibilidadSemanaResponse ─────────────────────────

    @Test
    @DisplayName("DisponibilidadSemanaResponse debe retornar slots correctamente")
    void disponibilidadSemanaResponse_debeRetornarSlots() {
        Map<String, Boolean> slots = Map.of(
                "2026-06-01T09:00", true,
                "2026-06-01T10:00", false
        );
        DisponibilidadSemanaResponse response = new DisponibilidadSemanaResponse(slots);

        assertEquals(slots, response.getSlots());
    }

    @Test
    @DisplayName("DisponibilidadSemanaResponse debe setear slots correctamente")
    void disponibilidadSemanaResponse_debeSetearSlots() {
        Map<String, Boolean> slots = Map.of("2026-06-01T09:00", true);
        DisponibilidadSemanaResponse response = new DisponibilidadSemanaResponse(slots);
        Map<String, Boolean> nuevosSlots = Map.of("2026-06-01T11:00", false);
        response.setSlots(nuevosSlots);

        assertEquals(nuevosSlots, response.getSlots());
    }

    // ─── CalificacionResponse ─────────────────────────────────

    @Test
    @DisplayName("CalificacionResponse debe setear y obtener campos correctamente")
    void calificacionResponse_debeSetearYObtenerCampos() {
        CalificacionResponse response = new CalificacionResponse();
        response.setIdCalificacion(1);
        response.setIdReserva(2);
        response.setPuntuacion(5);
        response.setComentario("Excelente");
        response.setFecha(LocalDate.of(2026, 6, 1));
        response.setNombreCliente("Juan Perez");
        response.setNombreTerapeuta("Maria Lopez");

        assertEquals(1, response.getIdCalificacion());
        assertEquals(2, response.getIdReserva());
        assertEquals(5, response.getPuntuacion());
        assertEquals("Excelente", response.getComentario());
        assertEquals(LocalDate.of(2026, 6, 1), response.getFecha());
        assertEquals("Juan Perez", response.getNombreCliente());
        assertEquals("Maria Lopez", response.getNombreTerapeuta());
    }

    // ─── ConfiguracionSpaDto ──────────────────────────────────

    @Test
    @DisplayName("ConfiguracionSpaDto constructor vacio debe crear objeto")
    void configuracionSpaDto_constructorVacio_debeCrearObjeto() {
        ConfiguracionSpaDto dto = new ConfiguracionSpaDto();
        assertNotNull(dto);
    }

    @Test
    @DisplayName("ConfiguracionSpaDto constructor con parametros debe asignar campos")
    void configuracionSpaDto_constructorParametros_debeAsignarCampos() {
        ConfiguracionSpaDto dto = new ConfiguracionSpaDto("Spa Test", "Calle 123", "3001234567", "spa@test.com");

        assertEquals("Spa Test", dto.getNombre());
        assertEquals("Calle 123", dto.getDireccion());
        assertEquals("3001234567", dto.getTelefono());
        assertEquals("spa@test.com", dto.getEmail());
    }

    @Test
    @DisplayName("ConfiguracionSpaDto debe setear y obtener campos correctamente")
    void configuracionSpaDto_debeSetearYObtenerCampos() {
        ConfiguracionSpaDto dto = new ConfiguracionSpaDto();
        dto.setNombre("Spa Manager");
        dto.setDireccion("Av Principal 456");
        dto.setTelefono("3009876543");
        dto.setEmail("manager@spa.com");

        assertEquals("Spa Manager", dto.getNombre());
        assertEquals("Av Principal 456", dto.getDireccion());
        assertEquals("3009876543", dto.getTelefono());
        assertEquals("manager@spa.com", dto.getEmail());
    }

    // ─── LogResponse ──────────────────────────────────────────

    @Test
    @DisplayName("LogResponse constructor vacio debe crear objeto")
    void logResponse_constructorVacio_debeCrearObjeto() {
        LogResponse response = new LogResponse();
        assertNotNull(response);
    }

    @Test
    @DisplayName("LogResponse constructor con parametros debe asignar campos")
    void logResponse_constructorParametros_debeAsignarCampos() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 1, 10, 0);
        LogResponse response = new LogResponse(1, TipoLog.LOGIN_EXITOSO, 2, "Admin", "Login exitoso", "127.0.0.1", fecha);

        assertEquals(1, response.getIdLog());
        assertEquals(TipoLog.LOGIN_EXITOSO, response.getTipo());
        assertEquals(2, response.getIdUsuario());
        assertEquals("Admin", response.getNombreUsuario());
        assertEquals("Login exitoso", response.getDescripcion());
        assertEquals("127.0.0.1", response.getIp());
        assertEquals(fecha, response.getFechaHora());
    }

    @Test
    @DisplayName("LogResponse debe setear y obtener campos correctamente")
    void logResponse_debeSetearYObtenerCampos() {
        LogResponse response = new LogResponse();
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 1, 10, 0);
        response.setIdLog(1);
        response.setTipo(TipoLog.LOGOUT);
        response.setIdUsuario(3);
        response.setNombreUsuario("Cliente Test");
        response.setDescripcion("Logout exitoso");
        response.setIp("192.168.1.1");
        response.setFechaHora(fecha);

        assertEquals(1, response.getIdLog());
        assertEquals(TipoLog.LOGOUT, response.getTipo());
        assertEquals(3, response.getIdUsuario());
        assertEquals("Cliente Test", response.getNombreUsuario());
        assertEquals("Logout exitoso", response.getDescripcion());
        assertEquals("192.168.1.1", response.getIp());
        assertEquals(fecha, response.getFechaHora());
    }

    // ─── ConfiguracionSpaEntity ───────────────────────────────

    @Test
    @DisplayName("ConfiguracionSpaEntity debe setear y obtener campos correctamente")
    void configuracionSpaEntity_debeSetearYObtenerCampos() {
        ConfiguracionSpaEntity entity = new ConfiguracionSpaEntity();
        entity.setId(1);
        entity.setNombre("Spa Entity");
        entity.setDireccion("Calle 789");
        entity.setTelefono("3001111111");
        entity.setEmail("entity@spa.com");

        assertEquals(1, entity.getId());
        assertEquals("Spa Entity", entity.getNombre());
        assertEquals("Calle 789", entity.getDireccion());
        assertEquals("3001111111", entity.getTelefono());
        assertEquals("entity@spa.com", entity.getEmail());
    }

    // ─── TerapeutaDisponibleResponse ──────────────────────────

    @Test
    @DisplayName("TerapeutaDisponibleResponse constructor debe asignar campos correctamente")
    void terapeutaDisponibleResponse_constructorDebeAsignarCampos() {
        TerapeutaDisponibleResponse response = new TerapeutaDisponibleResponse(1, "Maria Lopez", 3, 10);

        assertEquals(1, response.getIdTerapeuta());
        assertEquals("Maria Lopez", response.getNombre());
        assertEquals(3, response.getCitasHoy());
        assertEquals(10, response.getCitasSemana());
    }

    @Test
    @DisplayName("TerapeutaDisponibleResponse debe setear y obtener campos correctamente")
    void terapeutaDisponibleResponse_debeSetearYObtenerCampos() {
        TerapeutaDisponibleResponse response = new TerapeutaDisponibleResponse(1, "Test", 0, 0);
        response.setIdTerapeuta(2);
        response.setNombre("Ana Garcia");
        response.setCitasHoy(5);
        response.setCitasSemana(15);

        assertEquals(2, response.getIdTerapeuta());
        assertEquals("Ana Garcia", response.getNombre());
        assertEquals(5, response.getCitasHoy());
        assertEquals(15, response.getCitasSemana());
    }

    // ─── Calificacion ─────────────────────────────────────────

    @Test
    @DisplayName("Calificacion debe setear y obtener campos correctamente")
    void calificacion_debeSetearYObtenerCampos() {
        Calificacion calificacion = new Calificacion();
        calificacion.setIdCalificacion(1);
        calificacion.setIdReserva(2);
        calificacion.setPuntuacion(5);
        calificacion.setComentario("Muy bueno");
        calificacion.setFecha(LocalDate.of(2026, 6, 1));

        assertEquals(1, calificacion.getIdCalificacion());
        assertEquals(2, calificacion.getIdReserva());
        assertEquals(5, calificacion.getPuntuacion());
        assertEquals("Muy bueno", calificacion.getComentario());
        assertEquals(LocalDate.of(2026, 6, 1), calificacion.getFecha());
    }

    // ─── Excepciones ──────────────────────────────────────────

    @Test
    @DisplayName("CalificacionNoEncontradaException debe guardar mensaje")
    void calificacionNoEncontradaException_debeGuardarMensaje() {
        CalificacionNoEncontradaException ex = new CalificacionNoEncontradaException("Calificacion no encontrada");
        assertEquals("Calificacion no encontrada", ex.getMessage());
    }

    @Test
    @DisplayName("UsuarioInactivoException debe guardar mensaje")
    void usuarioInactivoException_debeGuardarMensaje() {
        UsuarioInactivoException ex = new UsuarioInactivoException("Usuario inactivo");
        assertEquals("Usuario inactivo", ex.getMessage());
    }

    // ─── TerapeutaServicioEntity ──────────────────────────────

    @Test
    @DisplayName("TerapeutaServicioEntity debe setear y obtener campos correctamente")
    void terapeutaServicioEntity_debeSetearYObtenerCampos() {
        TerapeutaServicioEntity entity = new TerapeutaServicioEntity();
        entity.setIdTerapeuta(1);
        entity.setIdServicio(2);

        assertEquals(1, entity.getIdTerapeuta());
        assertEquals(2, entity.getIdServicio());
    }

    @Test
    @DisplayName("TerapeutaServicioId equals y hashCode deben funcionar correctamente")
    void terapeutaServicioId_equalsYHashCode() {
        TerapeutaServicioEntity.TerapeutaServicioId id1 = new TerapeutaServicioEntity.TerapeutaServicioId(1, 2);
        TerapeutaServicioEntity.TerapeutaServicioId id2 = new TerapeutaServicioEntity.TerapeutaServicioId(1, 2);
        TerapeutaServicioEntity.TerapeutaServicioId id3 = new TerapeutaServicioEntity.TerapeutaServicioId(1, 3);

        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("TerapeutaServicioId constructor vacio debe crear objeto")
    void terapeutaServicioId_constructorVacio_debeCrearObjeto() {
        TerapeutaServicioEntity.TerapeutaServicioId id = new TerapeutaServicioEntity.TerapeutaServicioId();
        assertNotNull(id);
    }
    // ─── TerapeutaDisponibleResponse (usuarioadmin) ───────────

    @Test
    @DisplayName("TerapeutaDisponibleResponse usuarioadmin debe setear y obtener campos correctamente")
    void terapeutaDisponibleResponseAdmin_debeSetearYObtenerCampos() {
        com.spa.manager.usuarioadmin.application.dto.TerapeutaDisponibleResponse response =
                new com.spa.manager.usuarioadmin.application.dto.TerapeutaDisponibleResponse();
        response.setIdTerapeuta(1);
        response.setNombre("Maria Lopez");
        response.setHorariosDisponibles(List.of("09:00", "10:00", "11:00"));

        assertEquals(1, response.getIdTerapeuta());
        assertEquals("Maria Lopez", response.getNombre());
        assertEquals(3, response.getHorariosDisponibles().size());
    }
}