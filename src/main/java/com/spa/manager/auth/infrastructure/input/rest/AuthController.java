package com.spa.manager.auth.infrastructure.input.rest;

import com.spa.manager.auth.application.dto.AuthResponse;
import com.spa.manager.auth.application.dto.LoginCommand;
import com.spa.manager.auth.application.dto.RegisterCommand;
import com.spa.manager.auth.application.ports.input.LoginUseCase;
import com.spa.manager.auth.application.ports.input.RegisterUseCase;
import com.spa.manager.auth.infrastructure.input.rest.dto.LoginRequest;
import com.spa.manager.auth.infrastructure.input.rest.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    public AuthController(LoginUseCase loginUseCase, RegisterUseCase registerUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request,
                                              HttpServletRequest httpRequest) {
        String ip = obtenerIp(httpRequest);
        LoginCommand command = new LoginCommand(request.getCorreo(), request.getPassword(), ip);
        AuthResponse response = loginUseCase.login(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request,
                                                 HttpServletRequest httpRequest) {
        String ip = obtenerIp(httpRequest);
        RegisterCommand command = new RegisterCommand(request.getNombre(), request.getCorreo(),
                request.getPassword(), ip);
        AuthResponse response = registerUseCase.register(command);
        return ResponseEntity.status(201).body(response);
    }

    private String obtenerIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}