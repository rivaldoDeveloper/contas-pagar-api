package com.sistema.contas.auth.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class TokenConfig {

    // Chave foi hardcodada para depuração, eliminando a leitura do application.yml
    private final String secretKeyString = "testesenhasecreta_testesenhasecreta_testesenhasecreta";

    @Bean
    public SecretKey jwtSecretKey() {
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        // A chave para HS256 deve ter pelo menos 256 bits (32 bytes)
        // A string acima já tem mais de 32 bytes, então o padding não é mais necessário.
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
