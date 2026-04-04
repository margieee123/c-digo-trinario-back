package com.spa.manager.auth.application.dto;

public class LoginCommand {

    private String correo;
    private String password;
    private String ip;

    public LoginCommand() {}

    public LoginCommand(String correo, String password, String ip) {
        this.correo = correo;
        this.password = password;
        this.ip = ip;
    }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
}