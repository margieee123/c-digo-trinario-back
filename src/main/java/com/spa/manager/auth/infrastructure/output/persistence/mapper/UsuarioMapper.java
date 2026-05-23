package com.spa.manager.auth.infrastructure.output.persistence.mapper;

import com.spa.manager.auth.domain.model.Usuario;
import com.spa.manager.auth.infrastructure.output.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        return new Usuario(
                entity.getId(),
                entity.getNombre(),
                entity.getCorreo(),
                entity.getEstado(),
                entity.getRol(),
                entity.getPasswordHash()
        );
    }

    public UsuarioEntity toEntity(Usuario domain) {
        return new UsuarioEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getCorreo(),
                domain.getPasswordhash(),
                domain.getRol(),
                domain.getEstado()
        );
    }
}