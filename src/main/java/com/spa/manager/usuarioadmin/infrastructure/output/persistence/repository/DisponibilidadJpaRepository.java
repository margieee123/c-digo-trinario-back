package com.spa.manager.usuarioadmin.infrastructure.output.persistence.repository;

import com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity.DisponibilidadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DisponibilidadJpaRepository extends JpaRepository<DisponibilidadEntity, Integer> {
    List<DisponibilidadEntity> findByIdTerapeuta(Integer idTerapeuta);
}