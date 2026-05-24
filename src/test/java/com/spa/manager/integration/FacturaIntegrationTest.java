package com.spa.manager.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import com.spa.manager.reservas.domain.model.EstadoReserva;
import com.spa.manager.reservas.infrastructure.output.persistence.entity.ReservaEntity;
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
@DisplayName("Pruebas de integracion - Facturas")
class FacturaIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioJpaRepository usuarioRepository;
    @Autowired private ReservaJpaRepository reservaRepository;
    @Autowired private ServicioJpaRepository servicioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String tokenAdmin;
    private Integer idReserva;

    @BeforeEach
    void setUp() throws Exception {
        UsuarioEntity admin = new UsuarioEntity();
        admin.setNombre("Admin Test");
        admin.setCorreo("admin@test.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRol(Rol.administrador);
        admin.setEstado(Estado.activo);
        UsuarioEntity adminGuardado = usuarioRepository.save(admin);

        UsuarioEntity cliente = new UsuarioEntity();
        cliente.setNombre("Cliente Test");
        cliente.setCorreo("cliente@test.com");
        cliente.setPasswordHash(passwordEncoder.encode("test123"));
        cliente.setRol(Rol.cliente);
        cliente.setEstado(Estado.activo);
        UsuarioEntity clienteGuardado = usuarioRepository.save(cliente);

        UsuarioEntity terapeuta = new UsuarioEntity();
        terapeuta.setNombre("Terapeuta Test");
        terapeuta.setCorreo("terapeuta@test.com");
        terapeuta.setPasswordHash(passwordEncoder.encode("test123"));
        terapeuta.setRol(Rol.terapeuta);
        terapeuta.setEstado(Estado.activo);
        UsuarioEntity terapeutaGuardado = usuarioRepository.save(terapeuta);

        ServicioEntity servicio = new ServicioEntity();
        servicio.setNombre("Masaje Test");
        servicio.setDescripcion("Masaje de prueba");
        servicio.setPrecio(new BigDecimal("100000"));
        servicio.setDuracionMinutos(60);
        servicio.setEstado(com.spa.manager.servicios.domain.model.EstadoServicio.activo);
        ServicioEntity servicioGuardado = servicioRepository.save(servicio);

        ReservaEntity reserva = new ReservaEntity();
        reserva.setIdCliente(clienteGuardado.getId());
        reserva.setIdTerapeuta(terapeutaGuardado.getId());
        reserva.setIdServicios(List.of(servicioGuardado.getIdServicio()));
        reserva.setFecha(LocalDate.now().minusDays(1));
        reserva.setHoraInicio(LocalTime.of(9, 0));
        reserva.setHoraFin(LocalTime.of(10, 0));
        reserva.setEstado(EstadoReserva.finalizada);
        ReservaEntity reservaGuardada = reservaRepository.save(reserva);
        idReserva = reservaGuardada.getIdReserva();

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
    @DisplayName("Listar facturas debe retornar lista")
    void listarFacturas_debeRetornarLista() throws Exception {
        mockMvc.perform(get("/facturas")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Generar factura para reserva finalizada debe persistir en BD")
    void generarFactura_paraReservaFinalizada_debePersistirEnBD() throws Exception {
        mockMvc.perform(post("/facturas/reserva/" + idReserva)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idReserva").value(idReserva))
                .andExpect(jsonPath("$.estadoPago").value("pendiente"))
                .andExpect(jsonPath("$.monto").value(100000));
    }

    @Test
    @DisplayName("Generar factura dos veces debe retornar error")
    void generarFactura_dosVeces_debeRetornarError() throws Exception {
        mockMvc.perform(post("/facturas/reserva/" + idReserva)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk());

        mockMvc.perform(post("/facturas/reserva/" + idReserva)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Registrar pago de factura debe cambiar estado a pagado")
    void registrarPago_debeCambiarEstadoAPagado() throws Exception {
        String response = mockMvc.perform(post("/facturas/reserva/" + idReserva)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andReturn().getResponse().getContentAsString();

        Integer idFactura = objectMapper.readTree(response).get("idFactura").asInt();

        mockMvc.perform(patch("/facturas/" + idFactura + "/pagar")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoPago").value("pagado"));
    }

    @Test
    @DisplayName("Anular factura pendiente debe cambiar estado a anulada")
    void anularFactura_pendiente_debeCambiarEstadoAAnulada() throws Exception {
        String response = mockMvc.perform(post("/facturas/reserva/" + idReserva)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andReturn().getResponse().getContentAsString();

        Integer idFactura = objectMapper.readTree(response).get("idFactura").asInt();

        mockMvc.perform(patch("/facturas/" + idFactura + "/anular")
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoPago").value("anulada"));
    }

    @Test
    @DisplayName("Listar facturas sin token debe retornar error")
    void listarFacturas_sinToken_debeRetornarError() throws Exception {
        mockMvc.perform(get("/facturas"))
                .andExpect(status().is4xxClientError());
    }
}