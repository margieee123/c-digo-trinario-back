package com.spa.manager.usuarios.application.service;

import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Usuario;
import com.spa.manager.auth.domain.exception.UsuarioNoEncontradoException;
import com.spa.manager.auth.application.ports.output.UsuarioRepositoryPort;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;
import com.spa.manager.usuarios.application.ports.input.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements
        ListarUsuariosUseCase,
        ObtenerUsuarioUseCase,
        CambiarEstadoUsuarioUseCase,
        ObtenerPerfilUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;

    public UsuarioService(UsuarioRepositoryPort usuarioRepositoryPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
    }

    @Override
    public List<UsuarioResponse> listar() {
        return usuarioRepositoryPort.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse obtener(Integer id) {
        Usuario usuario = usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + id));
        return toResponse(usuario);
    }

    @Override
    public UsuarioResponse cambiarEstado(Integer id, Estado estado) {
        Usuario usuario = usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + id));
        usuario.setEstado(estado);
        return toResponse(usuarioRepositoryPort.save(usuario));
    }

    @Override
    public UsuarioResponse obtenerPerfil(String correo) {
        Usuario usuario = usuarioRepositoryPort.findBycorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        return toResponse(usuario);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getEstado()
        );
    }
}