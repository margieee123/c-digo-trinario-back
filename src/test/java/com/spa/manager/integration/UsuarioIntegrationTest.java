package com.spa.manager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Pruebas de integracion - Usuarios")
class UsuarioIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioJpaRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private Integer idUsuario;

    @BeforeEach
    void setUp() throws Exception {
        // Crear admin
        UsuarioEntity admin = new UsuarioEntity();
        admin.setNombre("Admin Test");
        admin.setCorreo("admin@test.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRol(Rol.administrador);
        admin.setEstado(Estado.activo);
        usuarioRepository.save(admin);

        // Crear cliente de prueba
        UsuarioEntity cliente = new UsuarioEntity();
        cliente.setNombre("Cliente Test");
        cliente.setCorreo("cliente@test.com");
        cliente.setPasswordHash(passwordEncoder.encode("test123"));
        cliente.setRol(Rol.cliente);
        cliente.setEstado(Estado.activo);
        UsuarioEntity guardado = usuarioRepository.save(cliente);
        idUsuario = guardado.getId();

        // Obtener token admin
        tokenAdmin = obtenerToken("admin@test.com", "admin123");
    }

    private String obtenerToken(String correo, String password) throws Exception {
        String body = String.format("{\"correo\":\"%s\",\"password\":\"%s\"}", correo, password);
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    // ─── AUTH ─────────────────────────────────────────────────

    @Test
    @DisplayName("Login exitoso debe retornar token")
    void login_exitoso_debeRetornarToken() throws Exception {
        String body = "{\"correo\":\"admin@test.com\",\"password\":\"admin123\"}";
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.rol").value("administrador"));
    }

    @Test
    @DisplayName("Login con credenciales incorrectas debe retornar error")
    void login_credencialesIncorrectas_debeRetornarError() throws Exception {
        String body = "{\"correo\":\"admin@test.com\",\"password\":\"wrongpassword\"}";
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Registro de nuevo cliente debe retornar token")
    void registro_nuevoCliente_debeRetornarToken() throws Exception {
        String body = "{\"nombre\":\"Nuevo Cliente\",\"correo\":\"nuevo@test.com\",\"password\":\"password123\"}";
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.rol").value("cliente"));
    }

    @Test
    @DisplayName("Registro con correo duplicado debe retornar error")
    void registro_correoDuplicado_debeRetornarError() throws Exception {
        String body = "{\"nombre\":\"Duplicado\",\"correo\":\"admin@test.com\",\"password\":\"password123\"}";
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());
    }

    // ─── USUARIOS ─────────────────────────────────────────────

    @Test
    @DisplayName("Listar usuarios como admin debe retornar lista")
    void listarUsuarios_comoAdmin_debeRetornarLista() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Obtener usuario por ID debe retornar datos correctos")
    void obtenerUsuario_porId_debeRetornarDatos() throws Exception {
        mockMvc.perform(get("/usuarios/" + idUsuario)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cliente Test"))
                .andExpect(jsonPath("$.correo").value("cliente@test.com"));
    }

    @Test
    @DisplayName("Crear usuario como admin debe persistir en BD")
    void crearUsuario_comoAdmin_debePersistirEnBD() throws Exception {
        String body = """
        {
            "nombre": "Nuevo Terapeuta",
            "correo": "terapeuta2@test.com",
            "password": "password123",
            "rol": "terapeuta"
        }
        """;
        mockMvc.perform(post("/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Nuevo Terapeuta"))
                .andExpect(jsonPath("$.rol").value("terapeuta"));
    }

    @Test
    @DisplayName("Actualizar usuario debe persistir cambios")
    void actualizarUsuario_debePersistirCambios() throws Exception {
        String body = """
            {
                "nombre": "Cliente Actualizado",
                "correo": "clienteupd@test.com",
                "rol": "cliente"
            }
            """;
        mockMvc.perform(put("/usuarios/" + idUsuario)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Cliente Actualizado"));
    }

    @Test
    @DisplayName("Cambiar estado de usuario debe persistir el cambio")
    void cambiarEstado_debePersistirCambio() throws Exception {
        mockMvc.perform(put("/usuarios/" + idUsuario + "/estado?estado=inactivo")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("inactivo"));
    }

    @Test
    @DisplayName("Buscar usuario por nombre debe retornar resultados")
    void buscarUsuario_porNombre_debeRetornarResultados() throws Exception {
        mockMvc.perform(get("/usuarios/buscar?nombre=Cliente")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}