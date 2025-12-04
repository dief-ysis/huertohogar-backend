package com.huertohogar.controller;

import com.huertohogar.dto.payment.WebpayCommitResponse;
import com.huertohogar.dto.payment.WebpayInitRequest;
import com.huertohogar.dto.payment.WebpayInitResponse;
import com.huertohogar.entity.Transaccion;
import com.huertohogar.service.WebpayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.huertohogar.dto.payment.WebpayCommitRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WEBPAY CONTROLLER
 * 
 * Endpoints de integración con Webpay Plus
 * 
 * CUMPLE CON PREGUNTAS P102-138:
 * - Iniciar transacción Webpay
 * - Confirmar pago
 * - Consultar estado
 * - Manejo de errores
 */

/*
 * PRINCIPIO: Adapter Pattern (Implícito)
 * Este controlador actúa como adaptador entre tu API REST y el servicio de Transbank.
 * Recibe peticiones JSON de tu frontend y orquesta la lógica compleja de Webpay.
 */
@Slf4j
@RestController
@RequestMapping("/v1/payment/webpay")
@RequiredArgsConstructor
@Tag(name = "Webpay", description = "Integración con Webpay Plus de Transbank")
public class WebpayController {

    private final WebpayService webpayService;

    /**
     * PREGUNTA P102-108: Iniciar transacción Webpay
     * 
     * POST /v1/payment/webpay/init
     * 
     * Request body:
     * {
     *   "buyOrder": "ORDER-123456",
     *   "sessionId": "session123",
     *   "amount": 15000,
     *   "returnUrl": "http://localhost:3000/payment-result"
     * }
     * 
     * Response:
     * {
     *   "token": "e9d555262db0f989e49d724b4db0b0af367cc415cde41f500a776550fc5fddd3",
     *   "url": "https://webpay3gint.transbank.cl/webpayserver/initTransaction"
     * }
     */

    /*
     * PASO 1: Iniciar.
     * El frontend envía los datos, nosotros contactamos a Transbank, 
     * y devolvemos un token + URL. El frontend debe redirigir al usuario a esa URL.
     */
    @PostMapping("/init")
    @Operation(summary = "Iniciar transacción")
    public ResponseEntity<WebpayInitResponse> iniciarTransaccion(
            @Valid @RequestBody WebpayInitRequest request,
            Authentication authentication
    ) {
        log.info("Iniciando transacción Webpay para buyOrder: {}", request.getBuyOrder());
        String userEmail = authentication.getName();
        WebpayInitResponse response = webpayService.iniciarTransaccion(userEmail, request);
        return ResponseEntity.ok(response);
    }

    /**
     * PREGUNTA P109-120: Confirmar transacción Webpay
     * 
     * POST /v1/payment/webpay/commit
     * 
     * Query param: token=e9d555262db0f989e49d724b4db0b0af367cc415cde41f500a776550fc5fddd3
     * 
     * Response:
     * {
     *   "buyOrder": "ORDER-123456",
     *   "sessionId": "session123",
     *   "amount": 15000,
     *   "status": "AUTHORIZED",
     *   "authorizationCode": "1213",
     *   "paymentTypeCode": "VD",
     *   "responseCode": "0",
     *   "installmentsNumber": 0,
     *   "transactionDate": "2024-12-01T10:30:00"
     * }
     */

    /*
     * PASO 2: Confirmar (Commit).
     * Transbank devuelve al usuario a tu frontend con un token en la URL (token_ws).
     * Tu frontend captura ese token y llama INMEDIATAMENTE a este endpoint.
     */
    @PostMapping("/commit")
    @Operation(summary = "Confirmar transacción Webpay")
    public ResponseEntity<WebpayCommitResponse> confirmarTransaccion(
            @RequestBody WebpayCommitRequest request
    ) {
        // Extraemos el token del objeto JSON
        String token = request.getToken();
        
        log.info("Confirmando transacción Webpay. Token recibido: {}", token);
        
        // Validación básica
        if (token == null || token.trim().isEmpty()) {
             throw new IllegalArgumentException("El token es requerido dentro del cuerpo JSON");
        }

        WebpayCommitResponse response = webpayService.confirmarTransaccion(token);
        return ResponseEntity.ok(response);
    }

    /**
     * PREGUNTA P121-130: Consultar estado de transacción
     * 
     * GET /v1/payment/webpay/status/{token}
     * 
     * Response:
     * {
     *   "id": 1,
     *   "buyOrder": "ORDER-123456",
     *   "token": "e9d555262...",
     *   "estado": "AUTORIZADA",
     *   "monto": 15000,
     *   "authorizationCode": "1213",
     *   "fechaCreacion": "2024-12-01T10:25:00",
     *   "fechaAutorizacion": "2024-12-01T10:30:00"
     * }
     */
    @GetMapping("/status/{token}")
    @Operation(
        summary = "Estado de transacción",
        description = "Consultar el estado actual de una transacción usando su token"
    )
    public ResponseEntity<Map<String, Object>> obtenerEstadoTransaccion(
            @PathVariable String token
    ) {
        log.info("Consultando estado de transacción. Token: {}", token);
        
        Transaccion transaccion = webpayService.obtenerEstadoTransaccion(token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", transaccion.getId());
        response.put("buyOrder", transaccion.getBuyOrder());
        response.put("token", transaccion.getToken());
        response.put("estado", transaccion.getEstado().name());
        response.put("monto", transaccion.getMonto());
        response.put("authorizationCode", transaccion.getAuthorizationCode());
        response.put("responseCode", transaccion.getResponseCode());
        response.put("fechaCreacion", transaccion.getFechaCreacion());
        response.put("fechaAutorizacion", transaccion.getFechaAutorizacion());
        response.put("esExitosa", transaccion.esExitosa());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Verificar si transacción fue exitosa
     * 
     * GET /v1/payment/webpay/verify/{token}
     * 
     * Response:
     * {
     *   "exitosa": true,
     *   "mensaje": "Pago autorizado exitosamente"
     * }
     */
    @GetMapping("/verify/{token}")
    @Operation(
        summary = "Verificar pago exitoso",
        description = "Verificar rápidamente si una transacción fue exitosa"
    )
    public ResponseEntity<Map<String, Object>> verificarPagoExitoso(
            @PathVariable String token
    ) {
        boolean exitosa = webpayService.esTransaccionExitosa(token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("exitosa", exitosa);
        response.put("mensaje", exitosa ? 
                "Pago autorizado exitosamente" : 
                "Pago no autorizado o pendiente");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtener historial de transacciones del usuario
     * 
     * GET /v1/payment/webpay/historial
     */
    @GetMapping("/historial")
    @Operation(
        summary = "Historial de transacciones",
        description = "Obtener todas las transacciones del usuario autenticado"
    )
    public ResponseEntity<List<Transaccion>> obtenerHistorialTransacciones(
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        List<Transaccion> transacciones = webpayService.obtenerTransaccionesUsuario(userEmail);
        return ResponseEntity.ok(transacciones);
    }

    /**
     * PREGUNTA P131-138: Manejo de transacción fallida
     * 
     * Este endpoint es llamado internamente o desde el frontend
     * cuando se detecta un error en el proceso de pago
     */
    @PostMapping("/failure")
    @Operation(
        summary = "Reportar fallo de transacción",
        description = "Marcar una transacción como fallida con su motivo"
    )
    public ResponseEntity<String> reportarFallo(
            @RequestParam String token,
            @RequestParam String motivo
    ) {
        log.warn("Reportando fallo de transacción. Token: {}, Motivo: {}", token, motivo);
        
        webpayService.manejarTransaccionFallida(token, motivo);
        
        return ResponseEntity.ok("Transacción fallida registrada");
    }
}