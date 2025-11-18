package com.sistema.contas.auth.adapters.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
