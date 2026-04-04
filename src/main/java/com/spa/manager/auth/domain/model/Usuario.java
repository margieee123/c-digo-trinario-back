package com.spa.manager.auth.domain.model;

public class Usuario
{
    private Integer id;
    private String nombre;
    private String correo;
    private String passwordhash;
    private Rol rol;
    private Estado estado;

    public Usuario(){

    }

    public Usuario(Integer id, String nombre, String correo, Estado estado, Rol rol, String passwordhash) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.estado = estado;
        this.rol = rol;
        this.passwordhash = passwordhash;
    }
    public boolean estaActivo() {
        return Estado.activo.equals(this.estado);
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPasswordhash() {
        return passwordhash;
    }

    public Rol getRol() {
        return rol;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setPasswordhash(String passwordhash) {
        this.passwordhash = passwordhash;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
