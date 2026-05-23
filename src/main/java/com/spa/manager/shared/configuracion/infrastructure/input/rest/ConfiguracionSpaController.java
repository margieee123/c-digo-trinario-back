package com.spa.manager.shared.configuracion.infrastructure.input.rest;

import com.spa.manager.shared.configuracion.application.dto.ConfiguracionSpaDto;
import com.spa.manager.shared.configuracion.application.service.ConfiguracionSpaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/configuracion-spa")
@Tag(name = "Configuración Spa", description = "Información general del spa")
@SecurityRequirement(name = "bearerAuth")
public class ConfiguracionSpaController {

    private final ConfiguracionSpaService service;

    public ConfiguracionSpaController(ConfiguracionSpaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Obtener configuración del spa")
    public ResponseEntity<ConfiguracionSpaDto> obtener() {
        return ResponseEntity.ok(service.obtener());
    }

    @PutMapping
    @PreAuthorize("hasRole('administrador')")
    @Operation(summary = "Guardar configuración del spa")
    public ResponseEntity<ConfiguracionSpaDto> guardar(@RequestBody ConfiguracionSpaDto dto) {
        return ResponseEntity.ok(service.guardar(dto));
    }
}