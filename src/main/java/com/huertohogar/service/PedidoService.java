package com.huertohogar.service;

import com.huertohogar.dto.order.PedidoDTO;
import com.huertohogar.dto.order.PedidoItemDTO;
import com.huertohogar.dto.order.PedidoRequest;
import com.huertohogar.entity.*;
import com.huertohogar.exception.BadRequestException;
import com.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.repository.CarritoRepository;
import com.huertohogar.repository.PedidoRepository;
import com.huertohogar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoService carritoService;
    private final ProductoService productoService;

    @Transactional
    public PedidoDTO crearPedido(String userEmail, PedidoRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        if (carrito.getItems().isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }

        // Verificar stock
        for (CarritoItem item : carrito.getItems()) {
            if (!productoService.verificarStock(item.getProducto().getId(), item.getCantidad())) {
                throw new BadRequestException("Stock insuficiente para: " + item.getProducto().getNombre());
            }
        }

        // 1. Crear Pedido Base
        Pedido pedido = new Pedido();
        pedido.setNumeroPedido("ORD-" + System.currentTimeMillis());
        pedido.setUsuario(usuario);
        pedido.setDireccionEnvio(request.getDireccionEnvio());
        pedido.setComunaEnvio(request.getComunaEnvio());
        pedido.setRegionEnvio(request.getRegionEnvio());
        pedido.setNotasEnvio(request.getNotasEnvio());
        pedido.setCostoEnvio(request.getCostoEnvio());
        pedido.setDescuentos(BigDecimal.ZERO);
        pedido.setEstado(Pedido.EstadoPedido.PENDIENTE);
        pedido.setMetodoPago(Pedido.MetodoPago.WEBPAY);
        pedido.setFechaCreacion(LocalDateTime.now());
        
        // 2. Convertir Items y Asociar
        List<PedidoItem> pedidoItems = new ArrayList<>();
        for (CarritoItem carritoItem : carrito.getItems()) {
            PedidoItem pi = new PedidoItem();
            pi.setPedido(pedido);
            pi.setProducto(carritoItem.getProducto());
            pi.setCantidad(carritoItem.getCantidad());
            pi.setPrecioUnitario(carritoItem.getPrecioUnitario());
            pi.setDescuento(carritoItem.getProducto().getDescuento());
            pedidoItems.add(pi);
        }
        pedido.setItems(pedidoItems);
        pedido.calcularTotales();

        // 3. Guardar Pedido
        pedido = pedidoRepository.save(pedido);


        // Convertir a DTO seguro (sin bucles)
        return convertToDTO(pedido);
    }

    /**
     * Obtener pedido por número de pedido
     */
    @Transactional(readOnly = true)
    public PedidoDTO getPedidoByNumeroPedido(String numeroPedido, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (!pedido.getUsuario().getId().equals(usuario.getId()) && 
            !usuario.getRol().equals(Usuario.Rol.ROLE_ADMIN)) {
            throw new BadRequestException("No tienes permiso para ver este pedido");
        }

        return convertToDTO(pedido);
    }

    @Transactional(readOnly = true)
    public PedidoDTO getPedidoById(Long id, String userEmail) {
        Pedido p = pedidoRepository.findById(id).orElseThrow();
        return convertToDTO(p);
    }

    /**
     * Obtener pedidos del usuario
     */
    @Transactional(readOnly = true)
    public Page<PedidoDTO> getMisPedidos(String userEmail, Pageable pageable) {
        Usuario u = usuarioRepository.findByEmail(userEmail).orElseThrow();
        return pedidoRepository.findByUsuarioOrderByFechaCreacionDesc(u, pageable).map(this::convertToDTO);
    }
    /**
     * Actualizar estado del pedido (ADMIN o después de pago)
     */
    @Transactional
    public PedidoDTO actualizarEstado(Long pedidoId, Pedido.EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", pedidoId));

        pedido.setEstado(nuevoEstado);

        // Actualizar fechas según el estado
        LocalDateTime ahora = LocalDateTime.now();
        switch (nuevoEstado) {
            case PAGADO:
                pedido.setFechaPago(ahora);
                // Reducir stock al confirmar pago
                pedido.getItems().forEach(item -> 
                    productoService.reducirStock(item.getProducto().getId(), item.getCantidad())
                );
                break;
            case ENVIADO:
                pedido.setFechaEnvio(ahora);
                break;
            case ENTREGADO:
                pedido.setFechaEntrega(ahora);
                break;
            default:
                break;
        }

        pedido = pedidoRepository.save(pedido);
        return convertToDTO(pedido);
    }

    /**
     * Obtener todos los pedidos (ADMIN)
     */
    @Transactional(readOnly = true)
    public Page<PedidoDTO> getAllPedidos(Pageable pageable) {
        return pedidoRepository.findAll(pageable).map(this::convertToDTO);
    }

    /**
     * Obtener pedidos por estado (ADMIN)
     */
    @Transactional(readOnly = true)
    public List<PedidoDTO> getPedidosByEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstadoOrderByFechaCreacionDesc(estado)
                .stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Mapeo manual (patrón Mapper). Podrías usar MapStruct para automatizar esto.
    private PedidoDTO convertToDTO(Pedido pedido) {
        List<PedidoItemDTO> itemDTOs = pedido.getItems().stream()
                .map(item -> PedidoItemDTO.builder()
                        .id(item.getId())
                        .nombreProducto(item.getProducto().getNombre())
                        .cantidad(item.getCantidad())
                        .precioUnitario(item.getPrecioUnitario())
                        .descuento(item.getDescuento())
                        .subtotal(item.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return PedidoDTO.builder()
                .id(pedido.getId())
                .numeroPedido(pedido.getNumeroPedido())
                .items(itemDTOs)
                .subtotal(pedido.getSubtotal())
                .costoEnvio(pedido.getCostoEnvio())
                .descuentos(pedido.getDescuentos())
                .total(pedido.getTotal())
                .estado(pedido.getEstado().name())
                .build();
    }
}