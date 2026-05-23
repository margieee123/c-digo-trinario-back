package com.spa.manager.auth.application.ports.output;

public interface PasswordEncoderPort {
    String encode(String rawPassword);
    boolean matches(String rawPassword,  String encodedPassword);
}
