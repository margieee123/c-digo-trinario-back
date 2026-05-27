package com.spa.manager.usuarioadmin.infrastructure.output.persistence.adapter;

import com.spa.manager.usuarioadmin.domain.model.TerapeutaServicio;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity.TerapeutaServicioEntity;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.mapper.TerapeutaServicioMapper;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.repository.TerapeutaServicioJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias - TerapeutaServicioRepositoryAdapter")
class TerapeutaServicioRepositoryAdapterTest {

    @Mock private TerapeutaServicioJpaRepository jpaRepository;
    @Mock private TerapeutaServicioMapper mapper;
    @InjectMocks private TerapeutaServicioRepositoryAdapter adapter;

    @Test
    @DisplayName("save debe guardar terapeuta servicio")
    void save_debeGuardarTerapeutaServicio() {
        TerapeutaServicio domain = new TerapeutaServicio(1, 2);
        TerapeutaServicioEntity entity = new TerapeutaServicioEntity();
        entity.setIdTerapeuta(1);
        entity.setIdServicio(2);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(entity);

        adapter.save(domain);

        verify(jpaRepository).save(entity);
    }

    @Test
    @DisplayName("delete debe eliminar terapeuta servicio")
    void delete_debeEliminarTerapeutaServicio() {
        adapter.delete(1, 2);
        verify(jpaRepository).deleteByIdTerapeutaAndIdServicio(1, 2);
    }

    @Test
    @DisplayName("findServiciosByIdTerapeuta debe retornar lista de ids")
    void findServiciosByIdTerapeuta_debeRetornarLista() {
        when(jpaRepository.findServicioIdsByIdTerapeuta(1)).thenReturn(List.of(1, 2, 3));

        List<Integer> result = adapter.findServiciosByIdTerapeuta(1);

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("findTerapeutasByIdServicio debe retornar lista de ids")
    void findTerapeutasByIdServicio_debeRetornarLista() {
        when(jpaRepository.findTerapeutaIdsByIdServicio(2)).thenReturn(List.of(1, 2));

        List<Integer> result = adapter.findTerapeutasByIdServicio(2);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("exists debe retornar true si existe la relacion")
    void exists_debeRetornarTrue() {
        when(jpaRepository.existsByIdTerapeutaAndIdServicio(1, 2)).thenReturn(true);

        assertTrue(adapter.exists(1, 2));
    }

    @Test
    @DisplayName("exists debe retornar false si no existe la relacion")
    void exists_debeRetornarFalse() {
        when(jpaRepository.existsByIdTerapeutaAndIdServicio(1, 99)).thenReturn(false);

        assertFalse(adapter.exists(1, 99));
    }
}