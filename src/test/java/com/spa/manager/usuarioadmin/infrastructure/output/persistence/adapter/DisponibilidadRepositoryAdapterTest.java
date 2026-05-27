package com.spa.manager.usuarioadmin.infrastructure.output.persistence.adapter;

import com.spa.manager.usuarioadmin.domain.model.DiaSemana;
import com.spa.manager.usuarioadmin.domain.model.Disponibilidad;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity.DisponibilidadEntity;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.mapper.DisponibilidadMapper;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.repository.DisponibilidadJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - DisponibilidadRepositoryAdapter")
class DisponibilidadRepositoryAdapterTest {

    @Mock private DisponibilidadJpaRepository jpaRepository;
    @Mock private DisponibilidadMapper mapper;
    @InjectMocks private DisponibilidadRepositoryAdapter adapter;

    private DisponibilidadEntity entityMock() {
        DisponibilidadEntity entity = new DisponibilidadEntity();
        entity.setIdDisponibilidad(1);
        entity.setIdTerapeuta(2);
        entity.setDiaSemana(DiaSemana.lunes);
        entity.setHoraInicio(LocalTime.of(8, 0));
        entity.setHoraFin(LocalTime.of(17, 0));
        return entity;
    }

    private Disponibilidad domainMock() {
        Disponibilidad d = new Disponibilidad();
        d.setIdDisponibilidad(1);
        d.setIdTerapeuta(2);
        d.setDiaSemana(DiaSemana.lunes);
        d.setHoraInicio(LocalTime.of(8, 0));
        d.setHoraFin(LocalTime.of(17, 0));
        return d;
    }

    @Test
    @DisplayName("save debe guardar y retornar disponibilidad")
    void save_debeGuardarYRetornarDisponibilidad() {
        Disponibilidad domain = domainMock();
        DisponibilidadEntity entity = entityMock();

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(domain);

        Disponibilidad result = adapter.save(domain);

        assertNotNull(result);
        verify(jpaRepository).save(entity);
    }

    @Test
    @DisplayName("findById debe retornar disponibilidad si existe")
    void findById_debeRetornarDisponibilidadSiExiste() {
        DisponibilidadEntity entity = entityMock();
        Disponibilidad domain = domainMock();

        when(jpaRepository.findById(1)).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        Optional<Disponibilidad> result = adapter.findById(1);

        assertTrue(result.isPresent());
    }

    @Test
    @DisplayName("findById debe retornar empty si no existe")
    void findById_debeRetornarEmptySiNoExiste() {
        when(jpaRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Disponibilidad> result = adapter.findById(99);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("findByIdTerapeuta debe retornar lista de disponibilidades")
    void findByIdTerapeuta_debeRetornarLista() {
        DisponibilidadEntity entity = entityMock();
        Disponibilidad domain = domainMock();

        when(jpaRepository.findByIdTerapeuta(2)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);

        List<Disponibilidad> result = adapter.findByIdTerapeuta(2);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("deleteById debe llamar al repositorio")
    void deleteById_debeLlamarAlRepositorio() {
        adapter.deleteById(1);
        verify(jpaRepository).deleteById(1);
    }
}