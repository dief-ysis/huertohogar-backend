package com.huertohogar.config;

import org.springframework.context.annotation.Configuration;

/**
 * CONFIGURACIÓN WEBPAY PLUS - SDK 2.0.0
 * 
 * NOTA: En SDK 2.0.0 no se requiere configuración global.
 * El SDK usa automáticamente credenciales de integración por defecto.
 * 
 * Para PRODUCCIÓN, se deben pasar las credenciales directamente
 * al crear cada transacción usando WebpayOptions.
 */
@Configuration
public class WebpayConfig {
    // No requiere configuración para ambiente TEST
    // SDK 2.0.0 usa credenciales de integración por defecto
}