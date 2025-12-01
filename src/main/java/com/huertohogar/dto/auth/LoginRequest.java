package com.huertohogar.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Email(message = "Email inválido")
    @NotBlank(message = "Email requerido")
    private String email;
    
    @NotBlank(message = "Contraseña requerida")
    private String password;
}