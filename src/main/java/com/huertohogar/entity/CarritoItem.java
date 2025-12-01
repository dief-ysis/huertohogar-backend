package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ENTIDAD CARRITO ITEM
 * 
 * Item individual dentro del carrito de compras.
 */
@Entity
@Table(name = "carrito_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Precio unitario en el momento de agregar al carrito
     * (guardado para mantener histórico)
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    // =========================================
    // MÉTODOS DE CÁLCULO
    // =========================================

    /**
     * Calcula el subtotal del item (precio * cantidad)
     */
    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    /**
     * Actualiza el precio unitario desde el producto actual
     */
    public void actualizarPrecio() {
        if (producto != null) {
            this.precioUnitario = producto.getPrecioConDescuento();
        }
    }
}