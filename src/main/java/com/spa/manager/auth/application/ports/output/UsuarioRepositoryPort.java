package com.spa.manager.auth.application.ports.output;
import java.util.Optional;
import com.spa.manager.auth.domain.model.Usuario;


public interface UsuarioRepositoryPort {
    Optional<Usuario> findBycorreo(String correo);
    boolean existsBycorreo(String correo);
    Usuario save(Usuario usuario);
}
