package com.huertohogar.repository;

import com.huertohogar.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORIO TRANSACCION
 * 
 * CUMPLE CON PREGUNTA P102-138:
 * - Gestión de transacciones Webpay
 */
@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {

    /**
     * Busca transacción por buyOrder
     */
    Optional<Transaccion> findByBuyOrder(String buyOrder);

    /**
     * Busca transacción por token de Webpay
     */
    Optional<Transaccion> findByToken(String token);

    /**
     * Busca transacciones de un usuario
     */
    List<Transaccion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);

    /**
     * Busca transacciones por estado
     */
    List<Transaccion> findByEstadoOrderByFechaCreacionDesc(Transaccion.EstadoTransaccion estado);
}