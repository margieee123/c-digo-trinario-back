package com.spa.manager.servicios.infrastructure.output.persistence.repository;

import com.spa.manager.servicios.infrastructure.output.persistence.entity.ServicioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServicioJpaRepository extends JpaRepository<ServicioEntity, Integer> {
    List<ServicioEntity> findByNombreContainingIgnoreCase(String nombre);
}