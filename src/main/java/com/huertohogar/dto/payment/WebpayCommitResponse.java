package com.huertohogar.dto.payment;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class WebpayCommitResponse {
    private String buyOrder;
    private String sessionId;
    private BigDecimal amount;
    private String status;
    private String authorizationCode;
    private String paymentTypeCode;
    private String responseCode;
    private Integer installmentsNumber;
}