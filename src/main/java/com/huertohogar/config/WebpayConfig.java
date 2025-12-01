package com.huertohogar.config;

import cl.transbank.webpay.webpayplus.WebpayPlus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * CONFIGURACIÓN WEBPAY PLUS
 * 
 * CUMPLE CON PREGUNTA P102-138:
 * - Configuración del SDK de Transbank
 */
@Configuration
public class WebpayConfig {

    @Value("${transbank.webpay.environment}")
    private String environment;

    @Value("${transbank.webpay.commerce-code}")
    private String commerceCode;

    @Value("${transbank.webpay.api-key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        if ("PRODUCTION".equalsIgnoreCase(environment)) {
            WebpayPlus.configureForProduction(commerceCode, apiKey);
        } else {
            WebpayPlus.configureForTesting();
        }
    }
}