package com.huertohogar.service;

import com.huertohogar.dto.cart.*;
import com.huertohogar.entity.*;
import com.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.exception.BadRequestException;
import com.huertohogar.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors; // Importante para el mapper
import java.math.BigDecimal;

/* Principio: State Management. El carrito es persistente en base de datos. */

@Service
@RequiredArgsConstructor
public class CarritoService {
    
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoItemRepository carritoItemRepository; // Necesario para eliminarItem individual

    @Transactional
    public CarritoResponse agregarItem(String userEmail, AgregarItemRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductoId()));

        if (!producto.getActivo()) {
            throw new BadRequestException("Producto no disponible");
        }
        
        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> {
                    Carrito c = new Carrito();
                    c.setUsuario(usuario);
                    return carritoRepository.save(c);
                });

        CarritoItem item = CarritoItem.builder()
                .producto(producto)
                .cantidad(request.getCantidad())
                .precioUnitario(producto.getPrecioConDescuento())
                .build();
        
        // CRÍTICO: Asegúrate de que Carrito.java tenga la lógica de fusión en 'agregarItem'
        carrito.agregarItem(item); 
        
        carritoRepository.save(carrito);
        
        // CRÍTICO: Retornar getCarrito fuerza una lectura fresca de la BD
        // esto evita enviar IDs nulos al frontend
        return getCarrito(userEmail);
    }

    @Transactional(readOnly = true)
    public CarritoResponse getCarrito(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Si no existe, devuelve uno vacío en memoria (o crea uno)
        return carritoRepository.findByUsuario(usuario)
                .map(this::convertToCarritoResponse)
                .orElse(CarritoResponse.builder()
                        .id(null)
                        .items(List.of())
                        .subtotal(BigDecimal.ZERO)
                        .total(BigDecimal.ZERO)
                        .cantidadTotal(0)
                        .descuentos(BigDecimal.ZERO)
                        .build());
    }

    @Transactional
    public CarritoResponse eliminarItem(String userEmail, Long itemId) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        Carrito carrito = carritoRepository.findByUsuario(usuario).orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));
        
        // Borrado seguro usando la lista de la entidad (orphanRemoval=true hará el delete en BD)
        carrito.getItems().removeIf(item -> item.getId().equals(itemId));
        carritoRepository.save(carrito);
        
        return convertToCarritoResponse(carrito);
    }
    
    @Transactional
    public void vaciarCarrito(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        carritoRepository.findByUsuario(usuario).ifPresent(carrito -> {
            carrito.getItems().clear(); // OrphanRemoval se encarga de borrar de BD
            carritoRepository.save(carrito);
        });
    }

    @Transactional
    public CarritoResponse sincronizarCarrito(String userEmail, List<AgregarItemRequest> itemsNuevos) {
        // 1. Vaciar carrito actual
        vaciarCarrito(userEmail);
        
        // 2. Agregar los items que venían del frontend
        if (itemsNuevos != null) {
            for (AgregarItemRequest item : itemsNuevos) {
                // Validación preventiva para evitar Rollback silencioso si un ID viejo ya no existe
                if (productoRepository.existsById(item.getProductoId())) {
                    try {
                        agregarItem(userEmail, item);
                    } catch (Exception e) {
                        System.err.println("No se pudo sincronizar item: " + item.getProductoId());
                    }
                }
            }
        }
        
        return getCarrito(userEmail);
    }

    @Transactional
    public CarritoResponse actualizarCantidad(String userEmail, Long itemId, Integer cantidad) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item no encontrado"));

        if (!item.getCarrito().getUsuario().getId().equals(usuario.getId())) {
            throw new BadRequestException("Item no pertenece al usuario");
        }

        if (cantidad <= 0) {
            return eliminarItem(userEmail, itemId);
        }

        if (item.getProducto().getStock() < cantidad) {
            throw new BadRequestException("Stock insuficiente");
        }

        item.setCantidad(cantidad);
        carritoItemRepository.save(item);

        return getCarrito(userEmail);
    }

    // --- MAPPERS ---

    private CarritoResponse convertToCarritoResponse(Carrito carrito) {
        List<CarritoItemDTO> itemDTOs = carrito.getItems().stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());

        BigDecimal subtotal = itemDTOs.stream()
                .map(CarritoItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular descuentos
        BigDecimal total = subtotal; // Simplificado, si tienes lógica de descuentos ponla aquí

        return CarritoResponse.builder()
                .id(carrito.getId())
                .items(itemDTOs)
                .cantidadTotal(carrito.getCantidadTotal())
                .subtotal(subtotal)
                .descuentos(BigDecimal.ZERO) // O cálculo real
                .total(total)
                .build();
    }

    private CarritoItemDTO convertToItemDTO(CarritoItem item) {
        return CarritoItemDTO.builder()
                .id(item.getId())
                .producto(com.huertohogar.dto.product.ProductoDTO.builder()
                        .id(item.getProducto().getId())
                        .nombre(item.getProducto().getNombre())
                        .precio(item.getProducto().getPrecio())
                        .imagen(item.getProducto().getImagen())
                        .stock(item.getProducto().getStock())
                        .categoria(item.getProducto().getCategoria())
                        .build())
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .build();
    }
}