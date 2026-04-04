package com.spa.manager.auth.application.ports.output;

import com.spa.manager.auth.domain.model.Usuario;

public interface JwtServicePort {
    String generateToken(Usuario usuario);
    String extractCorreo(String token);
    boolean isTokenValid(String token, String correo);
}
