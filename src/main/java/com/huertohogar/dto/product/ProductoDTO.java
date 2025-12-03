package com.huertohogar.dto.product;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private BigDecimal precioConDescuento;
    private Integer stock;
    private String categoria;
    private String imagen;
    private String unidad;
    private String origen;
    private Boolean destacado;
    private BigDecimal rating;
    // Descuento en porcentaje o monto, según lógica
    private BigDecimal descuento;
}