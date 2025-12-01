package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD PEDIDO
 * 
 * Pedido realizado por un usuario.
 */
@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String numeroPedido;  // Formato: ORDER-timestamp-random

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PedidoItem> items = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal costoEnvio;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal descuentos;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MetodoPago metodoPago = MetodoPago.WEBPAY;

    // DATOS DE ENVÍO
    @Column(nullable = false, length = 255)
    private String direccionEnvio;

    @Column(nullable = false, length = 100)
    private String comunaEnvio;

    @Column(nullable = false, length = 100)
    private String regionEnvio;

    @Column(length = 500)
    private String notasEnvio;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    private LocalDateTime fechaPago;

    private LocalDateTime fechaEnvio;

    private LocalDateTime fechaEntrega;

    // =========================================
    // ENUMS
    // =========================================

    public enum EstadoPedido {
        PENDIENTE,          // Pedido creado, esperando pago
        PAGADO,             // Pago confirmado
        PROCESANDO,         // Preparando el pedido
        ENVIADO,            // En camino
        ENTREGADO,          // Entregado al cliente
        CANCELADO,          // Cancelado
        RECHAZADO           // Pago rechazado
    }

    public enum MetodoPago {
        WEBPAY,
        TRANSFERENCIA,
        EFECTIVO
    }

    // =========================================
    // MÉTODOS DE NEGOCIO
    // =========================================

    public void agregarItem(PedidoItem item) {
        item.setPedido(this);
        items.add(item);
    }

    public void calcularTotales() {
        this.subtotal = items.stream()
            .map(PedidoItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.total = subtotal.add(costoEnvio).subtract(descuentos);
    }
}