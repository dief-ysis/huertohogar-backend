package com.huertohogar.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarritoResponse {
    private Long id;
    private List<CarritoItemDTO> items;
    private Integer cantidadTotal;
    private BigDecimal subtotal;
    private BigDecimal descuentos;
    private BigDecimal total;
}