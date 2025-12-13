package com.huertohogar.service;

import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;

import com.huertohogar.dto.payment.WebpayCommitResponse;
import com.huertohogar.dto.payment.WebpayInitRequest;
import com.huertohogar.dto.payment.WebpayInitResponse;
import com.huertohogar.entity.Pedido;
import com.huertohogar.entity.PedidoItem;
import com.huertohogar.entity.Transaccion;
import com.huertohogar.entity.Usuario;
import com.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.repository.PedidoRepository;
import com.huertohogar.repository.TransaccionRepository;
import com.huertohogar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebpayService {

    private final TransaccionRepository transaccionRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoService productoService;

    @Transactional
    public WebpayInitResponse iniciarTransaccion(String userEmail, WebpayInitRequest request) {
        log.info("Iniciando transacción Webpay para usuario: {}", userEmail);

        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Validar que el pedido existe
        Pedido pedido = pedidoRepository.findByNumeroPedido(request.getBuyOrder())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        // Validar que el pedido pertenece al usuario
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("El pedido no pertenece al usuario");
        }

        // VALIDAR parámetros de Transbank ANTES de llamar
        validarParametrosWebpay(request);

        try {
            // IMPORTANTE: Webpay no acepta decimales para CLP, redondear a entero
            long amountAsLong = request.getAmount().setScale(0, java.math.RoundingMode.HALF_UP).longValue();

            log.info("📡 Llamando a WebpayPlus.Transaction.create()...");
            log.info("   - buyOrder: {}", request.getBuyOrder());
            log.info("   - amount original: {}", request.getAmount());
            log.info("   - amount redondeado (CLP no acepta decimales): {}", amountAsLong);
            log.info("   - returnUrl: {}", request.getReturnUrl());

            WebpayPlus.Transaction transaction = new WebpayPlus.Transaction();
            WebpayPlusTransactionCreateResponse response = transaction.create(
                    request.getBuyOrder(),
                    request.getSessionId(),
                    amountAsLong,
                    request.getReturnUrl()
            );

            // CRÍTICO: Validar que response.getToken() no sea NULL
            if (response.getToken() == null || response.getToken().isEmpty()) {
                String errorMsg = "Error al generar token Webpay. Por favor verifica las credenciales de Transbank.";
                log.error("❌ {}", errorMsg);
                throw new RuntimeException(errorMsg);
            }

            log.info("✓ Token recibido: {}", response.getToken().substring(0, 10) + "...");

            // 2. Guardar transacción en estado INICIADA
            Transaccion transaccion = Transaccion.builder()
                    .pedido(pedido)
                    .usuario(usuario)
                    .token(response.getToken())
                    .monto(request.getAmount())
                    .estado(Transaccion.EstadoTransaccion.INICIADA)
                    .buyOrder(request.getBuyOrder())
                    .sessionId(request.getSessionId())
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            transaccionRepository.save(transaccion);
            log.info("Transacción guardada en BD con token: {}", response.getToken());

            return WebpayInitResponse.builder()
                    .token(response.getToken())
                    .url(response.getUrl())
                    .build();
        } catch (Exception e) {
            log.error("Error Webpay Init", e);
            throw new RuntimeException("Error al iniciar transacción: " + e.getMessage());
        }
    }

    /**
     * Valida que los parámetros cumplan con los requisitos de Transbank Webpay
     * 
     * Reglas:
     * - buyOrder: máximo 26 caracteres
     * - sessionId: máximo 61 caracteres
     * - amount: > 0
     * - returnUrl: URL válida (no localhost en producción)
     */
    private void validarParametrosWebpay(WebpayInitRequest request) {
        if (request.getBuyOrder() == null || request.getBuyOrder().length() > 26) {
            throw new IllegalArgumentException("buyOrder inválido (max 26 caracteres)");
        }
        if (request.getSessionId() == null || request.getSessionId().length() > 61) {
            throw new IllegalArgumentException("sessionId inválido (max 61 caracteres)");
        }
        if (request.getAmount() == null || request.getAmount().doubleValue() <= 0) {
            throw new IllegalArgumentException("amount debe ser > 0");
        }
        if (request.getReturnUrl() == null || !request.getReturnUrl().startsWith("http")) {
            throw new IllegalArgumentException("returnUrl debe ser una URL válida (http/https)");
        }
        log.info("✓ Parámetros validados correctamente");
    }

    @Transactional
    public WebpayCommitResponse confirmarTransaccion(String token) {
        log.info("Confirmando transacción Webpay con token: {}", token != null ? token.substring(0, 10) + "..." : "NULL");

        // CRÍTICO: Validar que el token no sea nulo
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token inválido o vacío");
        }

        Transaccion transaccion = transaccionRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));

        try {
            log.info("📡 Llamando a WebpayPlus.Transaction.commit()...");
            
            // Usar la instancia inyectada (configurada correctamente)
            WebpayPlus.Transaction transaction = new WebpayPlus.Transaction();
            WebpayPlusTransactionCommitResponse response = transaction.commit(token);

            log.info("✓ Respuesta recibida. Status: {}, ResponseCode: {}", response.getStatus(), response.getResponseCode());

            transaccion.setAuthorizationCode(response.getAuthorizationCode());
            transaccion.setResponseCode(String.valueOf(response.getResponseCode()));
            transaccion.setPaymentTypeCode(response.getPaymentTypeCode());
            transaccion.setInstallmentsNumber((int) response.getInstallmentsNumber());
            transaccion.setFechaAutorizacion(LocalDateTime.now());

            // Procesar resultado según estado de Transbank
            if ("AUTHORIZED".equals(response.getStatus())) {
                log.info("✓ PAGO AUTORIZADO");
                transaccion.setEstado(Transaccion.EstadoTransaccion.AUTORIZADA);
                
                Pedido pedido = transaccion.getPedido();
                pedido.setEstado(Pedido.EstadoPedido.PAGADO);
                pedido.setFechaPago(LocalDateTime.now());
                pedidoRepository.save(pedido);

                // Reducir stock después de pago confirmado
                for (PedidoItem item : pedido.getItems()) {
                    try {
                        productoService.reducirStock(item.getProducto().getId(), item.getCantidad());
                    } catch (Exception e) {
                        log.error("No se pudo vaciar el carrito tras pago exitoso", e);
                    }
                }
                log.info("✓ Stock reducido para todos los items");
            } else {
                log.warn("⚠ PAGO RECHAZADO - Status: {}, ResponseCode: {}", response.getStatus(), response.getResponseCode());
                transaccion.setEstado(Transaccion.EstadoTransaccion.RECHAZADA);
                transaccion.setMensajeError("Rechazada: " + response.getStatus());
                if (transaccion.getPedido() != null) {
                    transaccion.getPedido().setEstado(Pedido.EstadoPedido.RECHAZADO);
                }
            }

            transaccionRepository.save(transaccion);
            return mapToCommitResponse(transaccion);

        } catch (Exception e) {
            log.error("❌ Error al confirmar transacción: {}", e.getMessage(), e);
            transaccion.setEstado(Transaccion.EstadoTransaccion.RECHAZADA);
            transaccion.setMensajeError(e.getMessage());
            transaccionRepository.save(transaccion);
            throw new RuntimeException("Error al confirmar transacción: " + e.getMessage(), e);
        }
    }

    public Transaccion obtenerEstadoTransaccion(String token) {
        return transaccionRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));
    }

    public void manejarTransaccionFallida(String token, String motivo) {
        transaccionRepository.findByToken(token).ifPresent(t -> {
            t.marcarComoRechazada("FAILED", motivo);
            transaccionRepository.save(t);
        });
    }

    public boolean esTransaccionExitosa(String token) {
        return obtenerEstadoTransaccion(token).esExitosa();
    }

    public Transaccion obtenerPorBuyOrder(String buyOrder) {
        return transaccionRepository.findByBuyOrder(buyOrder)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));
    }

    public List<Transaccion> obtenerTransaccionesUsuario(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return transaccionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuario.getId());
    }

    private WebpayCommitResponse mapToCommitResponse(Transaccion t) {
        return WebpayCommitResponse.builder()
                .buyOrder(t.getBuyOrder())
                .sessionId(t.getSessionId())
                .amount(t.getMonto())
                .status(t.getEstado().name())
                .authorizationCode(t.getAuthorizationCode())
                .paymentTypeCode(t.getPaymentTypeCode())
                .responseCode(t.getResponseCode())
                .installmentsNumber(t.getInstallmentsNumber())
                .build();
    }
}