package com.huertohogar.dto.cart;
import com.huertohogar.dto.product.ProductoDTO;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class CarritoItemDTO {
    private Long id;
    private ProductoDTO producto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}