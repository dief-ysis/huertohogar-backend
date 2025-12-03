package com.huertohogar.controller;

import com.huertohogar.dto.auth.UserDTO;
import com.huertohogar.dto.user.ChangePasswordRequest;
import com.huertohogar.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Obtener mi perfil
    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(Authentication authentication) {
        return ResponseEntity.ok(usuarioService.getUsuarioByEmail(authentication.getName()));
    }

    // Actualizar mis datos
    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateProfile(
            Authentication authentication,
            @RequestBody UserDTO userDTO
    ) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(authentication.getName(), userDTO));
    }

    // Cambiar contraseña
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        usuarioService.cambiarPassword(
                authentication.getName(), 
                request.getOldPassword(), 
                request.getNewPassword()
        );
        return ResponseEntity.noContent().build();
    }
}