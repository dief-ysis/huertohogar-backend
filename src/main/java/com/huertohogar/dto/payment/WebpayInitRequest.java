package com.huertohogar.dto.payment;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class WebpayInitRequest {
    private String buyOrder;
    private String sessionId;
    private BigDecimal amount;
    private String returnUrl;
}