package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/* Principio: CarritoItem representa un ítem dentro del carrito, con cantidad y precio unitario. */

@Entity
@Table(name = "carrito_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    @ToString.Exclude
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    private Integer cantidad;
    private BigDecimal precioUnitario; // Snapshot del precio al momento de agregar

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    // Actualiza precio si el producto cambió (opcional, según regla de negocio)
    public void actualizarPrecio() {
        if (producto != null) {
            this.precioUnitario = producto.getPrecioConDescuento();
        }
    }
}