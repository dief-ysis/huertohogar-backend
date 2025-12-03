package com.huertohogar.dto.order;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PedidoDTO {
    private Long id;
    private String numeroPedido;
    private List<PedidoItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal costoEnvio;
    private BigDecimal descuentos;
    private BigDecimal total;
    private String estado;
    private String metodoPago;
    private String direccionEnvio;
    private String comunaEnvio;
    private String regionEnvio;
    private String notasEnvio;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaPago;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaEntrega;
}