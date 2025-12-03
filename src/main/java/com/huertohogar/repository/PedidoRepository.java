package com.huertohogar.repository;

import com.huertohogar.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    Page<Pedido> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario, Pageable pageable);
    boolean existsByNumeroPedido(String numeroPedido);
    List<Pedido> findByEstadoOrderByFechaCreacionDesc(Pedido.EstadoPedido estado);
}