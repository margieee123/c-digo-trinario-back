package com.spa.manager.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.from.email}")
    private String brevoFromEmail;

    @Value("${brevo.from.name}")
    private String brevoFromName;

    @Value("${server.port}")
    private String serverPort;

    public String getDbUrl() { return dbUrl; }
    public String getDbUsername() { return dbUsername; }
    public String getJwtSecret() { return jwtSecret; }
    public Long getJwtExpiration() { return jwtExpiration; }
    public String getBrevoApiKey() { return brevoApiKey; }
    public String getBrevoFromEmail() { return brevoFromEmail; }
    public String getBrevoFromName() { return brevoFromName; }
    public String getServerPort() { return serverPort; }
}