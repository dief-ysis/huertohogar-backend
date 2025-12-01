package com.huertohogar.repository;

import com.huertohogar.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITORIO USUARIO
 * 
 * CUMPLE CON PREGUNTA P33-35:
 * - Buscar usuario por email para autenticación
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por email (usado para login)
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el email dado
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios activos por rol
     */
    java.util.List<Usuario> findByRolAndActivoTrue(Usuario.Rol rol);
}