package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.*; // Usamos anotaciones granulares en vez de @Data
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/* * PRINCIPIO: Domain Driven Design (DDD) - Entidad Raíz (Aggregate Root)
 * El Pedido es la entidad principal que gestiona la coherencia de sus items.
 */
@Entity
@Table(name = "pedidos")
@Getter // PRINCIPIO: Encapsulamiento. Solo Getters/Setters explícitos.
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String numeroPedido;

    /* * PRINCIPIO: Relaciones JPA y Performance
     * FetchType.LAZY: Carga diferida. No traemos al usuario de la BD 
     * hasta que realmente se pida (ahorra memoria).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @ToString.Exclude // CORRECCIÓN: Evita bucle infinito al imprimir logs
    private Usuario usuario;

    /*
     * PRINCIPIO: Gestión del Ciclo de Vida (Cascade)
     * Si borro/guardo el pedido, se borran/guardan sus items automáticamente.
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude // CORRECCIÓN: Evita bucle infinito
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

    // Datos de Envío
    private String direccionEnvio;
    private String comunaEnvio;
    private String regionEnvio;
    private String notasEnvio;

    // Auditoría automática (Spring Data JPA)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    private LocalDateTime fechaPago;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntrega;

    public enum EstadoPedido {
        PENDIENTE, PAGADO, PROCESANDO, ENVIADO, ENTREGADO, CANCELADO, RECHAZADO
    }

    public enum MetodoPago {
        WEBPAY, TRANSFERENCIA, EFECTIVO
    }

    /*
     * PRINCIPIO: Expert Pattern (GRASP)
     * La entidad que tiene la información (items) es la experta en calcular sus totales.
     * No delegues esto al Servicio si la Entidad puede hacerlo.
     */
    public void calcularTotales() {
        this.subtotal = items.stream()
            .map(PedidoItem::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.total = subtotal.add(costoEnvio).subtract(descuentos);
    }
    
    // Método helper para mantener consistencia bidireccional
    public void agregarItem(PedidoItem item) {
        item.setPedido(this);
        items.add(item);
    }
}