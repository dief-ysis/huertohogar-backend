package com.huertohogar.dto.auth;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private String direccion;
    private String telefono;
    private String comuna;
    private String region;
}