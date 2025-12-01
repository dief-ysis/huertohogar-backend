package com.huertohogar.repository;

import com.huertohogar.entity.Carrito;
import com.huertohogar.entity.CarritoItem;
import com.huertohogar.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITORIO CARRITO ITEM
 */
@Repository
public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    /**
     * Busca un item específico en el carrito
     */
    Optional<CarritoItem> findByCarritoAndProducto(Carrito carrito, Producto producto);

    /**
     * Elimina todos los items de un carrito
     */
    void deleteByCarrito(Carrito carrito);
}