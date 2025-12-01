package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * ENTIDAD USUARIO
 * 
 * CUMPLE CON PREGUNTAS:
 * - P30-32: Implementación de roles (ROLE_USER, ROLE_ADMIN)
 * - P33-35: Gestión de estado autenticado
 * - P36-38: Gestión de tokens JWT
 * 
 * IMPLEMENTA UserDetails para integración con Spring Security
 */
@Entity
@Table(name = "usuarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 15)
    private String telefono;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String comuna;

    @Column(length = 100)
    private String region;

    /**
     * ROL DEL USUARIO
     * 
     * PREGUNTA P30-32: ¿Cómo implementaron roles?
     * RESPUESTA: Enum con ROLE_USER y ROLE_ADMIN
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Rol rol = Rol.ROLE_USER;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    // =========================================
    // USERDETAILS IMPLEMENTATION
    // =========================================

    /**
     * PREGUNTA P33: ¿Cómo saben si un usuario está autenticado?
     * RESPUESTA: Spring Security valida el token JWT y carga este UserDetails
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }

    // =========================================
    // ENUM ROL
    // =========================================

    /**
     * PREGUNTA P30-32: ¿Cómo están definidos los roles?
     * 
     * RESPUESTA: Enum con dos roles:
     * - ROLE_USER: Usuario normal
     * - ROLE_ADMIN: Administrador con permisos completos
     */
    public enum Rol {
        ROLE_USER,
        ROLE_ADMIN
    }
}