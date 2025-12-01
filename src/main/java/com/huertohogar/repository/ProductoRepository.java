package com.huertohogar.repository;

import com.huertohogar.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * REPOSITORIO PRODUCTO
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /**
     * Busca productos por categoría con paginación
     */
    Page<Producto> findByCategoriaAndActivoTrue(String categoria, Pageable pageable);

    /**
     * Busca productos destacados activos
     */
    List<Producto> findByDestacadoTrueAndActivoTrue();

    /**
     * Busca productos activos con paginación
     */
    Page<Producto> findByActivoTrue(Pageable pageable);

    /**
     * Búsqueda por nombre o descripción
     */
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Producto> buscarPorTexto(@Param("query") String query, Pageable pageable);

    /**
     * Obtiene todas las categorías únicas
     */
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.activo = true ORDER BY p.categoria")
    List<String> findAllCategorias();

    /**
     * Busca productos con descuento
     */
    List<Producto> findByActivoTrueAndDescuentoGreaterThan(java.math.BigDecimal descuento);
}