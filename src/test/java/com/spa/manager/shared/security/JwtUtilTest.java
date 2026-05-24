package com.spa.manager.shared.security;

import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias - JwtUtil")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();

        // Inyectar valores via reflection
        var secretField = JwtUtil.class.getDeclaredField("secretKey");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "spa-manager-clave-secreta-super-segura-2024-para-jwt");

        var expirationField = JwtUtil.class.getDeclaredField("expirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 7200000L);

        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setNombre("Test User");
        usuarioMock.setCorreo("test@spa.com");
        usuarioMock.setRol(Rol.cliente);
        usuarioMock.setEstado(Estado.activo);
    }

    @Test
    @DisplayName("Generar token no debe ser nulo")
    void generateToken_debeRetornarTokenNoNulo() {
        String token = jwtUtil.generateToken(usuarioMock);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Extraer correo del token debe coincidir")
    void extractCorreo_debeRetornarCorreoCorrecto() {
        String token = jwtUtil.generateToken(usuarioMock);
        String correo = jwtUtil.extractCorreo(token);
        assertEquals("test@spa.com", correo);
    }

    @Test
    @DisplayName("Token valido debe retornar true")
    void isTokenValid_tokenValido_debeRetornarTrue() {
        String token = jwtUtil.generateToken(usuarioMock);
        assertTrue(jwtUtil.isTokenValid(token, "test@spa.com"));
    }

    @Test
    @DisplayName("Token con correo incorrecto debe retornar false")
    void isTokenValid_correoIncorrecto_debeRetornarFalse() {
        String token = jwtUtil.generateToken(usuarioMock);
        assertFalse(jwtUtil.isTokenValid(token, "otro@spa.com"));
    }

    @Test
    @DisplayName("Token expirado debe lanzar excepcion")
    void isTokenValid_tokenExpirado_debeLanzarExcepcion() throws Exception {
        var expirationField = JwtUtil.class.getDeclaredField("expirationMs");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, -1000L);

        String token = jwtUtil.generateToken(usuarioMock);

        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtUtil.isTokenValid(token, "test@spa.com"));
    }
}