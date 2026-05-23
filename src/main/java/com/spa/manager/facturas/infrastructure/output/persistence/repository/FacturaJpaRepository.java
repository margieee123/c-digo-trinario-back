package com.spa.manager.facturas.infrastructure.output.persistence.repository;

import com.spa.manager.facturas.infrastructure.output.persistence.entity.FacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface FacturaJpaRepository extends JpaRepository<FacturaEntity, Integer> {
    Optional<FacturaEntity> findByIdReserva(Integer idReserva);
    boolean existsByIdReserva(Integer idReserva);
}