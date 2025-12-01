package com.huertohogar.repository;

import com.huertohogar.entity.Carrito;
import com.huertohogar.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITORIO CARRITO
 */
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    /**
     * Busca el carrito de un usuario
     */
    Optional<Carrito> findByUsuario(Usuario usuario);

    /**
     * Busca el carrito por ID de usuario
     */
    Optional<Carrito> findByUsuarioId(Long usuarioId);

    /**
     * Elimina el carrito de un usuario
     */
    void deleteByUsuario(Usuario usuario);
}