package com.spa.manager.usuarioadmin.infrastructure.output.persistence.repository;

import com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity.TerapeutaServicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TerapeutaServicioJpaRepository extends
        JpaRepository<TerapeutaServicioEntity, TerapeutaServicioEntity.TerapeutaServicioId> {

    List<TerapeutaServicioEntity> findByIdTerapeuta(Integer idTerapeuta);

    List<TerapeutaServicioEntity> findByIdServicio(Integer idServicio);

    boolean existsByIdTerapeutaAndIdServicio(Integer idTerapeuta, Integer idServicio);

    void deleteByIdTerapeutaAndIdServicio(Integer idTerapeuta, Integer idServicio);

    @Query("SELECT t.idServicio FROM TerapeutaServicioEntity t WHERE t.idTerapeuta = :idTerapeuta")
    List<Integer> findServicioIdsByIdTerapeuta(@Param("idTerapeuta") Integer idTerapeuta);

    @Query("SELECT t.idTerapeuta FROM TerapeutaServicioEntity t WHERE t.idServicio = :idServicio")
    List<Integer> findTerapeutaIdsByIdServicio(@Param("idServicio") Integer idServicio);
}