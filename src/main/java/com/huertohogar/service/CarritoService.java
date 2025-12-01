package com.huertohogar.service;

import com.huertohogar.dto.cart.AgregarItemRequest;
import com.huertohogar.dto.cart.CarritoItemDTO;
import com.huertohogar.dto.cart.CarritoResponse;
import com.huertohogar.dto.product.ProductoDTO;
import com.huertohogar.entity.Carrito;
import com.huertohogar.entity.CarritoItem;
import com.huertohogar.entity.Producto;
import com.huertohogar.entity.Usuario;
import com.huertohogar.exception.BadRequestException;
import com.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.repository.CarritoItemRepository;
import com.huertohogar.repository.CarritoRepository;
import com.huertohogar.repository.ProductoRepository;
import com.huertohogar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CARRITO SERVICE
 * 
 * Gestión completa del carrito de compras
 */
@Service
@RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtener carrito del usuario actual
     */
    @Transactional(readOnly = true)
    public CarritoResponse getCarrito(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> crearCarritoVacio(usuario));

        return convertToCarritoResponse(carrito);
    }

    /**
     * Agregar producto al carrito
     */
    @Transactional
    public CarritoResponse agregarItem(String userEmail, AgregarItemRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", request.getProductoId()));

        if (!producto.getActivo()) {
            throw new BadRequestException("Producto no disponible");
        }

        if (producto.getStock() < request.getCantidad()) {
            throw new BadRequestException("Stock insuficiente. Disponible: " + producto.getStock());
        }

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseGet(() -> crearCarritoVacio(usuario));

        // Verificar si el producto ya existe en el carrito
        CarritoItem existingItem = carritoItemRepository
                .findByCarritoAndProducto(carrito, producto)
                .orElse(null);

        if (existingItem != null) {
            // Actualizar cantidad
            int nuevaCantidad = existingItem.getCantidad() + request.getCantidad();
            
            if (producto.getStock() < nuevaCantidad) {
                throw new BadRequestException("Stock insuficiente. Disponible: " + producto.getStock());
            }
            
            existingItem.setCantidad(nuevaCantidad);
            existingItem.actualizarPrecio();
            carritoItemRepository.save(existingItem);
        } else {
            // Crear nuevo item
            CarritoItem nuevoItem = CarritoItem.builder()
                    .carrito(carrito)
                    .producto(producto)
                    .cantidad(request.getCantidad())
                    .precioUnitario(producto.getPrecioConDescuento())
                    .build();
            
            carritoItemRepository.save(nuevoItem);
            carrito.getItems().add(nuevoItem);
        }

        carrito = carritoRepository.save(carrito);
        return convertToCarritoResponse(carrito);
    }

    /**
     * Actualizar cantidad de un item
     */
    @Transactional
    public CarritoResponse actualizarCantidad(String userEmail, Long itemId, Integer nuevaCantidad) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item del carrito no encontrado"));

        if (!item.getCarrito().getUsuario().getId().equals(usuario.getId())) {
            throw new BadRequestException("Item no pertenece al usuario");
        }

        if (nuevaCantidad <= 0) {
            throw new BadRequestException("La cantidad debe ser mayor a 0");
        }

        if (item.getProducto().getStock() < nuevaCantidad) {
            throw new BadRequestException("Stock insuficiente. Disponible: " + item.getProducto().getStock());
        }

        item.setCantidad(nuevaCantidad);
        item.actualizarPrecio();
        carritoItemRepository.save(item);

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        return convertToCarritoResponse(carrito);
    }

    /**
     * Eliminar item del carrito
     */
    @Transactional
    public CarritoResponse eliminarItem(String userEmail, Long itemId) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        CarritoItem item = carritoItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item del carrito no encontrado"));

        if (!item.getCarrito().getUsuario().getId().equals(usuario.getId())) {
            throw new BadRequestException("Item no pertenece al usuario");
        }

        carritoItemRepository.delete(item);

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        return convertToCarritoResponse(carrito);
    }

    /**
     * Vaciar carrito
     */
    @Transactional
    public void vaciarCarrito(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElse(null);

        if (carrito != null) {
            carritoItemRepository.deleteByCarrito(carrito);
            carrito.getItems().clear();
            carritoRepository.save(carrito);
        }
    }

    /**
     * Sincronizar carrito (útil después de login)
     */
    @Transactional
    public CarritoResponse sincronizarCarrito(String userEmail, List<AgregarItemRequest> items) {
        // Primero vaciar el carrito actual
        vaciarCarrito(userEmail);

        // Agregar todos los items del frontend
        CarritoResponse response = null;
        for (AgregarItemRequest item : items) {
            response = agregarItem(userEmail, item);
        }

        return response != null ? response : getCarrito(userEmail);
    }

    /**
     * Crear carrito vacío para un usuario
     */
    private Carrito crearCarritoVacio(Usuario usuario) {
        Carrito carrito = Carrito.builder()
                .usuario(usuario)
                .build();
        return carritoRepository.save(carrito);
    }

    /**
     * Convertir Carrito a CarritoResponse
     */
    private CarritoResponse convertToCarritoResponse(Carrito carrito) {
        List<CarritoItemDTO> itemDTOs = carrito.getItems().stream()
                .map(this::convertToItemDTO)
                .collect(Collectors.toList());

        BigDecimal subtotal = itemDTOs.stream()
                .map(CarritoItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuentos = carrito.getItems().stream()
                .filter(item -> item.getProducto().getDescuento().compareTo(BigDecimal.ZERO) > 0)
                .map(item -> {
                    BigDecimal precioOriginal = item.getProducto().getPrecio();
                    BigDecimal precioConDescuento = item.getProducto().getPrecioConDescuento();
                    BigDecimal ahorroUnitario = precioOriginal.subtract(precioConDescuento);
                    return ahorroUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CarritoResponse.builder()
                .id(carrito.getId())
                .items(itemDTOs)
                .cantidadTotal(carrito.getCantidadTotal())
                .subtotal(subtotal)
                .descuentos(descuentos)
                .total(subtotal)
                .build();
    }

    /**
     * Convertir CarritoItem a CarritoItemDTO
     */
    private CarritoItemDTO convertToItemDTO(CarritoItem item) {
        ProductoDTO productoDTO = ProductoDTO.builder()
                .id(item.getProducto().getId())
                .nombre(item.getProducto().getNombre())
                .descripcion(item.getProducto().getDescripcion())
                .precio(item.getProducto().getPrecio())
                .descuento(item.getProducto().getDescuento())
                .precioConDescuento(item.getProducto().getPrecioConDescuento())
                .categoria(item.getProducto().getCategoria())
                .stock(item.getProducto().getStock())
                .unidad(item.getProducto().getUnidad())
                .imagen(item.getProducto().getImagen())
                .build();

        return CarritoItemDTO.builder()
                .id(item.getId())
                .producto(productoDTO)
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .build();
    }
}