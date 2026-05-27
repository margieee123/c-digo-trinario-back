package com.spa.manager.configuracion;

import com.spa.manager.shared.configuracion.application.dto.ConfiguracionSpaDto;
import com.spa.manager.shared.configuracion.application.service.ConfiguracionSpaService;
import com.spa.manager.shared.configuracion.infrastructure.output.persistence.entity.ConfiguracionSpaEntity;
import com.spa.manager.shared.configuracion.infrastructure.output.persistence.repository.ConfiguracionSpaJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - ConfiguracionSpaService")
class ConfiguracionSpaServiceTest {

    @Mock private ConfiguracionSpaJpaRepository repository;
    @InjectMocks private ConfiguracionSpaService service;

    @Test
    @DisplayName("obtener debe retornar configuracion existente")
    void obtener_debeRetornarConfiguracionExistente() {
        ConfiguracionSpaEntity entity = new ConfiguracionSpaEntity();
        entity.setId(1);
        entity.setNombre("Spa Test");
        entity.setDireccion("Calle 123");
        entity.setTelefono("3001234567");
        entity.setEmail("spa@test.com");

        when(repository.findById(1)).thenReturn(Optional.of(entity));

        ConfiguracionSpaDto result = service.obtener();

        assertEquals("Spa Test", result.getNombre());
        assertEquals("Calle 123", result.getDireccion());
        assertEquals("3001234567", result.getTelefono());
        assertEquals("spa@test.com", result.getEmail());
    }

    @Test
    @DisplayName("obtener debe retornar configuracion por defecto si no existe")
    void obtener_debeRetornarConfiguracionPorDefecto() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        ConfiguracionSpaDto result = service.obtener();

        assertEquals("Spa Manager", result.getNombre());
    }

    @Test
    @DisplayName("guardar debe actualizar configuracion existente")
    void guardar_debeActualizarConfiguracionExistente() {
        ConfiguracionSpaEntity entity = new ConfiguracionSpaEntity();
        entity.setId(1);
        entity.setNombre("Spa Viejo");

        when(repository.findById(1)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenReturn(entity);

        ConfiguracionSpaDto dto = new ConfiguracionSpaDto("Spa Nuevo", "Av 456", "3009999999", "nuevo@spa.com");
        ConfiguracionSpaDto result = service.guardar(dto);

        assertEquals("Spa Nuevo", result.getNombre());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("guardar debe crear configuracion si no existe")
    void guardar_debeCrearConfiguracionSiNoExiste() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ConfiguracionSpaDto dto = new ConfiguracionSpaDto("Spa Nuevo", "Av 456", "3009999999", "nuevo@spa.com");
        ConfiguracionSpaDto result = service.guardar(dto);

        assertEquals("Spa Nuevo", result.getNombre());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("guardar debe usar nombre por defecto si nombre es null")
    void guardar_debeUsarNombrePorDefectoSiNombreEsNull() {
        when(repository.findById(1)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(i -> i.getArgument(0));

        ConfiguracionSpaDto dto = new ConfiguracionSpaDto(null, "Av 456", "3009999999", "nuevo@spa.com");
        ConfiguracionSpaDto result = service.guardar(dto);

        assertEquals("Spa Manager", result.getNombre());
    }
}