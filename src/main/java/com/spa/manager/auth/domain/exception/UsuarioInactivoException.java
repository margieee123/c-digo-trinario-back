package com.spa.manager.auth.domain.exception;

public class UsuarioInactivoException extends RuntimeException {

    public UsuarioInactivoException(String mensaje) {
        super(mensaje);
    }
}
