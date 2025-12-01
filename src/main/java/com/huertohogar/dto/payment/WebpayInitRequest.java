package com.huertohogar.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WebpayInitRequest {
    @NotBlank(message = "buyOrder requerido")
    private String buyOrder;
    
    @NotBlank(message = "sessionId requerido")
    private String sessionId;
    
    @NotNull(message = "amount requerido")
    private BigDecimal amount;
    
    @NotBlank(message = "returnUrl requerido")
    private String returnUrl;
}