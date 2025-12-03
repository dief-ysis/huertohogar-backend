package com.huertohogar.repository;

import com.huertohogar.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Buscar solo productos activos
    Page<Producto> findByActivoTrue(Pageable pageable);

    // Buscar por categoría
    Page<Producto> findByCategoriaAndActivoTrue(String categoria, Pageable pageable);

    // Buscar productos destacados
    List<Producto> findByDestacadoTrueAndActivoTrue();

    // Buscar ofertas (descuento > 0)
    List<Producto> findByActivoTrueAndDescuentoGreaterThan(BigDecimal descuento);

    // Búsqueda por texto (Nombre o Descripción)
    @Query("SELECT p FROM Producto p WHERE p.activo = true AND " +
           "(LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Producto> buscarPorTexto(@Param("query") String query, Pageable pageable);

    // Obtener lista de categorías únicas
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.activo = true")
    List<String> findAllCategorias();
}