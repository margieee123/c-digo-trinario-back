package com.spa.manager.auth.application.ports.output;

import com.spa.manager.auth.domain.model.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> findBycorreo(String correo);
    boolean existsBycorreo(String correo);
    Usuario save(Usuario usuario);

    // Gestión de usuarios
    List<Usuario> findAll();
    Optional<Usuario> findById(Integer id);
}