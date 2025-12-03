package com.huertohogar.repository;

import com.huertohogar.entity.Carrito;
import com.huertohogar.entity.CarritoItem;
import com.huertohogar.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
    Optional<CarritoItem> findByCarritoAndProducto(Carrito carrito, Producto producto);
    void deleteByCarrito(Carrito carrito);
}