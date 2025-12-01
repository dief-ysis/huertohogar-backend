package com.huertohogar.service;

import com.huertohogar.dto.product.ProductoDTO;
import com.huertohogar.entity.Producto;
import com.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * PRODUCTO SERVICE
 * 
 * CUMPLE CON PREGUNTAS:
 * - P45-60: Gestión de productos vía API REST
 * - P21-29: Lógica de negocio
 */
@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    /**
     * Obtener todos los productos con paginación
     */
    @Transactional(readOnly = true)
    public Page<ProductoDTO> getAllProductos(Pageable pageable) {
        return productoRepository.findByActivoTrue(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Obtener producto por ID
     */
    @Transactional(readOnly = true)
    public ProductoDTO getProductoById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        if (!producto.getActivo()) {
            throw new ResourceNotFoundException("Producto no disponible");
        }
        
        return convertToDTO(producto);
    }

    /**
     * Buscar productos por categoría
     */
    @Transactional(readOnly = true)
    public Page<ProductoDTO> getProductosByCategoria(String categoria, Pageable pageable) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Buscar productos por texto (nombre o descripción)
     */
    @Transactional(readOnly = true)
    public Page<ProductoDTO> searchProductos(String query, Pageable pageable) {
        return productoRepository.buscarPorTexto(query, pageable)
                .map(this::convertToDTO);
    }

    /**
     * Obtener productos destacados
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> getProductosDestacados() {
        return productoRepository.findByDestacadoTrueAndActivoTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener productos con descuento
     */
    @Transactional(readOnly = true)
    public List<ProductoDTO> getProductosConDescuento() {
        return productoRepository.findByActivoTrueAndDescuentoGreaterThan(BigDecimal.ZERO)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener todas las categorías
     */
    @Transactional(readOnly = true)
    public List<String> getCategorias() {
        return productoRepository.findAllCategorias();
    }

    /**
     * Crear producto (ADMIN)
     */
    @Transactional
    public ProductoDTO createProducto(ProductoDTO productoDTO) {
        Producto producto = Producto.builder()
                .nombre(productoDTO.getNombre())
                .descripcion(productoDTO.getDescripcion())
                .precio(productoDTO.getPrecio())
                .descuento(productoDTO.getDescuento() != null ? productoDTO.getDescuento() : BigDecimal.ZERO)
                .categoria(productoDTO.getCategoria())
                .stock(productoDTO.getStock())
                .unidad(productoDTO.getUnidad())
                .imagen(productoDTO.getImagen())
                .origen(productoDTO.getOrigen())
                .destacado(productoDTO.getDestacado() != null ? productoDTO.getDestacado() : false)
                .rating(productoDTO.getRating() != null ? productoDTO.getRating() : BigDecimal.ZERO)
                .activo(true)
                .build();

        producto = productoRepository.save(producto);
        return convertToDTO(producto);
    }

    /**
     * Actualizar producto (ADMIN)
     */
    @Transactional
    public ProductoDTO updateProducto(Long id, ProductoDTO productoDTO) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));

        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setDescuento(productoDTO.getDescuento() != null ? productoDTO.getDescuento() : BigDecimal.ZERO);
        producto.setCategoria(productoDTO.getCategoria());
        producto.setStock(productoDTO.getStock());
        producto.setUnidad(productoDTO.getUnidad());
        producto.setImagen(productoDTO.getImagen());
        producto.setOrigen(productoDTO.getOrigen());
        producto.setDestacado(productoDTO.getDestacado() != null ? productoDTO.getDestacado() : false);
        producto.setRating(productoDTO.getRating() != null ? productoDTO.getRating() : BigDecimal.ZERO);

        producto = productoRepository.save(producto);
        return convertToDTO(producto);
    }

    /**
     * Eliminar producto (ADMIN) - soft delete
     */
    @Transactional
    public void deleteProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    /**
     * Verificar disponibilidad de stock
     */
    @Transactional(readOnly = true)
    public boolean verificarStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));
        
        return producto.getStock() >= cantidad;
    }

    /**
     * Reducir stock (llamado al crear pedido)
     */
    @Transactional
    public void reducirStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", productoId));
        
        if (producto.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para producto: " + producto.getNombre());
        }
        
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
    }

    /**
     * Convertir Producto entity a ProductoDTO
     */
    private ProductoDTO convertToDTO(Producto producto) {
        return ProductoDTO.builder()
                .id(producto.getId())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .descuento(producto.getDescuento())
                .precioConDescuento(producto.getPrecioConDescuento())
                .categoria(producto.getCategoria())
                .stock(producto.getStock())
                .unidad(producto.getUnidad())
                .imagen(producto.getImagen())
                .origen(producto.getOrigen())
                .destacado(producto.getDestacado())
                .rating(producto.getRating())
                .build();
    }
}