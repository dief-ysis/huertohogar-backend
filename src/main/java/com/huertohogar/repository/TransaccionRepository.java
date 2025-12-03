package com.huertohogar.repository;

import com.huertohogar.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    Optional<Transaccion> findByToken(String token);
    List<Transaccion> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
    Optional<Transaccion> findByBuyOrder(String buyOrder);
}