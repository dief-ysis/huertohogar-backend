package com.huertohogar.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebpayCommitResponse {
    private String buyOrder;
    private String sessionId;
    private BigDecimal amount;
    private String status;
    private String authorizationCode;
    private String paymentTypeCode;
    private String responseCode;
    private Integer installmentsNumber;
    private LocalDateTime transactionDate;
}