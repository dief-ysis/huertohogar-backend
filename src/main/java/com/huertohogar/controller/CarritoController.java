package com.huertohogar.controller;

import com.huertohogar.dto.cart.AgregarItemRequest;
import com.huertohogar.dto.cart.CarritoResponse;
import com.huertohogar.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CARRITO CONTROLLER
 * 
 * Endpoints de gestión del carrito de compras
 */
@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Carrito", description = "Gestión del carrito de compras")
public class CarritoController {

    private final CarritoService carritoService;

    /**
     * Obtener carrito del usuario actual
     */
    @GetMapping
    @Operation(summary = "Obtener carrito", description = "Obtener el carrito del usuario autenticado")
    public ResponseEntity<CarritoResponse> getCarrito(Authentication authentication) {
        String userEmail = authentication.getName();
        CarritoResponse carrito = carritoService.getCarrito(userEmail);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Agregar producto al carrito
     */
    @PostMapping("/items")
    @Operation(summary = "Agregar producto", description = "Agregar un producto al carrito")
    public ResponseEntity<CarritoResponse> agregarItem(
            @Valid @RequestBody AgregarItemRequest request,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        CarritoResponse carrito = carritoService.agregarItem(userEmail, request);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Actualizar cantidad de un item
     */
    @PutMapping("/items/{itemId}")
    @Operation(summary = "Actualizar cantidad", description = "Modificar la cantidad de un producto en el carrito")
    public ResponseEntity<CarritoResponse> actualizarCantidad(
            @PathVariable Long itemId,
            @RequestParam Integer cantidad,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        CarritoResponse carrito = carritoService.actualizarCantidad(userEmail, itemId, cantidad);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Eliminar item del carrito
     */
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Eliminar producto", description = "Eliminar un producto del carrito")
    public ResponseEntity<CarritoResponse> eliminarItem(
            @PathVariable Long itemId,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        CarritoResponse carrito = carritoService.eliminarItem(userEmail, itemId);
        return ResponseEntity.ok(carrito);
    }

    /**
     * Vaciar carrito
     */
    @DeleteMapping
    @Operation(summary = "Vaciar carrito", description = "Eliminar todos los productos del carrito")
    public ResponseEntity<String> vaciarCarrito(Authentication authentication) {
        String userEmail = authentication.getName();
        carritoService.vaciarCarrito(userEmail);
        return ResponseEntity.ok("Carrito vaciado exitosamente");
    }

    /**
     * Sincronizar carrito (útil después de login)
     */
    @PostMapping("/sync")
    @Operation(summary = "Sincronizar carrito", description = "Sincronizar carrito local con el servidor")
    public ResponseEntity<CarritoResponse> sincronizarCarrito(
            @RequestBody List<AgregarItemRequest> items,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        CarritoResponse carrito = carritoService.sincronizarCarrito(userEmail, items);
        return ResponseEntity.ok(carrito);
    }
}