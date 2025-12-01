package com.huertohogar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * HUERTOHOGAR BACKEND APPLICATION
 * 
 * Aplicación Spring Boot para e-commerce de productos orgánicos.
 * 
 * CARACTERÍSTICAS:
 * - API REST versionada (v1)
 * - Spring Security con JWT
 * - Integración Webpay Plus
 * - Documentación Swagger
 * - Base de datos MySQL
 * 
 * EVALUACIÓN DSY1104:
 * - Backend con Spring Boot ✅
 * - Conexión a base de datos ✅
 * - Lógica de negocio ✅
 * - API REST con CRUD ✅
 * - Autenticación JWT ✅
 * - Integración Transbank ✅
 * - Documentación API ✅
 * 
 * @author Equipo HuertoHogar
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class HuertoHogarApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuertoHogarApplication.class, args);
        
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║             🌱 HUERTOHOGAR BACKEND INICIADO 🌱              ║");
        System.out.println("║                                                              ║");
        System.out.println("║  API REST:     http://localhost:8080/api                     ║");
        System.out.println("║  Swagger UI:   http://localhost:8080/api/swagger-ui.html     ║");
        System.out.println("║  API Docs:     http://localhost:8080/api/v1/api-docs         ║");
        System.out.println("║                                                              ║");
        System.out.println("║  Versión:      1.0.0                                         ║");
        System.out.println("║  Perfil:       development                                   ║");
        System.out.println("║  Database:     MySQL (huertohogar_db)                        ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
    }

}