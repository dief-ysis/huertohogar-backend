package com.huertohogar.controller;

import com.huertohogar.dto.order.PedidoDTO;
import com.huertohogar.dto.order.PedidoRequest;
import com.huertohogar.entity.Pedido;
import com.huertohogar.service.PedidoService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PEDIDO CONTROLLER
 * 
 * Endpoints de gestión de pedidos
 */
@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Pedidos", description = "Gestión de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * Crear pedido desde el carrito
     */
    @PostMapping
    @Operation(summary = "Crear pedido", description = "Crear un nuevo pedido desde el carrito actual")
    public ResponseEntity<PedidoDTO> crearPedido(
            @Valid @RequestBody PedidoRequest request,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        PedidoDTO pedido = pedidoService.crearPedido(userEmail, request);
        return ResponseEntity.ok(pedido);
    }

    /**
     * Obtener pedido por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener pedido", description = "Obtener detalles de un pedido por ID")
    public ResponseEntity<PedidoDTO> getPedidoById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        PedidoDTO pedido = pedidoService.getPedidoById(id, userEmail);
        return ResponseEntity.ok(pedido);
    }

    /**
     * Obtener pedido por número de pedido
     */
    @GetMapping("/numero/{numeroPedido}")
    @Operation(summary = "Obtener pedido por número", description = "Obtener pedido usando su número único")
    public ResponseEntity<PedidoDTO> getPedidoByNumeroPedido(
            @PathVariable String numeroPedido,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        PedidoDTO pedido = pedidoService.getPedidoByNumeroPedido(numeroPedido, userEmail);
        return ResponseEntity.ok(pedido);
    }

    /**
     * Obtener mis pedidos
     */
    @GetMapping("/user")
    @Operation(summary = "Mis pedidos", description = "Obtener todos los pedidos del usuario autenticado")
    public ResponseEntity<Page<PedidoDTO>> getMisPedidos(
            @PageableDefault(size = 10, sort = "fechaCreacion") Pageable pageable,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        Page<PedidoDTO> pedidos = pedidoService.getMisPedidos(userEmail, pageable);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Actualizar estado del pedido (ADMIN)
     */
    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar estado", description = "Cambiar el estado de un pedido (solo ADMIN)")
    public ResponseEntity<PedidoDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam Pedido.EstadoPedido estado
    ) {
        PedidoDTO pedido = pedidoService.actualizarEstado(id, estado);
        return ResponseEntity.ok(pedido);
    }

    /**
     * Obtener todos los pedidos (ADMIN)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Todos los pedidos", description = "Obtener todos los pedidos (solo ADMIN)")
    public ResponseEntity<Page<PedidoDTO>> getAllPedidos(
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable
    ) {
        Page<PedidoDTO> pedidos = pedidoService.getAllPedidos(pageable);
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Obtener pedidos por estado (ADMIN)
     */
    @GetMapping("/admin/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Pedidos por estado", description = "Filtrar pedidos por estado (solo ADMIN)")
    public ResponseEntity<List<PedidoDTO>> getPedidosByEstado(
            @PathVariable Pedido.EstadoPedido estado
    ) {
        List<PedidoDTO> pedidos = pedidoService.getPedidosByEstado(estado);
        return ResponseEntity.ok(pedidos);
    }
}