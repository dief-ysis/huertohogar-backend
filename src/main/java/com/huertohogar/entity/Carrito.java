package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTIDAD CARRITO
 * 
 * Carrito de compras del usuario.
 * Cada usuario tiene un único carrito activo.
 */
@Entity
@Table(name = "carritos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CarritoItem> items = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    // =========================================
    // MÉTODOS DE NEGOCIO
    // =========================================

    /**
     * Agrega un item al carrito o incrementa la cantidad si ya existe
     */
    public void agregarItem(CarritoItem item) {
        item.setCarrito(this);
        
        // Buscar si el producto ya existe en el carrito
        CarritoItem existingItem = items.stream()
            .filter(i -> i.getProducto().getId().equals(item.getProducto().getId()))
            .findFirst()
            .orElse(null);
        
        if (existingItem != null) {
            existingItem.setCantidad(existingItem.getCantidad() + item.getCantidad());
        } else {
            items.add(item);
        }
    }

    /**
     * Elimina un item del carrito
     */
    public void eliminarItem(CarritoItem item) {
        items.remove(item);
        item.setCarrito(null);
    }

    /**
     * Vacía el carrito eliminando todos los items
     */
    public void vaciar() {
        items.clear();
    }

    /**
     * Obtiene la cantidad total de items en el carrito
     */
    public int getCantidadTotal() {
        return items.stream()
            .mapToInt(CarritoItem::getCantidad)
            .sum();
    }
}