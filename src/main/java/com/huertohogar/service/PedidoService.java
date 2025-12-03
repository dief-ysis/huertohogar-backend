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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoService carritoService;
    private final ProductoService productoService;

    /*
     * PRINCIPIO: Atomicidad (ACID)
     * @Transactional asegura que todo sucede o nada sucede. 
     * Si falla el guardado del pedido, no se vacía el carrito.
     */
    @Transactional
    public PedidoDTO crearPedido(String userEmail, PedidoRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Carrito carrito = carritoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        if (carrito.getItems().isEmpty()) {
            throw new BadRequestException("El carrito está vacío");
        }

        /*
         * ADVERTENCIA DE DISEÑO (Race Condition):
         * Aquí verificamos stock, pero NO lo descontamos hasta que se paga.
         * Existe un riesgo de que dos usuarios compren la última unidad al mismo tiempo.
         * Solución ideal: Reservar stock temporalmente o usar bloqueo pesimista en DB.
         * Solución actual (MVP): Verificar de nuevo al momento del pago.
         */
        for (CarritoItem item : carrito.getItems()) {
            if (!productoService.verificarStock(item.getProducto().getId(), item.getCantidad())) {
                throw new BadRequestException("Stock insuficiente para: " + item.getProducto().getNombre());
            }
        }

        Pedido pedido = Pedido.builder()
                .numeroPedido(generarNumeroPedido())
                .usuario(usuario)
                .direccionEnvio(request.getDireccionEnvio())
                .comunaEnvio(request.getComunaEnvio())
                .regionEnvio(request.getRegionEnvio())
                .notasEnvio(request.getNotasEnvio())
                .costoEnvio(request.getCostoEnvio())
                .descuentos(BigDecimal.ZERO)
                .estado(Pedido.EstadoPedido.PENDIENTE)
                .metodoPago(Pedido.MetodoPago.WEBPAY)
                .build();

        // Conversión de CarritoItem a PedidoItem
        for (CarritoItem carritoItem : carrito.getItems()) {
            PedidoItem pedidoItem = PedidoItem.builder()
                    .pedido(pedido) // Establecemos la relación bidireccional
                    .producto(carritoItem.getProducto())
                    .cantidad(carritoItem.getCantidad())
                    .precioUnitario(carritoItem.getPrecioUnitario())
                    .descuento(carritoItem.getProducto().getDescuento())
                    .build();
            
            pedido.agregarItem(pedidoItem);
        }

        pedido.calcularTotales();
        pedido = pedidoRepository.save(pedido);

        // Limpieza: Una vez convertido en pedido, el carrito ya no se necesita.
        carritoService.vaciarCarrito(userEmail);

        return convertToDTO(pedido);
    }

    /**
     * Obtener pedido por ID
     */
    @Transactional(readOnly = true)
    public PedidoDTO getPedidoById(Long id, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", "id", id));

        // Verificar que el pedido pertenezca al usuario (o sea admin)
        if (!pedido.getUsuario().getId().equals(usuario.getId()) && 
            !usuario.getRol().equals(Usuario.Rol.ROLE_ADMIN)) {
            throw new BadRequestException("No tienes permiso para ver este pedido");
        }

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

    /**
     * Obtener pedidos del usuario
     */
    @Transactional(readOnly = true)
    public Page<PedidoDTO> getMisPedidos(String userEmail, Pageable pageable) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return pedidoRepository.findByUsuarioOrderByFechaCreacionDesc(usuario, pageable)
                .map(this::convertToDTO);
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
                for (PedidoItem item : pedido.getItems()) {
                    productoService.reducirStock(item.getProducto().getId(), item.getCantidad());
                }
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
        return pedidoRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    /**
     * Obtener pedidos por estado (ADMIN)
     */
    @Transactional(readOnly = true)
    public List<PedidoDTO> getPedidosByEstado(Pedido.EstadoPedido estado) {
        return pedidoRepository.findByEstadoOrderByFechaCreacionDesc(estado)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Generar número de pedido único
     */
    private String generarNumeroPedido() {
        return "ORDER-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
                .metodoPago(pedido.getMetodoPago().name())
                .direccionEnvio(pedido.getDireccionEnvio())
                .comunaEnvio(pedido.getComunaEnvio())
                .regionEnvio(pedido.getRegionEnvio())
                .notasEnvio(pedido.getNotasEnvio())
                .fechaCreacion(pedido.getFechaCreacion())
                .fechaPago(pedido.getFechaPago())
                .fechaEnvio(pedido.getFechaEnvio())
                .fechaEntrega(pedido.getFechaEntrega())
                .build();
    }
}