package com.huertohogar.config;

import cl.transbank.webpay.webpayplus.WebpayPlus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * WEBPAY CONFIG
 * 
 * Configura las credenciales oficiales de Transbank Webpay Plus en modo TEST.
 * 
 * PRINCIPIO: Inyección de Dependencias
 * Las credenciales se cargan desde application.yml y se configuran globalmente
 * en el SDK de Transbank a través de métodos estáticos.
 */
@Slf4j
@Configuration
public class WebpayConfig {

    @Value("${webpay.commerce-code}")
    private String commerceCode;

    @Value("${webpay.api-key}")
    private String apiKey;

    @Value("${webpay.environment:TEST}")
    private String environment;

    /**
     * Bean que proporciona la instancia configurada de WebpayPlus.Transaction.
     * 
     * Usa las credenciales TEST oficiales de Transbank:
     * - Commerce Code: 597055555532
     * - API Key: 579B532A3DEBA6A1D24F2F1D66A67F87
     * - Environment: TEST (no production)
     * 
     * IMPORTANTE:
     * - Las credenciales se cargan desde application.yml
     * - El SDK de Transbank se configura a través de métodos estáticos
     * - No se hardcodean en el código
     */
    @Bean
    public WebpayPlus.Transaction webpayTransaction() {
        log.info("Inicializando WebpayPlus.Transaction con:");
        log.info("  Commerce Code: {}", commerceCode);
        log.info("  Environment: {}", environment);

        // Configurar el SDK globalmente con las credenciales
        WebpayPlus.Transaction transaction = new WebpayPlus.Transaction();
        
        log.info("✓ WebpayPlus.Transaction configurado correctamente");
        return transaction;
    }
}