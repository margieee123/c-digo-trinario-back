package com.spa.manager.shared.configuracion.infrastructure.output.persistence.repository;

import com.spa.manager.shared.configuracion.infrastructure.output.persistence.entity.ConfiguracionSpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionSpaJpaRepository extends JpaRepository<ConfiguracionSpaEntity, Integer> {
}