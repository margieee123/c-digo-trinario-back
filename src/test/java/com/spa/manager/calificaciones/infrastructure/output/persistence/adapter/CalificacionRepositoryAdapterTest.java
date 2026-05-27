package com.spa.manager.calificaciones.infrastructure.output.persistence.adapter;

import com.spa.manager.calificaciones.domain.model.Calificacion;
import com.spa.manager.calificaciones.infrastructure.output.persistence.entity.CalificacionEntity;
import com.spa.manager.calificaciones.infrastructure.output.persistence.mapper.CalificacionMapper;
import com.spa.manager.calificaciones.infrastructure.output.persistence.repository.CalificacionJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - CalificacionRepositoryAdapter")
class CalificacionRepositoryAdapterTest {

    @Mock private CalificacionJpaRepository jpaRepository;
    @Mock private CalificacionMapper mapper;
    @InjectMocks private CalificacionRepositoryAdapter adapter;

    private CalificacionEntity entityMock() {
        CalificacionEntity entity = new CalificacionEntity();
        entity.setIdCalificacion(1);
        entity.setIdReserva(1);
        entity.setPuntuacion(5);
        entity.setComentario("Excelente");
        entity.setFecha(LocalDate.now());
        return entity;
    }

    private Calificacion domainMock() {
        Calificacion c = new Calificacion();
        c.setIdCalificacion(1);
        c.setIdReserva(1);
        c.setPuntuacion(5);
        c.setComentario("Excelente");
        c.setFecha(LocalDate.now());
        return c;
    }

    @Test
    @DisplayName("save debe guardar y retornar calificacion")
    void save_debeGuardarYRetornarCalificacion() {
        Calificacion domain = domainMock();
        CalificacionEntity entity = entityMock();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Calificacion result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(1, result.getIdCalificacion());
        verify(jpaRepository).save(entity);
    }

    @Test
    @DisplayName("findById debe retornar calificacion si existe")
    void findById_debeRetornarCalificacionSiExiste() {
        CalificacionEntity entity = entityMock();
        Calificacion domain = domainMock();

        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Calificacion> result = adapter.findById(1);

        assertTrue(result.isPresent());
        assertEquals(1, result.get().getIdCalificacion());
    }

    @Test
    @DisplayName("findById debe retornar empty si no existe")
    void findById_debeRetornarEmptySiNoExiste() {
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Calificacion> result = adapter.findById(99);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByIdReserva debe retornar calificacion si existe")
    void findByIdReserva_debeRetornarCalificacionSiExiste() {
        CalificacionEntity entity = entityMock();
        Calificacion domain = domainMock();

        when(jpaRepository.findByIdReserva(1)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Calificacion> result = adapter.findByIdReserva(1);

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("findAll debe retornar lista de calificaciones")
    void findAll_debeRetornarLista() {
        CalificacionEntity entity = entityMock();
        Calificacion domain = domainMock();

        when(jpaRepository.findAll()).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Calificacion> result = adapter.findAll();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("findByIdTerapeuta debe retornar calificaciones del terapeuta")
    void findByIdTerapeuta_debeRetornarCalificaciones() {
        CalificacionEntity entity = entityMock();
        Calificacion domain = domainMock();

        when(jpaRepository.findByIdTerapeuta(2)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Calificacion> result = adapter.findByIdTerapeuta(2);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("existsByIdReserva debe retornar true si existe")
    void existsByIdReserva_debeRetornarTrue() {
        when(jpaRepository.existsByIdReserva(1)).thenReturn(true);

        assertTrue(adapter.existsByIdReserva(1));
    }

    @Test
    @DisplayName("promedioByIdTerapeuta debe retornar promedio correctamente")
    void promedioByIdTerapeuta_debeRetornarPromedio() {
        when(jpaRepository.promedioByIdTerapeuta(2)).thenReturn(4.5);

        assertEquals(4.5, adapter.promedioByIdTerapeuta(2));
    }
}