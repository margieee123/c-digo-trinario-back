package com.spa.manager.servicios.domain.exception;

public class ServicioNoEncontradoException extends RuntimeException {
    public ServicioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}