package com.huertohogar.dto.cart;
import lombok.Data;

@Data
public class AgregarItemRequest {
    private Long productoId;
    private Integer cantidad;
}