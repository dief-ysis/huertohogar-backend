package com.huertohogar.dto.auth;
import lombok.Data;
@Data
public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    // Campos opcionales para perfil inicial
    private String telefono;
    private String direccion;
}