package com.spa.manager.auth.infrastructure.output.persistence.adapter;

import com.spa.manager.auth.application.ports.output.UsuarioRepositoryPort;
import com.spa.manager.auth.domain.model.Usuario;
import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import com.spa.manager.auth.infrastructure.output.persistence.mapper.UsuarioMapper;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository jpaRepository;
    private final UsuarioMapper mapper;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository jpaRepository, UsuarioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Usuario> findBycorreo(String correo) {
        return jpaRepository.findByCorreo(correo)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsBycorreo(String correo) {
        return jpaRepository.existsByCorreo(correo);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = mapper.toEntity(usuario);
        UsuarioEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}