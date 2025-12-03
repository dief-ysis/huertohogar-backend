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

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión de productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar productos")
    public ResponseEntity<Page<ProductoDTO>> getAllProductos(
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        Page<ProductoDTO> productos = productoService.getAllProductos(pageable);
        return ResponseEntity.ok(productos);
    }

    // CORRECCIÓN CRÍTICA: Restringimos el ID para que solo acepte números.
    // Esto evita que la palabra "categorias" o "search" se confunda con un ID.
    @GetMapping("/{id:[0-9]+}")
    @Operation(summary = "Obtener producto por ID")
    public ResponseEntity<ProductoDTO> getProductoById(@PathVariable Long id) {
        ProductoDTO producto = productoService.getProductoById(id);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/category/{categoria}")
    @Operation(summary = "Productos por categoría")
    public ResponseEntity<Page<ProductoDTO>> getProductosByCategoria(
            @PathVariable String categoria,
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        Page<ProductoDTO> productos = productoService.getProductosByCategoria(categoria, pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar productos")
    public ResponseEntity<Page<ProductoDTO>> searchProductos(
            @RequestParam String q,
            @PageableDefault(size = 12, sort = "id") Pageable pageable
    ) {
        Page<ProductoDTO> productos = productoService.searchProductos(q, pageable);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/destacados")
    @Operation(summary = "Productos destacados")
    public ResponseEntity<List<ProductoDTO>> getProductosDestacados() {
        List<ProductoDTO> productos = productoService.getProductosDestacados();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/ofertas")
    @Operation(summary = "Productos en oferta")
    public ResponseEntity<List<ProductoDTO>> getProductosConDescuento() {
        List<ProductoDTO> productos = productoService.getProductosConDescuento();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/categorias")
    @Operation(summary = "Listar categorías")
    public ResponseEntity<List<String>> getCategorias() {
        List<String> categorias = productoService.getCategorias();
        return ResponseEntity.ok(categorias);
    }

    // --- Endpoints ADMIN ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Crear producto")
    public ResponseEntity<ProductoDTO> createProducto(@Valid @RequestBody ProductoDTO productoDTO) {
        ProductoDTO nuevoProducto = productoService.createProducto(productoDTO);
        return ResponseEntity.ok(nuevoProducto);
    }

    @PutMapping("/{id:[0-9]+}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Actualizar producto")
    public ResponseEntity<ProductoDTO> updateProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoDTO productoDTO
    ) {
        ProductoDTO productoActualizado = productoService.updateProducto(id, productoDTO);
        return ResponseEntity.ok(productoActualizado);
    }

    @DeleteMapping("/{id:[0-9]+}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Eliminar producto")
    public ResponseEntity<String> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.ok("Producto eliminado exitosamente");
    }
}