package com.spa.manager.servicios.application.service;

import com.spa.manager.servicios.application.dto.ServicioRequest;
import com.spa.manager.servicios.application.dto.ServicioResponse;
import com.spa.manager.servicios.application.ports.output.ServicioRepositoryPort;
import com.spa.manager.servicios.domain.exception.ServicioNoEncontradoException;
import com.spa.manager.servicios.domain.model.EstadoServicio;
import com.spa.manager.servicios.domain.model.Servicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - ServicioService")
class ServicioServiceTest {

    @Mock private ServicioRepositoryPort servicioRepository;

    @InjectMocks
    private ServicioService servicioService;

    private Servicio servicioMock;
    private ServicioRequest requestMock;

    @BeforeEach
    void setUp() {
        servicioMock = new Servicio();
        servicioMock.setIdServicio(1);
        servicioMock.setNombre("Masaje Relajante");
        servicioMock.setDescripcion("Masaje de cuerpo completo");
        servicioMock.setPrecio(new BigDecimal("100000"));
        servicioMock.setDuracionMinutos(60);
        servicioMock.setEstado(EstadoServicio.activo);
        servicioMock.setImagenUrl("https://imagen.com/masaje.jpg");

        requestMock = new ServicioRequest();
        requestMock.setNombre("Masaje Relajante");
        requestMock.setDescripcion("Masaje de cuerpo completo");
        requestMock.setPrecio(new BigDecimal("100000"));
        requestMock.setDuracionMinutos(60);
        requestMock.setImagenUrl("https://imagen.com/masaje.jpg");
    }

    // ─── CREAR ────────────────────────────────────────────────────

    @Test
    @DisplayName("Crear servicio exitosamente")
    void crear_debeCrearServicioExitosamente() {
        when(servicioRepository.save(any())).thenReturn(servicioMock);

        ServicioResponse response = servicioService.crear(requestMock);

        assertNotNull(response);
        assertEquals("Masaje Relajante", response.getNombre());
        assertEquals(EstadoServicio.activo, response.getEstado());
        verify(servicioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Crear servicio debe asignar estado activo por defecto")
    void crear_debeAsignarEstadoActivo() {
        when(servicioRepository.save(any())).thenReturn(servicioMock);

        ServicioResponse response = servicioService.crear(requestMock);

        assertEquals(EstadoServicio.activo, response.getEstado());
    }

    // ─── LISTAR ───────────────────────────────────────────────────

    @Test
    @DisplayName("Listar servicios debe retornar lista")
    void listar_debeRetornarLista() {
        when(servicioRepository.findAll()).thenReturn(List.of(servicioMock));

        List<ServicioResponse> result = servicioService.listar();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Masaje Relajante", result.get(0).getNombre());
    }

    @Test
    @DisplayName("Listar servicios vacia debe retornar lista vacia")
    void listar_sinServicios_debeRetornarListaVacia() {
        when(servicioRepository.findAll()).thenReturn(List.of());

        List<ServicioResponse> result = servicioService.listar();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ─── OBTENER ──────────────────────────────────────────────────

    @Test
    @DisplayName("Obtener servicio por id existente")
    void obtener_servicioExistente_debeRetornarResponse() {
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicioMock));

        ServicioResponse response = servicioService.obtener(1);

        assertNotNull(response);
        assertEquals(1, response.getIdServicio());
        assertEquals("Masaje Relajante", response.getNombre());
    }

    @Test
    @DisplayName("Obtener servicio por id inexistente debe lanzar excepcion")
    void obtener_servicioInexistente_debeLanzarExcepcion() {
        when(servicioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ServicioNoEncontradoException.class,
                () -> servicioService.obtener(99));
    }

    // ─── ACTUALIZAR ───────────────────────────────────────────────

    @Test
    @DisplayName("Actualizar servicio exitosamente")
    void actualizar_debeActualizarServicio() {
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicioMock));
        when(servicioRepository.save(any())).thenReturn(servicioMock);

        ServicioResponse response = servicioService.actualizar(1, requestMock);

        assertNotNull(response);
        verify(servicioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Actualizar servicio inexistente debe lanzar excepcion")
    void actualizar_servicioInexistente_debeLanzarExcepcion() {
        when(servicioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ServicioNoEncontradoException.class,
                () -> servicioService.actualizar(99, requestMock));

        verify(servicioRepository, never()).save(any());
    }

    // ─── ELIMINAR ─────────────────────────────────────────────────

    @Test
    @DisplayName("Eliminar servicio debe marcarlo como inactivo")
    void eliminar_debeMarcarlComoInactivo() {
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicioMock));

        servicioService.eliminar(1);

        verify(servicioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Eliminar servicio inexistente debe lanzar excepcion")
    void eliminar_servicioInexistente_debeLanzarExcepcion() {
        when(servicioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ServicioNoEncontradoException.class,
                () -> servicioService.eliminar(99));

        verify(servicioRepository, never()).save(any());
    }

    // ─── CAMBIAR ESTADO ───────────────────────────────────────────

    @Test
    @DisplayName("Cambiar estado servicio a inactivo")
    void cambiarEstado_debeActualizarEstado() {
        when(servicioRepository.findById(1)).thenReturn(Optional.of(servicioMock));
        when(servicioRepository.save(any())).thenReturn(servicioMock);

        ServicioResponse response = servicioService.cambiarEstado(1, EstadoServicio.inactivo);

        assertNotNull(response);
        verify(servicioRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Cambiar estado servicio inexistente debe lanzar excepcion")
    void cambiarEstado_servicioInexistente_debeLanzarExcepcion() {
        when(servicioRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ServicioNoEncontradoException.class,
                () -> servicioService.cambiarEstado(99, EstadoServicio.inactivo));
    }

    // ─── BUSCAR POR NOMBRE ────────────────────────────────────────

    @Test
    @DisplayName("Buscar por nombre debe retornar servicios coincidentes")
    void buscarPorNombre_debeRetornarServicios() {
        when(servicioRepository.buscarPorNombre("masaje")).thenReturn(List.of(servicioMock));

        List<ServicioResponse> result = servicioService.buscarPorNombre("masaje");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Masaje Relajante", result.get(0).getNombre());
    }

    @Test
    @DisplayName("Buscar por nombre sin coincidencias debe retornar lista vacia")
    void buscarPorNombre_sinCoincidencias_debeRetornarListaVacia() {
        when(servicioRepository.buscarPorNombre("xyz")).thenReturn(List.of());

        List<ServicioResponse> result = servicioService.buscarPorNombre("xyz");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}