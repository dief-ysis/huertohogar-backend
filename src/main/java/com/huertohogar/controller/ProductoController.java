package com.huertohogar.controller;

import com.huertohogar.dto.product.ProductoDTO;
import com.huertohogar.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PRODUCTO CONTROLLER
 * 
 * Endpoints de gestión de productos
 */
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Listar todos los productos con paginación
     */
    @GetMapping
    @Operation(summary = "Listar productos", description = "Obtener todos los productos activos con paginación")
    public ResponseEntity<Page<ProductoDTO>> getAllProductos(
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        Page<ProductoDTO> productos = productoService.getAllProductos(pageable);
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtener producto por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Obtener detalles de un producto por ID")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
        ProductoDTO producto = productoService.getProductoById(id);
        return ResponseEntity.ok(producto);
    }

    /**
     * Buscar productos por categoría
     */
    @GetMapping("/category/{categoria}")
    @Operation(summary = "Productos por categoría", description = "Filtrar productos por categoría")
    public ResponseEntity<Page<ProductoDTO>> getProductosByCategoria(
            @PathVariable String categoria,
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        Page<ProductoDTO> productos = productoService.getProductosByCategoria(categoria, pageable);
        return ResponseEntity.ok(productos);
    }

    /**
     * Buscar productos por texto
     */
    @GetMapping("/search")
    @Operation(summary = "Buscar productos", description = "Buscar productos por nombre o descripción")
    public ResponseEntity<Page<ProductoDTO>> searchProductos(
            @RequestParam String q,
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        Page<ProductoDTO> productos = productoService.searchProductos(q, pageable);
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtener productos destacados
     */
    @GetMapping("/destacados")
    @Operation(summary = "Productos destacados", description = "Obtener productos marcados como destacados")
    public ResponseEntity<List<ProductoDTO>> getProductosDestacados() {
        List<ProductoDTO> productos = productoService.getProductosDestacados();
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtener productos con descuento
     */
    @GetMapping("/ofertas")
    @Operation(summary = "Productos en oferta", description = "Obtener productos con descuento")
    public ResponseEntity<List<ProductoDTO>> getProductosConDescuento() {
        List<ProductoDTO> productos = productoService.getProductosConDescuento();
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtener todas las categorías
     */
    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías", description = "Obtener todas las categorías disponibles")
    public ResponseEntity<List<String>> getCategorias() {
        List<String> categorias = productoService.getCategorias();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Crear producto (ADMIN)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear producto", description = "Crear un nuevo producto (solo ADMIN)")
    public ResponseEntity<ProductoDTO> createProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.createProducto(productoDTO);
        return ResponseEntity.ok(nuevoProducto);
    }

    /**
     * Actualizar producto (ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar producto", description = "Actualizar un producto existente (solo ADMIN)")
    public ResponseEntity<ProductoDTO> updateProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDTO
    ) {
        ProductoDTO productoActualizado = productoService.updateProducto(id, productoDTO);
        return ResponseEntity.ok(productoActualizado);
    }

    /**
     * Eliminar producto (ADMIN) - soft delete
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar producto", description = "Desactivar un producto (solo ADMIN)")
    public ResponseEntity<String> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.ok("Producto eliminado exitosamente");
    }
}