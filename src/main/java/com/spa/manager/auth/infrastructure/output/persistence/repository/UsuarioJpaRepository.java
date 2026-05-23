package com.spa.manager.auth.infrastructure.output.persistence.repository;

import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<UsuarioEntity, Integer> {
    Optional<UsuarioEntity> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    List<UsuarioEntity> findByRol(Rol rol);
}