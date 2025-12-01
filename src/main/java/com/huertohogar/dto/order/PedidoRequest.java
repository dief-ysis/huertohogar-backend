package com.huertohogar.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PedidoRequest {
    @NotBlank(message = "Dirección requerida")
    private String direccionEnvio;
    
    @NotBlank(message = "Comuna requerida")
    private String comunaEnvio;
    
    @NotBlank(message = "Región requerida")
    private String regionEnvio;
    
    private String notasEnvio;
    
    @NotNull(message = "Costo de envío requerido")
    private BigDecimal costoEnvio;
}