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

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    // --- MÉTODOS DE LECTURA (Públicos) ---

    @Transactional(readOnly = true)
    public Page<ProductoDTO> getAllProductos(Pageable pageable) {
        return productoRepository.findByActivoTrue(pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public ProductoDTO getProductoById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", id));
        
        if (!producto.getActivo()) {
            throw new ResourceNotFoundException("El producto no está disponible");
        }
        return convertToDTO(producto);
    }

    @Transactional(readOnly = true)
    public Page<ProductoDTO> getProductosByCategoria(String categoria, Pageable pageable) {
        return productoRepository.findByCategoriaAndActivoTrue(categoria, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<ProductoDTO> searchProductos(String query, Pageable pageable) {
        return productoRepository.buscarPorTexto(query, pageable)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> getProductosDestacados() {
        return productoRepository.findByDestacadoTrueAndActivoTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductoDTO> getProductosConDescuento() {
        // Buscamos productos con descuento mayor a 0
        return productoRepository.findByActivoTrueAndDescuentoGreaterThan(BigDecimal.ZERO).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getCategorias() {
        return productoRepository.findAllCategorias();
    }

    // --- MÉTODOS DE STOCK (Llamados por PedidoService / WebpayService) ---

    @Transactional(readOnly = true)
    public boolean verificarStock(Long productoId, Integer cantidadRequerida) {
        return productoRepository.findById(productoId)
                .map(p -> p.getStock() >= cantidadRequerida && p.getActivo())
                .orElse(false);
    }

    @Transactional
    public void reducirStock(Long productoId, Integer cantidad) {
        Producto p = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        
        if (p.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para: " + p.getNombre());
        }
        
        p.setStock(p.getStock() - cantidad);
        productoRepository.save(p);
    }

    // --- MÉTODOS DE ADMIN (Escritura) ---

    @Transactional
    public ProductoDTO createProducto(ProductoDTO dto) {
        Producto producto = Producto.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precio(dto.getPrecio())
                .descuento(dto.getDescuento() != null ? dto.getDescuento() : BigDecimal.ZERO)
                .stock(dto.getStock())
                .categoria(dto.getCategoria())
                .unidad(dto.getUnidad())
                .imagen(dto.getImagen())
                .origen(dto.getOrigen())
                .destacado(dto.getDestacado() != null ? dto.getDestacado() : false)
                .rating(dto.getRating() != null ? dto.getRating() : BigDecimal.ZERO)
                .activo(true)
                .build();
        return convertToDTO(productoRepository.save(producto));
    }

    @Transactional
    public ProductoDTO updateProducto(Long id, ProductoDTO dto) {
        Producto p = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setStock(dto.getStock());
        p.setCategoria(dto.getCategoria());
        p.setImagen(dto.getImagen());
        
        if (dto.getDescuento() != null) p.setDescuento(dto.getDescuento());
        if (dto.getDestacado() != null) p.setDestacado(dto.getDestacado());
        if (dto.getUnidad() != null) p.setUnidad(dto.getUnidad());
        if (dto.getOrigen() != null) p.setOrigen(dto.getOrigen());

        return convertToDTO(productoRepository.save(p));
    }

    @Transactional
    public void deleteProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        producto.setActivo(false); // Borrado lógico
        productoRepository.save(producto);
    }

    // --- MAPPER ---

    private ProductoDTO convertToDTO(Producto p) {
        return ProductoDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .descuento(p.getDescuento())
                .precioConDescuento(p.getPrecioConDescuento())
                .stock(p.getStock())
                .categoria(p.getCategoria())
                .unidad(p.getUnidad())
                .imagen(p.getImagen())
                .origen(p.getOrigen())
                .destacado(p.getDestacado())
                .rating(p.getRating())
                .build();
    }
}