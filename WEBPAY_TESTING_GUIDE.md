# 🧪 GUÍA DE TESTING - WEBPAY

## 1️⃣ Iniciar el Backend

```bash
cd c:\Users\LENOVO\Downloads\fullstack\EVA3\archivosEVA3\EVA3OFI\huertohogar-backend
mvn spring-boot:run
```

**Esperar a ver:**
```
✓ WebpayPlus.Transaction configurado correctamente
✓ Parámetros validados correctamente
```

---

## 2️⃣ Endpoints Disponibles

### Crear Transacción Webpay
```http
POST /api/v1/payment/webpay/init
Content-Type: application/json

{
  "buyOrder": "ORDER-20241203-001",
  "sessionId": "session-123456789",
  "amount": 15000,
  "returnUrl": "https://tu-dominio.com/payment-result"
}
```

**Response exitosa:**
```json
{
  "token": "eyJ0eXAiOiJKV1QiLC...",
  "url": "https://webpay.transbank.cl/webpayplus/initTransaction?token_ws=..."
}
```

**Response con error (token NULL):**
```json
{
  "status": 500,
  "message": "Error al generar token Webpay..."
}
```

---

### Confirmar Pago
```http
POST /api/v1/payment/webpay/commit
Content-Type: application/json

{
  "token": "eyJ0eXAiOiJKV1QiLC..."
}
```

**Response exitosa:**
```json
{
  "buyOrder": "ORDER-20241203-001",
  "sessionId": "session-123456789",
  "status": "AUTHORIZED",
  "authorizationCode": "123456",
  "responseCode": 0,
  "amount": 15000
}
```

---

## 3️⃣ Debugging - Qué Revisar

### Si falla **CREATE** (generar token)

**Logs a buscar:**
```
❌ Error al generar token Webpay...
❌ Error al iniciar transacción Webpay: ...
```

**Causas posibles:**
| Causa | Solución |
|-------|----------|
| API Key incorrecta | Verificar en `application.yml` que sea válida |
| Commerce Code incorrecto | Debe ser `597055555532` para TEST |
| returnUrl = localhost | Cambiar a URL pública con ngrok |
| Parámetros inválidos | Revisar validación (buyOrder, sessionId, amount) |

### Si falla **COMMIT** (confirmar pago)

**Logs a buscar:**
```
Token inválido o vacío
❌ PAGO RECHAZADO - Status: ..., ResponseCode: ...
```

**Causas posibles:**
| Causa | Solución |
|-------|----------|
| Token NULL | Token de CREATE falló - revisar pasos anteriores |
| Token expirado | Completar pago dentro de tiempo límite |
| Status = FAILED | Rechazado por Transbank - revisar ResponseCode |

---

## 4️⃣ Testing con ngrok

### Paso 1: Instalar ngrok (si no está instalado)
```bash
# Descargar de https://ngrok.com/download
# O usar Chocolatey si está instalado:
choco install ngrok
```

### Paso 2: Exponer localhost:8080
```bash
ngrok http 8080
```

**Output:**
```
Forwarding    https://abc12345.ngrok.app -> http://localhost:8080
```

### Paso 3: Usar la URL en returnUrl
```json
{
  "returnUrl": "https://abc12345.ngrok.app/api/v1/payment/webpay/return"
}
```

---

## 5️⃣ Tarjetas de Test Transbank

### ✅ Pago Exitoso (AUTHORIZED)
```
Tarjeta: 4051885600446623
Vencimiento: 12/25
CVV: 123
```

### ❌ Pago Rechazado (FAILED)
```
Tarjeta: 4051885600446631
Vencimiento: 12/25
CVV: 123
```

---

## 6️⃣ Logs Esperados (CASO EXITOSO)

```
[INFO] Iniciando transacción Webpay para usuario: user@example.com
[INFO] 📡 Llamando a WebpayPlus.Transaction.create()...
[INFO]    - buyOrder: ORDER-20241203-001
[INFO]    - amount: 15000
[INFO]    - returnUrl: https://abc12345.ngrok.app/api/v1/payment/webpay/return
[INFO] ✓ Token recibido: eyJ0eXAiOi...
[INFO] ✓ Transacción guardada en BD con ID: 123

[INFO] Confirmando transacción Webpay con token: eyJ0eXAiOi...
[INFO] 📡 Llamando a WebpayPlus.Transaction.commit()...
[INFO] ✓ Respuesta recibida. Status: AUTHORIZED, ResponseCode: 0
[INFO] ✓ PAGO AUTORIZADO
[INFO] ✓ Stock reducido para todos los items
```

---

## 7️⃣ Logs Esperados (CASO CON ERROR)

```
[ERROR] ❌ Error al iniciar transacción Webpay: buyOrder inválido (max 26 caracteres)

[ERROR] ❌ Error al generar token Webpay...

[ERROR] ❌ Token inválido o vacío

[ERROR] ⚠ PAGO RECHAZADO - Status: FAILED, ResponseCode: -1
```

---

## 8️⃣ Checklist de Validación

- [ ] Backend compilado sin errores
- [ ] Credenciales en `application.yml` son válidas
- [ ] `WebpayConfig.java` tiene @Configuration
- [ ] `WebpayService.java` inyecta `WebpayPlus.Transaction`
- [ ] Parámetros de request cumplen restricciones
- [ ] returnUrl es URL pública (no localhost)
- [ ] Logs muestran ✓ (no ❌)
- [ ] Token se recibe en response de create
- [ ] Status es AUTHORIZED en commit

---

**¡Listo para probar Webpay! 🚀**
