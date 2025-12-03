package com.huertohogar.controller;

import com.huertohogar.dto.order.PedidoDTO;
import com.huertohogar.dto.order.PedidoRequest;
import com.huertohogar.entity.Pedido;
import com.huertohogar.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/* Principio: Resource Hierarchy. /user devuelve los recursos del usuario logueado. */

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    // Crear pedido desde el carrito actual
    @PostMapping
    public ResponseEntity<PedidoDTO> crearPedido(
            @Valid @RequestBody PedidoRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(pedidoService.crearPedido(authentication.getName(), request));
    }

    // Obtener mis pedidos (Usuario normal)
    @GetMapping("/user")
    public ResponseEntity<Page<PedidoDTO>> getMisPedidos(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "fechaCreacion") Pageable pageable
    ) {
        return ResponseEntity.ok(pedidoService.getMisPedidos(authentication.getName(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> getPedidoById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return ResponseEntity.ok(pedidoService.getPedidoById(id, authentication.getName()));
    }
    
    // Obtener todos los pedidos (Solo ADMIN)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PedidoDTO>> getAllPedidos(
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable
    ) {
        return ResponseEntity.ok(pedidoService.getAllPedidos(pageable));
    }

    // Actualizar estado (Solo ADMIN)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Pedido.EstadoPedido estado
    ) {
        return ResponseEntity.ok(pedidoService.actualizarEstado(id, estado));
    }
}