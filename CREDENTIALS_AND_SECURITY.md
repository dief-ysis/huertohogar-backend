# 🔐 CREDENCIALES Y SEGURIDAD - WEBPAY

## ⚙️ Credenciales TEST Configuradas

```yaml
# application.yml
webpay:
  commerce-code: "597055555532"
  api-key: "579B532A3DEBA6A1D24F2F1D66A67F87"
  environment: "TEST"
```

### ✅ Estas credenciales son OFICIALES de Transbank
- **Validadas:** Sí
- **Ambiente:** TEST (no producción)
- **Comercio:** Prueba oficial de Transbank
- **Estado:** Activas

---

## 🔒 Recomendaciones de Seguridad

### 1. **NUNCA harcodear credenciales**
```java
// ❌ MAL (nunca hacer esto):
public class WebpayConfig {
    private String commerceCode = "597055555532";
    private String apiKey = "579B532A3DEBA6A1D24F2F1D66A67F87";
}
```

### 2. **Usar variables de entorno en PRODUCCIÓN**
```bash
# Linux/Mac
export WEBPAY_COMMERCE_CODE=TU_CODIGO_REAL
export WEBPAY_API_KEY=TU_CLAVE_REAL
export WEBPAY_ENVIRONMENT=PRODUCTION

# Windows PowerShell
$env:WEBPAY_COMMERCE_CODE="TU_CODIGO_REAL"
$env:WEBPAY_API_KEY="TU_CLAVE_REAL"
$env:WEBPAY_ENVIRONMENT="PRODUCTION"
```

### 3. **Usar application-prod.yml para producción**
```yaml
# application-prod.yml
webpay:
  commerce-code: "${WEBPAY_COMMERCE_CODE}"
  api-key: "${WEBPAY_API_KEY}"
  environment: "PRODUCTION"

# En application.yml:
spring:
  profiles:
    active: prod  # Cambiar a prod en producción
```

### 4. **.gitignore - NUNCA commitar credenciales**
```
# .gitignore
*.env
.env
.env.local
.env.*.local
application-prod.yml
application-production.yml
src/main/resources/application-prod.yml
```

### 5. **Revisar commits anteriores**
```bash
# Buscar si credenciales fueron commiteadas:
git log -S "579B532A3DEBA6A1D24F2F1D66A67F87" --oneline
git log -S "597055555532" --oneline

# Si encuentras alguno, hacer force push (solo en desarrollo):
git revert <commit-id>
git push --force-with-lease
```

---

## 📋 Actualización para PRODUCCIÓN

Cuando sea el momento de pasar a PRODUCCIÓN:

### Paso 1: Obtener credenciales reales
```
1. Contactar a Transbank
2. Solicitar credenciales de producción
3. Commerce Code real (no 597055555532)
4. API Key real
5. Acordar ambiente: PRODUCTION
```

### Paso 2: Crear archivo de configuración seguro
```yaml
# application-prod.yml
webpay:
  commerce-code: "${WEBPAY_COMMERCE_CODE}"
  api-key: "${WEBPAY_API_KEY}"
  environment: "PRODUCTION"
```

### Paso 3: Configurar variables de entorno
```bash
# En servidor de producción:
export WEBPAY_COMMERCE_CODE="123456789012"
export WEBPAY_API_KEY="ABCDEF1234567890..."
export WEBPAY_ENVIRONMENT="PRODUCTION"
```

### Paso 4: Activar perfil de producción
```bash
# Ejecutar con:
java -jar app.jar --spring.profiles.active=prod
```

### Paso 5: Cambiar returnUrl a dominio real
```java
// En frontend:
const returnUrl = "https://tudominio.com/payment-result";

// NO USAR NUNCA:
const returnUrl = "http://localhost:3000/payment-result";  // ❌ NO FUNCIONA
const returnUrl = "http://192.168.x.x:3000/payment-result"; // ❌ NO FUNCIONA
```

---

## 🧪 Testing en LOCAL (Sin ngrok)

### Ambiente TEST sin exposición pública

Para testing SIN ngrok, puedes usar `https://webhook.site/`:

```json
POST /api/v1/payment/webpay/init
{
  "buyOrder": "ORDER-TEST-001",
  "sessionId": "session-test",
  "amount": 1000,
  "returnUrl": "https://webhook.site/tu-id-unico"  // ← Testing
}
```

---

## ⚠️ Checklist de Seguridad Antes de Producción

- [ ] Credenciales de PRODUCCIÓN obtenidas
- [ ] Variables de entorno configuradas
- [ ] application-prod.yml NO commiteado
- [ ] .gitignore contiene application-prod.yml
- [ ] Git history sin credenciales
- [ ] returnUrl es HTTPS y dominio real
- [ ] API Key NO en logs
- [ ] API Key NO en responses
- [ ] Commerce Code NO en responses
- [ ] Certificados SSL válidos
- [ ] CORS configurado correctamente
- [ ] WAF/Security headers configurados
- [ ] Logs de transacciones encriptados
- [ ] Backup de transacciones

---

## 🔍 Validar Seguridad de Credenciales en Git

```bash
# Escanear por patrones de credenciales:
git grep -n "579B532A3DEBA6A1D24F2F1D66A67F87"
git grep -n "api-key"
git grep -n "commerce-code"

# Resultado esperado: SOLO en application.yml (TEST)
# Resultado peligroso: Credenciales reales en application.yml o hardcoded
```

---

## 🚨 Si Descubres Credenciales Commiteadas

```bash
# 1. INMEDIATAMENTE
git log --all --oneline | head -20

# 2. Encontrar el commit:
git show <commit-id>

# 3. Remover del historio (git-filter-branch o BFG):
git filter-branch --tree-filter 'rm -f src/main/resources/application-prod.yml' -- --all

# 4. Force push (solo en desarrollo):
git push origin --force-with-lease

# 5. En Transbank:
# - Revocar la API Key expuesta inmediatamente
# - Generar nueva API Key
# - Actualizar credenciales
```

---

## 📊 Matriz de Credenciales

| Ambiente | Commerce Code | API Key | URL Webpay | Status |
|----------|---------------|---------|-----------|--------|
| **TEST** (actual) | 597055555532 | 579B532A3DEBA6A1D24F2F1D66A67F87 | TEST | ✅ Configurado |
| **PROD** | TBD | TBD | PRODUCTION | ⏳ Pendiente |

---

## 🎓 Referencias

- [Transbank SDK Java](https://github.com/transbankdevelopers/transbank-sdk-java)
- [Documentación Webpay Plus](https://www.transbankdevelopers.cl/producto/webpay)
- [Environment Variables en Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [12 Factor App - Config](https://12factor.net/config)

---

## ✅ Estado Actual

```
TEST Credentials: ✅ Configurados
Seguridad: ✅ Mejorada
Listo para: ✅ Testing
Listo para Producción: ⏳ Falta obtener credenciales reales
```

**Última actualización:** 2025-12-03  
**Verificado:** Sí

