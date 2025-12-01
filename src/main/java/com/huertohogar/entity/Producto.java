package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ENTIDAD PRODUCTO
 * 
 * Productos orgánicos disponibles en la tienda.
 */
@Entity
@Table(name = "productos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false)
    private String categoria;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Column(length = 100)
    private String unidad;  // kg, unidad, bolsa, etc.

    @Column(length = 255)
    private String imagen;

    @Column(length = 100)
    private String origen;  // País o región de origen

    @Column(nullable = false)
    @Builder.Default
    private Boolean destacado = false;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    /**
     * Calcula el precio con descuento aplicado
     */
    public BigDecimal getPrecioConDescuento() {
        if (descuento == null || descuento.compareTo(BigDecimal.ZERO) == 0) {
            return precio;
        }
        BigDecimal descuentoDecimal = descuento.divide(BigDecimal.valueOf(100));
        return precio.subtract(precio.multiply(descuentoDecimal));
    }
}