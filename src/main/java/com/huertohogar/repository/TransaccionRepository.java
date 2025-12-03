package com.huertohogar.repository;

import com.huertohogar.entity.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    Optional<Transaccion> findByBuyOrder(String buyOrder);
    Optional<Transaccion> findByToken(String token);
    List<Transaccion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    List<Transaccion> findByEstadoOrderByFechaCreacionDesc(Transaccion.EstadoTransaccion estado);
}