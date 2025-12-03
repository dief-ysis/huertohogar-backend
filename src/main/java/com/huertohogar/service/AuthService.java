package com.huertohogar.service;

import com.huertohogar.dto.auth.*;
import com.huertohogar.entity.Usuario;
import com.huertohogar.exception.BadRequestException;
import com.huertohogar.repository.UsuarioRepository;
import com.huertohogar.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* Principio: Token Management. Genera access token (corto plazo) y refresh token (largo plazo). */

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Validar credenciales con Spring Security
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // 2. Obtener usuario (si llegamos aquí, la password es correcta)
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        // 3. Generar tokens
        return construirAuthResponse(usuario);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está en uso");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // SIEMPRE encriptar
                .rol(Usuario.Rol.ROLE_USER)
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);
        return construirAuthResponse(usuario);
    }
    
    private AuthResponse construirAuthResponse(Usuario usuario) {
        String token = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);
        
        // Mapeo simple a DTO
        UserDTO userDTO = UserDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userDTO)
                .build();
    }
}