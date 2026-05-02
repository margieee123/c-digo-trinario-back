package com.spa.manager.shared.config;

import com.spa.manager.auth.domain.exception.CorreoYaRegistradoException;
import com.spa.manager.auth.domain.exception.CredencialesInvalidasException;
import com.spa.manager.auth.domain.exception.UsuarioInactivoException;
import com.spa.manager.auth.domain.exception.UsuarioNoEncontradoException;
import com.spa.manager.facturas.domain.exception.FacturaNoEncontradaException;
import com.spa.manager.reservas.domain.exception.HorarioNoDisponibleException;
import com.spa.manager.reservas.domain.exception.ReservaNoEncontradaException;
import com.spa.manager.servicios.domain.exception.ServicioNoEncontradoException;
import com.spa.manager.calificaciones.domain.exception.CalificacionNoEncontradaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioNoEncontrado(UsuarioNoEncontradoException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ServicioNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> handleServicioNoEncontrado(ServicioNoEncontradoException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ReservaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleReservaNoEncontrada(ReservaNoEncontradaException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(FacturaNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleFacturaNoEncontrada(FacturaNoEncontradaException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(HorarioNoDisponibleException.class)
    public ResponseEntity<Map<String, Object>> handleHorarioNoDisponible(HorarioNoDisponibleException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CorreoYaRegistradoException.class)
    public ResponseEntity<Map<String, Object>> handleCorreoYaRegistrado(CorreoYaRegistradoException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<Map<String, Object>> handleCredencialesInvalidas(CredencialesInvalidasException ex) {
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UsuarioInactivoException.class)
    public ResponseEntity<Map<String, Object>> handleUsuarioInactivo(UsuarioInactivoException ex) {
        return error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor.");
    }
    @ExceptionHandler(CalificacionNoEncontradaException.class)
    public ResponseEntity<Map<String, Object>> handleCalificacionNoEncontrada(CalificacionNoEncontradaException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String mensaje) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("mensaje", mensaje);
        return ResponseEntity.status(status).body(body);
    }
}