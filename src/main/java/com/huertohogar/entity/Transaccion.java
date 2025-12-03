package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/* Principio: Representa una transacción de pago, con estado y datos de autorización. */

@Entity
@Table(name = "transacciones")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String buyOrder;
    
    @Column(unique = true)
    private String token; // Token Webpay
    
    private String sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    @ToString.Exclude
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @ToString.Exclude
    private Usuario usuario;

    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    private EstadoTransaccion estado;

    // Datos retorno Webpay
    private String authorizationCode;
    private String responseCode;
    private String paymentTypeCode;
    private Integer installmentsNumber;

    private String mensajeError;

    @CreatedDate
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaAutorizacion;

    public enum EstadoTransaccion {
        INICIADA, AUTORIZADA, RECHAZADA, ANULADA
    }
    
    public boolean esExitosa() {
        return estado == EstadoTransaccion.AUTORIZADA && "AUTHORIZED".equals(responseCode);
    }

    public void marcarComoRechazada(String responseCode, String mensaje) {
        this.estado = EstadoTransaccion.RECHAZADA;
        this.responseCode = responseCode;
        this.mensajeError = mensaje;
    }
}