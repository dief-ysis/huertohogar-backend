package com.huertohogar.dto.cart;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CarritoResponse {
    private Long id;
    private List<CarritoItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal total;
}