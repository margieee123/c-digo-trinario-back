package com.spa.manager.logs.infrastructure.input.rest;

import com.spa.manager.logs.application.dto.LogResponse;
import com.spa.manager.logs.application.ports.input.ListarLogsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
@Tag(name = "Logs", description = "Registro de actividad del sistema")
@SecurityRequirement(name = "bearerAuth")
public class LogController {

    private final ListarLogsUseCase listarLogsUseCase;

    public LogController(ListarLogsUseCase listarLogsUseCase) {
        this.listarLogsUseCase = listarLogsUseCase;
    }

    @GetMapping
    @PreAuthorize("hasRole('administrador')")
    @Operation(summary = "Listar logs recientes")
    public ResponseEntity<List<LogResponse>> listarRecientes(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(listarLogsUseCase.listarRecientes(limit));
    }
}