package com.huertohogar.service;

import com.huertohogar.dto.cart.*;
import com.huertohogar.entity.*;
import com.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/* Principio: State Management. El carrito es persistente en base de datos, no en sesión del navegador, 
permitiendo que el usuario cambie de dispositivo y mantenga su carrito. */

@Service
@RequiredArgsConstructor
public class CarritoService {
    
    private final CarritoRepository carritoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public CarritoResponse agregarItem(String userEmail, AgregarItemRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

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
        
        carrito.agregarItem(item); // Lógica de fusión de cantidades en la Entidad
        
        return convertToResponse(carritoRepository.save(carrito));
    }

    @Transactional(readOnly = true)
    public CarritoResponse getCarrito(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Si no existe, devuelve uno vacío en memoria (o crea uno)
        return carritoRepository.findByUsuario(usuario)
                .map(this::convertToResponse)
                .orElse(CarritoResponse.builder().build()); // Retorno vacío seguro
    }

    @Transactional
    public CarritoResponse eliminarItem(String userEmail, Long itemId) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        // Buscar el item y verificar que pertenezca al carrito del usuario
        
        // Simplificación: Vaciar item específico (Implementación rápida)
        Carrito carrito = carritoRepository.findByUsuario(usuario).orElseThrow();
        carrito.getItems().removeIf(item -> item.getId().equals(itemId));
        carritoRepository.save(carrito);
        
        return convertToResponse(carrito);
    }
    
    @Transactional
    public void vaciarCarrito(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail).orElseThrow();
        carritoRepository.findByUsuario(usuario).ifPresent(carrito -> {
            carrito.getItems().clear(); // OrphanRemoval se encarga de borrar de BD
            carritoRepository.save(carrito);
        });
    }

    // Mapper manual simple
    private CarritoResponse convertToResponse(Carrito c) {
        // Implementación simplificada para el ejemplo
        return CarritoResponse.builder().id(c.getId()).build();
    }
}