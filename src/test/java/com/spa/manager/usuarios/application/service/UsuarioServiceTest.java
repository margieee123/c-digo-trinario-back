package com.spa.manager.usuarios.application.service;

import com.spa.manager.auth.application.ports.output.UsuarioRepositoryPort;
import com.spa.manager.auth.domain.exception.UsuarioNoEncontradoException;
import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.domain.model.Usuario;
import com.spa.manager.shared.email.EmailService;
import com.spa.manager.usuarios.application.dto.UsuarioRequest;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - UsuarioService")
class UsuarioServiceTest {

    @Mock private UsuarioRepositoryPort usuarioRepositoryPort;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioMock;
    private UsuarioRequest requestMock;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNombre("Cliente Test");
        usuarioMock.setCorreo("cliente@test.com");
        usuarioMock.setPasswordhash("$2a$10$hashedpassword");
        usuarioMock.setRol(Rol.cliente);
        usuarioMock.setEstado(Estado.activo);

        requestMock = new UsuarioRequest();
        requestMock.setNombre("Cliente Test");
        requestMock.setCorreo("cliente@test.com");
        requestMock.setPassword("password123");
        requestMock.setRol(Rol.cliente);
    }

    // ─── LISTAR ───────────────────────────────────────────────────

    @Test
    @DisplayName("Listar usuarios debe retornar lista")
    void listar_debeRetornarLista() {
        when(usuarioRepositoryPort.findAll()).thenReturn(List.of(usuarioMock));

        List<UsuarioResponse> result = usuarioService.listar();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cliente Test", result.get(0).getNombre());
    }

    @Test
    @DisplayName("Listar usuarios vacia debe retornar lista vacia")
    void listar_sinUsuarios_debeRetornarListaVacia() {
        when(usuarioRepositoryPort.findAll()).thenReturn(List.of());

        List<UsuarioResponse> result = usuarioService.listar();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ─── OBTENER ──────────────────────────────────────────────────

    @Test
    @DisplayName("Obtener usuario por id existente")
    void obtener_usuarioExistente_debeRetornarResponse() {
        when(usuarioRepositoryPort.findById(1)).thenReturn(Optional.of(usuarioMock));

        UsuarioResponse response = usuarioService.obtener(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("Cliente Test", response.getNombre());
    }

    @Test
    @DisplayName("Obtener usuario por id inexistente debe lanzar excepcion")
    void obtener_usuarioInexistente_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.findById(99)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioService.obtener(99));
    }

    // ─── CREAR ────────────────────────────────────────────────────

    @Test
    @DisplayName("Crear usuario exitosamente")
    void crear_debeCrearUsuarioExitosamente() {
        when(usuarioRepositoryPort.existsBycorreo(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedpassword");
        when(usuarioRepositoryPort.save(any())).thenReturn(usuarioMock);

        UsuarioResponse response = usuarioService.crear(requestMock);

        assertNotNull(response);
        assertEquals("Cliente Test", response.getNombre());
        verify(usuarioRepositoryPort, times(1)).save(any());
    }

    @Test
    @DisplayName("Crear usuario con correo duplicado debe lanzar excepcion")
    void crear_correoYaExiste_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.existsBycorreo(anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.crear(requestMock));

        verify(usuarioRepositoryPort, never()).save(any());
    }

    // ─── ACTUALIZAR ───────────────────────────────────────────────

    @Test
    @DisplayName("Actualizar usuario exitosamente")
    void actualizar_debeActualizarUsuario() {
        when(usuarioRepositoryPort.findById(1)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepositoryPort.save(any())).thenReturn(usuarioMock);

        UsuarioResponse response = usuarioService.actualizar(1, requestMock);

        assertNotNull(response);
        verify(usuarioRepositoryPort, times(1)).save(any());
    }

    @Test
    @DisplayName("Actualizar usuario inexistente debe lanzar excepcion")
    void actualizar_usuarioInexistente_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.findById(99)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioService.actualizar(99, requestMock));
    }

    // ─── CAMBIAR ESTADO ───────────────────────────────────────────

    @Test
    @DisplayName("Cambiar estado usuario exitosamente")
    void cambiarEstado_debeActualizarEstado() {
        when(usuarioRepositoryPort.findById(1)).thenReturn(Optional.of(usuarioMock));
        when(usuarioRepositoryPort.save(any())).thenReturn(usuarioMock);

        UsuarioResponse response = usuarioService.cambiarEstado(1, Estado.inactivo);

        assertNotNull(response);
        verify(usuarioRepositoryPort, times(1)).save(any());
    }

    // ─── ELIMINAR ─────────────────────────────────────────────────

    @Test
    @DisplayName("Eliminar usuario debe marcarlo como inactivo")
    void eliminar_debeMarcarlComoInactivo() {
        when(usuarioRepositoryPort.findById(1)).thenReturn(Optional.of(usuarioMock));

        usuarioService.eliminar(1);

        verify(usuarioRepositoryPort, times(1)).save(any());
    }

    @Test
    @DisplayName("Eliminar usuario inexistente debe lanzar excepcion")
    void eliminar_usuarioInexistente_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.findById(99)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioService.eliminar(99));
    }

    // ─── CAMBIAR PASSWORD ─────────────────────────────────────────

    @Test
    @DisplayName("Cambiar password exitosamente")
    void cambiarPassword_debeActualizarPassword() {
        when(usuarioRepositoryPort.findBycorreo(anyString())).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$newhashedpassword");

        usuarioService.cambiarPassword("cliente@test.com", "oldpassword", "newpassword123");

        verify(usuarioRepositoryPort, times(1)).save(any());
    }

    @Test
    @DisplayName("Cambiar password con password actual incorrecta debe lanzar excepcion")
    void cambiarPassword_passwordIncorrecta_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.findBycorreo(anyString())).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cambiarPassword("cliente@test.com", "wrongpassword", "newpassword123"));

        verify(usuarioRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Cambiar password con nueva password corta debe lanzar excepcion")
    void cambiarPassword_passwordNuevaCorta_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.findBycorreo(anyString())).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> usuarioService.cambiarPassword("cliente@test.com", "oldpassword", "123"));

        verify(usuarioRepositoryPort, never()).save(any());
    }

    // ─── RECUPERAR PASSWORD ───────────────────────────────────────

    @Test
    @DisplayName("Recuperar password debe enviar email")
    void recuperarPassword_debeEnviarEmail() {
        when(usuarioRepositoryPort.findBycorreo(anyString())).thenReturn(Optional.of(usuarioMock));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedpassword");

        usuarioService.recuperarPassword("cliente@test.com");

        verify(emailService, times(1)).enviar(any());
        verify(usuarioRepositoryPort, times(1)).save(any());
    }

    @Test
    @DisplayName("Recuperar password con correo inexistente debe lanzar excepcion")
    void recuperarPassword_correoInexistente_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.findBycorreo(anyString())).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> usuarioService.recuperarPassword("noexiste@test.com"));

        verify(emailService, never()).enviar(any());
    }

    // ─── BUSCAR POR NOMBRE ────────────────────────────────────────

    @Test
    @DisplayName("Buscar por nombre debe filtrar solo clientes")
    void buscarPorNombre_debeRetornarSoloClientes() {
        Usuario terapeuta = new Usuario();
        terapeuta.setId(2);
        terapeuta.setNombre("Cliente Terapeuta");
        terapeuta.setCorreo("terapeuta@test.com");
        terapeuta.setRol(Rol.terapeuta);
        terapeuta.setEstado(Estado.activo);

        when(usuarioRepositoryPort.findAll()).thenReturn(List.of(usuarioMock, terapeuta));

        List<UsuarioResponse> result = usuarioService.buscarPorNombre("cliente");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Rol.cliente, result.get(0).getRol());
    }
}