package com.huertohogar.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal descuento;
    private BigDecimal precioConDescuento;
    private String categoria;
    private Integer stock;
    private String unidad;
    private String imagen;
    private String origen;
    private Boolean destacado;
    private BigDecimal rating;
}