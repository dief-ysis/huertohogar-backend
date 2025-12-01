package com.huertohogar.service;

import com.huertohogar.dto.auth.UserDTO;
import com.huertohogar.entity.Usuario;
import com.huertohogar.exception.BadRequestException;
import com.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * USUARIO SERVICE
 * 
 * Gestión de usuarios
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Obtener usuario por email
     */
    @Transactional(readOnly = true)
    public UserDTO getUsuarioByEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));
        
        return convertToDTO(usuario);
    }

    /**
     * Obtener usuario por ID
     */
    @Transactional(readOnly = true)
    public UserDTO getUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id));
        
        return convertToDTO(usuario);
    }

    /**
     * Actualizar perfil de usuario
     */
    @Transactional
    public UserDTO actualizarPerfil(String email, UserDTO userDTO) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        // Actualizar campos
        if (userDTO.getNombre() != null) {
            usuario.setNombre(userDTO.getNombre());
        }
        if (userDTO.getTelefono() != null) {
            usuario.setTelefono(userDTO.getTelefono());
        }
        if (userDTO.getDireccion() != null) {
            usuario.setDireccion(userDTO.getDireccion());
        }
        if (userDTO.getComuna() != null) {
            usuario.setComuna(userDTO.getComuna());
        }
        if (userDTO.getRegion() != null) {
            usuario.setRegion(userDTO.getRegion());
        }

        usuario = usuarioRepository.save(usuario);
        return convertToDTO(usuario);
    }

    /**
     * Cambiar contraseña
     */
    @Transactional
    public void cambiarPassword(String email, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        // Verificar password actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new BadRequestException("Contraseña actual incorrecta");
        }

        // Actualizar password
        usuario.setPassword(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }

    /**
     * Desactivar cuenta
     */
    @Transactional
    public void desactivarCuenta(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "email", email));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Activar cuenta (ADMIN)
     */
    @Transactional
    public void activarCuenta(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    /**
     * Obtener todos los usuarios (ADMIN)
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener usuarios por rol (ADMIN)
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getUsuariosByRol(Usuario.Rol rol) {
        return usuarioRepository.findByRolAndActivoTrue(rol)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cambiar rol de usuario (ADMIN)
     */
    @Transactional
    public UserDTO cambiarRol(Long usuarioId, Usuario.Rol nuevoRol) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        usuario.setRol(nuevoRol);
        usuario = usuarioRepository.save(usuario);
        
        return convertToDTO(usuario);
    }

    /**
     * Verificar si un email ya existe
     */
    @Transactional(readOnly = true)
    public boolean existeEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /**
     * Convertir Usuario entity a UserDTO
     */
    private UserDTO convertToDTO(Usuario usuario) {
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