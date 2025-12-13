package com.huertohogar.dto.payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebpayInitRequest {
    private String buyOrder;
    private String sessionId;
    private BigDecimal amount;
    private String returnUrl;
}