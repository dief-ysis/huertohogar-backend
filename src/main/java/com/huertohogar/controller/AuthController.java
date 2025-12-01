package com.huertohogar.controller;

import com.huertohogar.dto.auth.AuthResponse;
import com.huertohogar.dto.auth.LoginRequest;
import com.huertohogar.dto.auth.RegisterRequest;
import com.huertohogar.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AUTH CONTROLLER
 * 
 * Endpoints de autenticación
 * 
 * CUMPLE CON PREGUNTAS:
 * - P33-35: Login y registro
 * - P36-38: JWT tokens
 * - P45-60: API REST
 */
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de autenticación y registro")
public class AuthController {

    private final AuthService authService;

    /**
     * Login de usuario
     * 
     * @param request Email y contraseña
     * @return JWT token y datos del usuario
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autenticación con email y contraseña")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Registro de nuevo usuario
     * 
     * @param request Datos del usuario
     * @return JWT token y datos del usuario
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar usuario", description = "Crear cuenta nueva")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Renovar token JWT
     * 
     * @param refreshToken Refresh token
     * @return Nuevo JWT token
     */
    @PostMapping("/refresh")
    @Operation(summary = "Renovar token", description = "Obtener nuevo token usando refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        // Extraer token del header (después de "Bearer ")
        String token = refreshToken.replace("Bearer ", "");
        AuthResponse response = authService.refreshToken(token);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout (opcional - el token simplemente expira)
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Invalidar sesión actual")
    public ResponseEntity<String> logout() {
        // En JWT stateless, el logout es manejado por el frontend eliminando el token
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }
}