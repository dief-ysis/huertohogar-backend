package com.huertohogar.controller;

import com.huertohogar.dto.cart.AgregarItemRequest;
import com.huertohogar.dto.cart.CarritoResponse;
import com.huertohogar.service.CarritoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public ResponseEntity<CarritoResponse> getCarrito(Authentication authentication) {
        return ResponseEntity.ok(carritoService.getCarrito(authentication.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoResponse> agregarItem(
            @RequestBody AgregarItemRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(carritoService.agregarItem(authentication.getName(), request));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CarritoResponse> actualizarCantidad(
            @PathVariable Long itemId,
            @RequestBody Map<String, Integer> payload, // Recibimos { "quantity": 5 }
            Authentication authentication
    ) {
        Integer cantidad = payload.get("quantity");
        return ResponseEntity.ok(carritoService.actualizarCantidad(authentication.getName(), itemId, cantidad));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CarritoResponse> eliminarItem(
            @PathVariable Long itemId, 
            Authentication authentication
    ) {
        return ResponseEntity.ok(carritoService.eliminarItem(authentication.getName(), itemId));
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(Authentication authentication) {
        carritoService.vaciarCarrito(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/sync")
    public ResponseEntity<CarritoResponse> sincronizarCarrito(
            @RequestBody Map<String, List<AgregarItemRequest>> payload,
            Authentication authentication
    ) {
        List<AgregarItemRequest> items = payload.get("items");
        return ResponseEntity.ok(carritoService.sincronizarCarrito(authentication.getName(), items));
    }
}