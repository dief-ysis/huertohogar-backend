package com.huertohogar.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Nombre requerido")
    @Size(min = 3, max = 100)
    private String nombre;
    
    @Email(message = "Email inválido")
    @NotBlank(message = "Email requerido")
    private String email;
    
    @NotBlank(message = "Contraseña requerida")
    @Size(min = 6, message = "Contraseña debe tener al menos 6 caracteres")
    private String password;
    
    private String telefono;
    private String direccion;
    private String comuna;
    private String region;
}