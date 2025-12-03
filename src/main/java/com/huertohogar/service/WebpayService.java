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

import java.math.BigDecimal;
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

        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Pedido pedido = pedidoRepository.findByNumeroPedido(request.getBuyOrder())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("El pedido no pertenece al usuario");
        }

        try {
            WebpayPlus.Transaction transaction = new WebpayPlus.Transaction();
            
            // CORREGIDO: Usar la clase importada correctamente
            WebpayPlusTransactionCreateResponse response = transaction.create(
                    request.getBuyOrder(),
                    request.getSessionId(),
                    request.getAmount().doubleValue(),
                    request.getReturnUrl()
            );

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

            WebpayInitResponse initResponse = new WebpayInitResponse();
            initResponse.setToken(response.getToken());
            initResponse.setUrl(response.getUrl());
            return initResponse;

        } catch (Exception e) {
            log.error("Error al iniciar transacción Webpay: {}", e.getMessage(), e);
            throw new RuntimeException("Error al iniciar transacción: " + e.getMessage(), e);
        }
    }

    @Transactional
    public WebpayCommitResponse confirmarTransaccion(String token) {
        log.info("Confirmando transacción Webpay con token: {}", token);

        Transaccion transaccion = transaccionRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));

        try {
            WebpayPlus.Transaction transaction = new WebpayPlus.Transaction();
            
            // CORREGIDO: Usar la clase importada correctamente
            WebpayPlusTransactionCommitResponse response = transaction.commit(token);

            transaccion.setAuthorizationCode(response.getAuthorizationCode());
            transaccion.setResponseCode(String.valueOf(response.getResponseCode()));
            transaccion.setPaymentTypeCode(response.getPaymentTypeCode());
            transaccion.setInstallmentsNumber((int) response.getInstallmentsNumber());
            transaccion.setFechaAutorizacion(LocalDateTime.now());

            if ("AUTHORIZED".equals(response.getStatus())) {
                transaccion.setEstado(Transaccion.EstadoTransaccion.AUTORIZADA);
                
                Pedido pedido = transaccion.getPedido();
                pedido.setEstado(Pedido.EstadoPedido.PAGADO);
                pedido.setFechaPago(LocalDateTime.now());
                pedidoRepository.save(pedido);

                for (PedidoItem item : pedido.getItems()) {
                    productoService.reducirStock(item.getProducto().getId(), item.getCantidad());
                }
            } else {
                transaccion.marcarComoRechazada(String.valueOf(response.getResponseCode()), "Rechazado por Webpay");
                transaccion.getPedido().setEstado(Pedido.EstadoPedido.RECHAZADO);
                pedidoRepository.save(transaccion.getPedido());
            }

            transaccionRepository.save(transaccion);
            
            WebpayCommitResponse commitResponse = new WebpayCommitResponse();
            commitResponse.setBuyOrder(response.getBuyOrder());
            commitResponse.setSessionId(response.getSessionId());
            commitResponse.setAmount(BigDecimal.valueOf(response.getAmount()));
            commitResponse.setStatus(response.getStatus());
            commitResponse.setAuthorizationCode(response.getAuthorizationCode());
            commitResponse.setPaymentTypeCode(response.getPaymentTypeCode());
            commitResponse.setResponseCode(String.valueOf(response.getResponseCode()));
            commitResponse.setInstallmentsNumber((int) response.getInstallmentsNumber());
            
            return commitResponse;

        } catch (Exception e) {
            // CORREGIDO: metodo setMensajeError
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
        Transaccion transaccion = transaccionRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada"));
        transaccion.marcarComoRechazada("FAILED", motivo);
        transaccionRepository.save(transaccion);
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
}