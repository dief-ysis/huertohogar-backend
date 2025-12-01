package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ENTIDAD PEDIDO ITEM
 * 
 * Item individual dentro de un pedido.
 */
@Entity
@Table(name = "pedido_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    // =========================================
    // MÉTODOS DE CÁLCULO
    // =========================================

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        
        if (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal descuentoDecimal = descuento.divide(BigDecimal.valueOf(100));
            subtotal = subtotal.subtract(subtotal.multiply(descuentoDecimal));
        }
        
        return subtotal;
    }
}