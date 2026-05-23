package com.spa.manager.auth.application.dto;

import com.spa.manager.auth.domain.model.Rol;

public class AuthResponse {
    private String token;
    private int idUsuario;
    private String nombre;
    private String correo;
    private Rol rol;

    public AuthResponse() {

    }
    public AuthResponse(String token, int idUsuario, String nombre, String correo, Rol rol) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
    }


    public String getToken() {
        return token;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    public Rol getRol() {
        return rol;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
