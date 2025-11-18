package com.sistema.contas.auth.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final SecretKey jwtSecretKey; // Injetando a chave secreta centralizada

    public String createToken(Authentication authentication) {
        String username = authentication.getName();
        var authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        Date now = new Date();
        Date validity = new Date(now.getTime() + 3600000); // 1 hora

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authorities)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256) // Usando a chave injetada
                .compact();
    }
}
