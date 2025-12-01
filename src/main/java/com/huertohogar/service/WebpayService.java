package com.huertohogar.service;

// Imports del SDK v4
import cl.transbank.webpay.webpayplus.WebpayPlus;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCommitResponse;
import cl.transbank.webpay.webpayplus.responses.WebpayPlusTransactionCreateResponse;
import cl.transbank.webpay.exception.TransactionCommitException;
import cl.transbank.webpay.exception.TransactionCreateException;

import com.huertohogar.dto.payment.WebpayCommitResponse;
import com.huertohogar.dto.payment.WebpayInitRequest;
import com.huertohogar.dto.payment.WebpayInitResponse;
import com.huertohogar.entity.Pedido;
import com.huertohogar.entity.Transaccion;
import com.huertohogar.entity.Usuario;
import com.huertohogar.exception.ResourceNotFoundException;
import com.huertohogar.exception.WebpayException;
import com.huertohogar.repository.PedidoRepository;
import com.huertohogar.repository.TransaccionRepository;
import com.huertohogar.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebpayService {

    private final TransaccionRepository transaccionRepository;
    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoService pedidoService;

    @Value("${transbank.webpay.return-url}")
    private String returnUrl;

    @Transactional
    public WebpayInitResponse iniciarTransaccion(String userEmail, WebpayInitRequest request) {
        try {
            Usuario usuario = usuarioRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

            Pedido pedido = pedidoRepository.findByNumeroPedido(request.getBuyOrder())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

            if (!pedido.getUsuario().getId().equals(usuario.getId())) {
                throw new WebpayException("Pedido no pertenece al usuario");
            }

            log.info("Iniciando transacción Webpay para pedido: {}", request.getBuyOrder());
            
            double amount = request.getAmount().doubleValue();

            // CORRECCIÓN 1: Instanciar la clase Transaction (new WebpayPlus.Transaction())
            // Esta versión del SDK requiere instanciar para usar los métodos
            WebpayPlusTransactionCreateResponse response = new WebpayPlus.Transaction().create(
                    request.getBuyOrder(),
                    request.getSessionId(),
                    amount,
                    returnUrl
            );

            Transaccion transaccion = Transaccion.builder()
                    .buyOrder(request.getBuyOrder())
                    .token(response.getToken())
                    .sessionId(request.getSessionId())
                    .pedido(pedido)
                    .usuario(usuario)
                    .monto(request.getAmount())
                    .estado(Transaccion.EstadoTransaccion.INICIADA)
                    // .fechaCreacion se llena automático por @CreatedDate en la entidad, no es necesario aquí
                    .build();

            transaccionRepository.save(transaccion);
            log.info("Transacción creada. Token: {}", response.getToken());

            return WebpayInitResponse.builder()
                    .token(response.getToken())
                    .url(response.getUrl())
                    .build();

        } catch (TransactionCreateException | IOException e) {
            log.error("Error al crear transacción Webpay: {}", e.getMessage());
            throw new WebpayException("Error al iniciar transacción: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage());
            throw new WebpayException("Error interno: " + e.getMessage(), e);
        }
    }

    @Transactional
    public WebpayCommitResponse confirmarTransaccion(String token) {
        try {
            log.info("Confirmando transacción Webpay. Token: {}", token);

            Transaccion transaccion = transaccionRepository.findByToken(token)
                    .orElseThrow(() -> new WebpayException("Transacción no encontrada"));

            // CORRECCIÓN 2: Instanciar para el commit también
            WebpayPlusTransactionCommitResponse response = new WebpayPlus.Transaction().commit(token);

            log.info("Status: {}, Code: {}", response.getStatus(), response.getResponseCode());

            boolean aprobado = "AUTHORIZED".equals(response.getStatus()) && (response.getResponseCode() == 0);

            if (aprobado) {
                transaccion.setEstado(Transaccion.EstadoTransaccion.AUTORIZADA);
                transaccion.setAuthorizationCode(response.getAuthorizationCode());
                transaccion.setResponseCode(String.valueOf(response.getResponseCode()));
                transaccion.setPaymentTypeCode(response.getPaymentTypeCode());
                
                // CORRECCIÓN 4: Casting de byte a int para installments
                transaccion.setInstallmentsNumber((int) response.getInstallmentsNumber());
                
                // CORRECCIÓN 5: Usar fechaAutorizacion (campo real) en vez de fechaConfirmacion
                transaccion.setFechaAutorizacion(LocalDateTime.now());

                pedidoService.actualizarEstado(
                        transaccion.getPedido().getId(),
                        Pedido.EstadoPedido.PAGADO 
                );
                
                log.info("PAGO EXITOSO para pedido: {}", transaccion.getBuyOrder());
            } else {
                transaccion.setEstado(Transaccion.EstadoTransaccion.RECHAZADA);
                transaccion.setResponseCode(String.valueOf(response.getResponseCode()));
                transaccion.setMensajeError("Rechazado por Webpay");

                pedidoService.actualizarEstado(
                        transaccion.getPedido().getId(),
                        Pedido.EstadoPedido.RECHAZADO
                );
                log.warn("PAGO RECHAZADO para pedido: {}", transaccion.getBuyOrder());
            }

            transaccionRepository.save(transaccion);

            return WebpayCommitResponse.builder()
                    .buyOrder(response.getBuyOrder())
                    .sessionId(response.getSessionId())
                    .amount(java.math.BigDecimal.valueOf(response.getAmount()))
                    .status(response.getStatus())
                    .authorizationCode(response.getAuthorizationCode())
                    .paymentTypeCode(response.getPaymentTypeCode())
                    .responseCode(String.valueOf(response.getResponseCode()))
                    // Casting aquí también para el DTO de respuesta
                    .installmentsNumber((int) response.getInstallmentsNumber())
                    .transactionDate(LocalDateTime.now())
                    .build();

        } catch (TransactionCommitException | IOException e) {
            log.error("Error al confirmar: {}", e.getMessage());
            throw new WebpayException("Error al confirmar transacción: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado en confirmación: {}", e.getMessage());
            throw new WebpayException("Error procesando pago: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public Transaccion obtenerEstadoTransaccion(String token) {
        return transaccionRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));
    }

    @Transactional(readOnly = true)
    public boolean esTransaccionExitosa(String token) {
        Transaccion t = obtenerEstadoTransaccion(token);
        // Usar método de negocio de la entidad
        return t.esExitosa();
    }

    @Transactional
    public void manejarTransaccionFallida(String token, String motivo) {
        transaccionRepository.findByToken(token).ifPresent(t -> {
            t.setEstado(Transaccion.EstadoTransaccion.RECHAZADA);
            t.setMensajeError(motivo);
            transaccionRepository.save(t);
            
            if (t.getPedido() != null) {
                pedidoService.actualizarEstado(t.getPedido().getId(), Pedido.EstadoPedido.RECHAZADO);
            }
        });
    }

    @Transactional(readOnly = true)
    public java.util.List<Transaccion> obtenerTransaccionesUsuario(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return transaccionRepository.findByUsuarioIdOrderByFechaCreacionDesc(usuario.getId());
    }
}