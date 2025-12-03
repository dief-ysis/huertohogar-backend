package com.huertohogar.dto.order;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PedidoRequest {
    private String direccionEnvio;
    private String comunaEnvio;
    private String regionEnvio;
    private String notasEnvio;
    private BigDecimal costoEnvio;
}