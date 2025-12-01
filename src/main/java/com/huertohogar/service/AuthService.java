package com.huertohogar.service;

import com.huertohogar.dto.auth.AuthResponse;
import com.huertohogar.dto.auth.LoginRequest;
import com.huertohogar.dto.auth.RegisterRequest;
import com.huertohogar.dto.auth.UserDTO;
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

/**
 * AUTH SERVICE
 * 
 * CUMPLE CON PREGUNTAS:
 * - P33-35: Autenticación de usuarios
 * - P36-38: Generación y validación de JWT
 * 
 * Maneja login, registro y refresh token
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * PREGUNTA P33-35: ¿Cómo autentican usuarios?
     * RESPUESTA: Login con email/password, retorna JWT
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        // Autenticar con Spring Security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Cargar usuario
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        // Generar tokens
        String token = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(convertToUserDTO(usuario))
                .build();
    }

    /**
     * Registro de nuevo usuario
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Validar que el email no exista
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El email ya está registrado");
        }

        // Crear usuario
        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .comuna(request.getComuna())
                .region(request.getRegion())
                .rol(Usuario.Rol.ROLE_USER)
                .activo(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        // Generar tokens
        String token = jwtService.generateToken(usuario);
        String refreshToken = jwtService.generateRefreshToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(convertToUserDTO(usuario))
                .build();
    }

    /**
     * PREGUNTA P38: ¿Qué hacen cuando un token expira?
     * RESPUESTA: Refresh token permite renovar sin re-login
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        String userEmail = jwtService.extractUsername(refreshToken);
        
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        if (!jwtService.isTokenValid(refreshToken, usuario)) {
            throw new BadRequestException("Refresh token inválido o expirado");
        }

        String newToken = jwtService.generateToken(usuario);
        String newRefreshToken = jwtService.generateRefreshToken(usuario);

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .user(convertToUserDTO(usuario))
                .build();
    }

    /**
     * Convierte Usuario entity a UserDTO
     */
    private UserDTO convertToUserDTO(Usuario usuario) {
        return UserDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .comuna(usuario.getComuna())
                .region(usuario.getRegion())
                .rol(usuario.getRol().name())
                .build();
    }
}