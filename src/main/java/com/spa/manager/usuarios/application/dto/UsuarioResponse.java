package com.spa.manager.usuarios.application.dto;

import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;

public class UsuarioResponse {

    private Integer id;
    private String nombre;
    private String correo;
    private Rol rol;
    private Estado estado;

    public UsuarioResponse() {}

    public UsuarioResponse(Integer id, String nombre, String correo, Rol rol, Estado estado) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Rol getRol() { return rol; }
    public void setRol(Rol rol) { this.rol = rol; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}