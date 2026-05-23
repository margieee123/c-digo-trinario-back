package com.spa.manager.auth.application.service;

import com.spa.manager.auth.application.dto.AuthResponse;
import com.spa.manager.auth.application.dto.LoginCommand;
import com.spa.manager.auth.application.dto.RegisterCommand;
import com.spa.manager.auth.application.ports.input.LoginUseCase;
import com.spa.manager.auth.application.ports.input.RegisterUseCase;
import com.spa.manager.auth.application.ports.output.JwtServicePort;
import com.spa.manager.auth.application.ports.output.PasswordEncoderPort;
import com.spa.manager.auth.application.ports.output.UsuarioRepositoryPort;
import com.spa.manager.auth.domain.exception.CorreoYaRegistradoException;
import com.spa.manager.auth.domain.exception.CredencialesInvalidasException;
import com.spa.manager.auth.domain.exception.UsuarioInactivoException;
import com.spa.manager.auth.domain.exception.UsuarioNoEncontradoException;
import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.domain.model.Usuario;
import com.spa.manager.logs.application.ports.input.RegistrarLogUseCase;
import com.spa.manager.logs.domain.model.Log;
import com.spa.manager.logs.domain.model.TipoLog;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements LoginUseCase, RegisterUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtServicePort jwtService;
    private final RegistrarLogUseCase logService;

    public AuthService(UsuarioRepositoryPort usuarioRepository,
                       PasswordEncoderPort passwordEncoder,
                       JwtServicePort jwtService,
                       RegistrarLogUseCase logService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.logService = logService;
    }

    @Override
    public AuthResponse login(LoginCommand command) {
        Usuario usuario;

        try {
            usuario = usuarioRepository.findBycorreo(command.getCorreo())
                    .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado."));
        } catch (UsuarioNoEncontradoException e) {
            logService.registrar(new Log(TipoLog.LOGIN_FALLIDO, null,
                    "Intento de login con correo no registrado: " + command.getCorreo(), command.getIp()));
            throw e;
        }

        if (!usuario.estaActivo()) {
            logService.registrar(new Log(TipoLog.LOGIN_FALLIDO, usuario.getId(),
                    "Intento de login con usuario inactivo: " + command.getCorreo(), command.getIp()));
            throw new UsuarioInactivoException("El usuario está inactivo.");
        }

        if (!passwordEncoder.matches(command.getPassword(), usuario.getPasswordhash())) {
            logService.registrar(new Log(TipoLog.LOGIN_FALLIDO, usuario.getId(),
                    "Contraseña incorrecta para: " + command.getCorreo(), command.getIp()));
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");
        }

        logService.registrar(new Log(TipoLog.LOGIN_EXITOSO, usuario.getId(),
                "Login exitoso: " + command.getCorreo(), command.getIp()));

        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, usuario.getId(), usuario.getNombre(),
                usuario.getCorreo(), usuario.getRol());
    }

    @Override
    public AuthResponse register(RegisterCommand command) {
        if (usuarioRepository.existsBycorreo(command.getCorreo())) {
            throw new CorreoYaRegistradoException("El correo ya está registrado.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(command.getNombre());
        nuevo.setCorreo(command.getCorreo());
        nuevo.setPasswordhash(passwordEncoder.encode(command.getPassword()));
        nuevo.setRol(Rol.cliente);
        nuevo.setEstado(Estado.activo);

        Usuario guardado = usuarioRepository.save(nuevo);

        logService.registrar(new Log(TipoLog.ACCION_USUARIO, guardado.getId(),
                "Nuevo usuario registrado: " + guardado.getCorreo(), command.getIp()));

        String token = jwtService.generateToken(guardado);
        return new AuthResponse(token, guardado.getId(), guardado.getNombre(),
                guardado.getCorreo(), guardado.getRol());
    }
}