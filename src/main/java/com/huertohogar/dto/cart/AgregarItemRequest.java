package com.huertohogar.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgregarItemRequest {
    @NotNull(message = "ID del producto requerido")
    private Long productoId;
    
    @Min(value = 1, message = "Cantidad debe ser al menos 1")
    private Integer cantidad = 1;
}