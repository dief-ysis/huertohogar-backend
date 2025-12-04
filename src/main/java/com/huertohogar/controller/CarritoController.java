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

/* Principio: Context Awareness. Usamos Authentication para saber quién llama, 
en lugar de pedir el ID del usuario por parámetro (lo cual sería inseguro). */

@RestController
@RequestMapping("/v1/cart")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public ResponseEntity<CarritoResponse> getCarrito(Authentication authentication) {
        // Obtenemos el email del Token JWT automáticamente
        return ResponseEntity.ok(carritoService.getCarrito(authentication.getName()));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoResponse> agregarItem(
            @RequestBody AgregarItemRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(carritoService.agregarItem(authentication.getName(), request));
    }

    // Endpoint para sincronizar carrito local (si implementas esa lógica en React)
    @PostMapping("/sync")
    public ResponseEntity<CarritoResponse> sincronizarCarrito(
            @RequestBody Map<String, List<AgregarItemRequest>> payload,
            Authentication authentication
    ) {
        // El frontend envía { items: [...] }, extraemos la lista
        List<AgregarItemRequest> items = payload.get("items");
        return ResponseEntity.ok(carritoService.sincronizarCarrito(authentication.getName(), items));
    }

    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(Authentication authentication) {
        carritoService.vaciarCarrito(authentication.getName());
        return ResponseEntity.noContent().build();
    }
    
    // Faltaba este en el servicio anterior, asumo su existencia para REST completo
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CarritoResponse> eliminarItem(
            @PathVariable Long itemId, 
            Authentication authentication
    ) {
        return ResponseEntity.ok(carritoService.eliminarItem(authentication.getName(), itemId));
    }
}