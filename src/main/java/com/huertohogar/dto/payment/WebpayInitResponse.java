package com.huertohogar.dto.payment;
import lombok.Data;

@Data
public class WebpayInitResponse {
    private String token;
    private String url;
}