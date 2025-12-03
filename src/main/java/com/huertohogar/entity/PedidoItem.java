package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pedido_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @ToString.Exclude // CORRECCIÓN CRÍTICA: Rompe la recursividad con Pedido
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.EAGER) // EAGER está bien aquí, solemos necesitar el producto
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    /*
     * PRINCIPIO: Inmutabilidad Histórica
     * Guardamos el precio *del momento de la compra*. Si el Producto cambia de precio
     * mañana, este registro histórico NO debe cambiar.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        if (descuento != null && descuento.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal descuentoDecimal = descuento.divide(BigDecimal.valueOf(100));
            subtotal = subtotal.subtract(subtotal.multiply(descuentoDecimal));
        }
        return subtotal;
    }
}