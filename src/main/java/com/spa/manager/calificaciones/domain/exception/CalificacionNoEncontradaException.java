package com.spa.manager.calificaciones.domain.exception;

public class CalificacionNoEncontradaException extends RuntimeException {
    public CalificacionNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}