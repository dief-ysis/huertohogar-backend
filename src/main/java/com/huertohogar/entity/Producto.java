package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/* Principio: Soft Delete (Borrado Lógico). Nunca borramos datos físicos (historial de ventas), 
solo marcamos activo = false. */

@Entity
@Table(name = "productos")
@Getter
@Setter
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

    // BigDecimal es obligatorio para dinero para evitar errores de redondeo de float/double
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

    private String unidad; // kg, unidad, malla
    private String imagen; // URL de la imagen
    private String origen; 

    @Builder.Default
    private Boolean destacado = false;

    @Column(precision = 3, scale = 2)
    @Builder.Default
    private java.math.BigDecimal rating = java.math.BigDecimal.ZERO;

    @Builder.Default
    private Boolean activo = true; // Para Soft Delete

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    // Lógica de dominio: Calcular precio real
    public BigDecimal getPrecioConDescuento() {
        if (descuento == null || descuento.compareTo(BigDecimal.ZERO) == 0) {
            return precio;
        }
        BigDecimal descuentoDecimal = descuento.divide(BigDecimal.valueOf(100));
        return precio.subtract(precio.multiply(descuentoDecimal));
    }
}