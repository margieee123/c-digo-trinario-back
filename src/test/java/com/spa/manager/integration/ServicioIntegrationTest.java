package com.spa.manager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import com.spa.manager.servicios.infrastructure.output.persistence.entity.ServicioEntity;
import com.spa.manager.servicios.infrastructure.output.persistence.repository.ServicioJpaRepository;
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

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Pruebas de integracion - Servicios")
class ServicioIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioJpaRepository usuarioRepository;
    @Autowired private ServicioJpaRepository servicioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private Integer idServicio;

    @BeforeEach
    void setUp() throws Exception {
        UsuarioEntity admin = new UsuarioEntity();
        admin.setNombre("Admin Test");
        admin.setCorreo("admin@test.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRol(Rol.administrador);
        admin.setEstado(Estado.activo);
        usuarioRepository.save(admin);

        ServicioEntity servicio = new ServicioEntity();
        servicio.setNombre("Masaje Test");
        servicio.setDescripcion("Masaje de prueba");
        servicio.setPrecio(new BigDecimal("100000"));
        servicio.setDuracionMinutos(60);
        servicio.setEstado(com.spa.manager.servicios.domain.model.EstadoServicio.activo);
        ServicioEntity guardado = servicioRepository.save(servicio);
        idServicio = guardado.getIdServicio();

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

    @Test
    @DisplayName("Listar servicios debe retornar lista")
    void listarServicios_debeRetornarLista() throws Exception {
        mockMvc.perform(get("/servicios")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].nombre").value("Masaje Test"));
    }

    @Test
    @DisplayName("Obtener servicio por ID debe retornar datos correctos")
    void obtenerServicio_porId_debeRetornarDatos() throws Exception {
        mockMvc.perform(get("/servicios/" + idServicio)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Masaje Test"))
                .andExpect(jsonPath("$.duracionMinutos").value(60));
    }

    @Test
    @DisplayName("Crear servicio debe persistir en BD")
    void crearServicio_debePersistirEnBD() throws Exception {
        String body = """
            {
                "nombre": "Facial Hidratante",
                "descripcion": "Tratamiento facial",
                "precio": 80000,
                "duracionMinutos": 45,
                "imagenUrl": ""
            }
            """;
        mockMvc.perform(post("/servicios")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Facial Hidratante"))
                .andExpect(jsonPath("$.estado").value("activo"));
    }

    @Test
    @DisplayName("Actualizar servicio debe persistir cambios")
    void actualizarServicio_debePersistirCambios() throws Exception {
        String body = """
            {
                "nombre": "Masaje Actualizado",
                "descripcion": "Descripcion actualizada",
                "precio": 120000,
                "duracionMinutos": 90,
                "imagenUrl": ""
            }
            """;
        mockMvc.perform(put("/servicios/" + idServicio)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Masaje Actualizado"))
                .andExpect(jsonPath("$.precio").value(120000));
    }

    @Test
    @DisplayName("Cambiar estado de servicio debe persistir el cambio")
    void cambiarEstado_debePersistirCambio() throws Exception {
        mockMvc.perform(patch("/servicios/" + idServicio + "/estado?estado=inactivo")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("inactivo"));
    }

    @Test
    @DisplayName("Buscar servicio por nombre debe retornar resultados")
    void buscarServicio_porNombre_debeRetornarResultados() throws Exception {
        mockMvc.perform(get("/servicios")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Listar servicios sin token debe retornar error")
    void listarServicios_sinToken_debeRetornarError() throws Exception {
        mockMvc.perform(get("/servicios"))
                .andExpect(status().is4xxClientError());
    }
}