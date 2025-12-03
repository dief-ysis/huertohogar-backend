package com.huertohogar.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

/* Principio: Composition. El carrito "posee" sus items. */
@Entity
@Table(name = "carritos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @ToString.Exclude // Evita ciclo
    private Usuario usuario;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude // Evita ciclo
    private List<CarritoItem> items = new ArrayList<>();

    // Lógica helper para añadir items
    public void agregarItem(CarritoItem item) {
        item.setCarrito(this);
        // Verificar si ya existe para sumar cantidad
        this.items.stream()
            .filter(i -> i.getProducto().getId().equals(item.getProducto().getId()))
            .findFirst()
            .ifPresentOrElse(
                existente -> existente.setCantidad(existente.getCantidad() + item.getCantidad()),
                () -> this.items.add(item)
            );
    }
    
    public int getCantidadTotal() {
        return items.stream().mapToInt(CarritoItem::getCantidad).sum();
    }
}