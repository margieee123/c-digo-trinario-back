package com.spa.manager.facturas.domain.exception;

public class FacturaNoEncontradaException extends RuntimeException {
    public FacturaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}