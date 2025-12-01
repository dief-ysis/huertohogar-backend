package com.huertohogar.repository;

import com.huertohogar.entity.Pedido;
import com.huertohogar.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO PEDIDO
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Busca pedido por número de pedido
     */
    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    /**
     * Busca pedidos de un usuario con paginación
     */
    Page<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario, Pageable pageable);

    /**
     * Busca pedidos de un usuario por ID
     */
    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    /**
     * Busca pedidos por estado
     */
    List<Pedido> findByEstadoOrderByFechaCreacionDesc(Pedido.EstadoPedido estado);

    /**
     * Verifica si existe un pedido con el número dado
     */
    boolean existsByNumeroPedido(String numeroPedido);
}