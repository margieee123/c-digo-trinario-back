package com.spa.manager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import com.spa.manager.reservas.application.dto.ReservaRequest;
import com.spa.manager.reservas.infrastructure.output.persistence.repository.ReservaJpaRepository;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Pruebas de integracion - Reservas")
class ReservaIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioJpaRepository usuarioRepository;
    @Autowired private ReservaJpaRepository reservaRepository;
    @Autowired private ServicioJpaRepository servicioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private String tokenCliente;
    private Integer idServicio;
    private Integer idTerapeuta;
    private Integer idCliente;

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

        // Crear terapeuta
        UsuarioEntity terapeuta = new UsuarioEntity();
        terapeuta.setNombre("Terapeuta Test");
        terapeuta.setCorreo("terapeuta@test.com");
        terapeuta.setPasswordHash(passwordEncoder.encode("test123"));
        terapeuta.setRol(Rol.terapeuta);
        terapeuta.setEstado(Estado.activo);
        UsuarioEntity terapeutaGuardado = usuarioRepository.save(terapeuta);
        idTerapeuta = terapeutaGuardado.getId();

        // Crear cliente
        UsuarioEntity cliente = new UsuarioEntity();
        cliente.setNombre("Cliente Test");
        cliente.setCorreo("cliente@test.com");
        cliente.setPasswordHash(passwordEncoder.encode("test123"));
        cliente.setRol(Rol.cliente);
        cliente.setEstado(Estado.activo);
        UsuarioEntity clienteGuardado = usuarioRepository.save(cliente);
        idCliente = clienteGuardado.getId();

        // Crear servicio
        ServicioEntity servicio = new ServicioEntity();
        servicio.setNombre("Masaje Test");
        servicio.setDescripcion("Masaje de prueba");
        servicio.setPrecio(new BigDecimal("100000"));
        servicio.setDuracionMinutos(60);
        servicio.setEstado(com.spa.manager.servicios.domain.model.EstadoServicio.activo);
        ServicioEntity servicioGuardado = servicioRepository.save(servicio);
        idServicio = servicioGuardado.getIdServicio();

        // Obtener tokens
        tokenAdmin = obtenerToken("admin@test.com", "admin123");
        tokenCliente = obtenerToken("cliente@test.com", "test123");
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
    @DisplayName("Crear reserva como admin debe persistir en BD")
    void crearReserva_comoAdmin_debePersistirEnBD() throws Exception {
        ReservaRequest request = new ReservaRequest();
        request.setIdCliente(idCliente);
        request.setIdTerapeuta(idTerapeuta);
        request.setIdServicios(List.of(idServicio));
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(10, 0));

        mockMvc.perform(post("/reservas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").exists())
                .andExpect(jsonPath("$.estado").value("pendiente"))
                .andExpect(jsonPath("$.nombreTerapeuta").value("Terapeuta Test"));
    }

    @Test
    @DisplayName("Listar reservas como admin debe retornar lista")
    void listarReservas_comoAdmin_debeRetornarLista() throws Exception {
        mockMvc.perform(get("/reservas")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Listar reservas sin token debe retornar 403")
    void listarReservas_sinToken_debeRetornar403() throws Exception {
        mockMvc.perform(get("/reservas"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Crear reserva sin servicios debe retornar error")
    void crearReserva_sinServicios_debeRetornarError() throws Exception {
        ReservaRequest request = new ReservaRequest();
        request.setIdCliente(idCliente);
        request.setIdTerapeuta(idTerapeuta);
        request.setIdServicios(List.of());
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(10, 0));

        mockMvc.perform(post("/reservas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Cambiar estado de reserva debe persistir el cambio")
    void cambiarEstado_debePersistirCambio() throws Exception {
        // Crear reserva primero
        ReservaRequest request = new ReservaRequest();
        request.setIdCliente(idCliente);
        request.setIdTerapeuta(idTerapeuta);
        request.setIdServicios(List.of(idServicio));
        request.setFecha(LocalDate.now().plusDays(1));
        request.setHoraInicio(LocalTime.of(10, 0));

        String response = mockMvc.perform(post("/reservas")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Integer idReserva = objectMapper.readTree(response).get("idReserva").asInt();

        mockMvc.perform(patch("/reservas/" + idReserva + "/estado?estado=confirmada")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("confirmada"));
    }

    @Test
    @DisplayName("Cliente no puede listar todas las reservas debe retornar error")
    void listarReservas_comoCliente_debeRetornarError() throws Exception {
        mockMvc.perform(get("/reservas")
                        .header("Authorization", "Bearer " + tokenCliente))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("Buscar terapeuta disponible debe retornar resultado")
    void buscarTerapeutaDisponible_debeRetornarResultado() throws Exception {
        mockMvc.perform(get("/reservas/terapeuta-disponible")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .param("fecha", LocalDate.now().plusDays(1).toString())
                        .param("horaInicio", "10:00:00")
                        .param("duracionMinutos", "60"))
                .andExpect(status().isOk());
    }
}