package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ENTIDAD TRANSACCION
 * 
 * Transacción de pago con Webpay Plus.
 * 
 * CUMPLE CON PREGUNTAS P102-138:
 * - Integración Webpay Plus
 * - Gestión de transacciones
 * - Estados de pago
 */
@Entity
@Table(name = "transacciones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String buyOrder;  // Orden de compra única

    @Column(unique = true)
    private String token;  // Token de Webpay

    @Column(nullable = false)
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoTransaccion estado = EstadoTransaccion.INICIADA;

    // DATOS DE RESPUESTA DE WEBPAY
    private String authorizationCode;
    private String paymentTypeCode;
    private String responseCode;
    private Integer installmentsNumber;

    @Column(length = 1000)
    private String mensajeError;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaAutorizacion;

    // =========================================
    // ENUM ESTADO TRANSACCIÓN
    // =========================================

    public enum EstadoTransaccion {
        INICIADA,           // Transacción creada, esperando pago
        AUTORIZADA,         // Pago autorizado por Webpay
        RECHAZADA,          // Pago rechazado
        ANULADA,            // Transacción anulada
        REVERSADA,          // Transacción reversada
        EXPIRADA            // Token expirado
    }

    // =========================================
    // MÉTODOS DE NEGOCIO
    // =========================================

    public boolean esExitosa() {
        return estado == EstadoTransaccion.AUTORIZADA 
            && "AUTHORIZED".equals(responseCode);
    }

    public void marcarComoAutorizada(String authCode, String respCode) {
        this.estado = EstadoTransaccion.AUTORIZADA;
        this.authorizationCode = authCode;
        this.responseCode = respCode;
        this.fechaAutorizacion = LocalDateTime.now();
    }

    public void marcarComoRechazada(String respCode, String mensaje) {
        this.estado = EstadoTransaccion.RECHAZADA;
        this.responseCode = respCode;
        this.mensajeError = mensaje;
    }
}