# Mejoras en el Sistema de Comprobantes

## Resumen de Cambios

Se han implementado mejoras significativas en el sistema de gestión de comprobantes para mejorar el control, la auditoría y la regeneración automática de archivos.

---

## 1. Nuevos Campos en la Entidad Comprobante

### Campos de Control de Impresiones
- **`contadorImpresiones`**: Contador de cuántas veces se ha impreso/descargado el comprobante
- **`fechaPrimeraImpresion`**: Timestamp de la primera vez que se imprimió

### Campos de Anulación
- **`fechaAnulacion`**: Timestamp de cuándo se anuló el comprobante
- **`usuarioAnulacion`**: Nombre del usuario que anuló el comprobante

### Campos de Envío Digital
- **`fechaEnvioDigital`**: Timestamp del envío por email/WhatsApp
- **`emailEnvio`**: Email al que se envió el comprobante

---

## 2. Estados de Comprobantes - Flujo Mejorado

### Estado GENERADO
- **Cuándo**: Al crear el comprobante por primera vez
- **Significa**: PDFs generados correctamente, listo para usar
- **Transiciones**: Puede pasar a IMPRESO, ENVIADO, ANULADO o ERROR

### Estado IMPRESO
- **Cuándo**:
  - Primera descarga de ticket/PDF ✅ NUEVO
  - Al llamar `reimprimirComprobante()`
- **Significa**: El documento fue impreso físicamente
- **Campos relacionados**:
  - `contadorImpresiones` se incrementa
  - `fechaPrimeraImpresion` se establece en primera impresión

### Estado ENVIADO
- **Cuándo**: Al enviar por email usando `enviarPorEmail()`
- **Significa**: Cliente recibió el comprobante digitalmente
- **Campos relacionados**:
  - `fechaEnvioDigital`
  - `emailEnvio`

### Estado ANULADO
- **Cuándo**: Al llamar `anularComprobante()`
- **Significa**: Comprobante cancelado (no válido)
- **Campos relacionados**:
  - `fechaAnulacion`
  - `usuarioAnulacion`
  - `observaciones` (con motivo)
- **Restricciones**: No se puede reimprimir, descargar ni enviar

### Estado ERROR
- **Cuándo**: Si falla la generación de archivos
- **Significa**: Problema técnico en la generación
- **Restricciones**: No se puede anular

---

## 3. Nuevas Funcionalidades

### 3.1. Auto-marcar como IMPRESO ✅
**Problema resuelto**: Antes solo se marcaba como impreso en reimpresiones.

**Solución**: Nuevo método `descargarYMarcarImpreso()` que:
- Marca automáticamente como IMPRESO en la primera descarga
- Incrementa el contador de impresiones
- Registra fecha de primera impresión
- Regenera archivos si no existen físicamente

**Endpoints afectados**:
- `GET /api/v1/comprobantes/{id}/ticket`
- `GET /api/v1/comprobantes/{id}/ticket/imprimir`
- `GET /api/v1/comprobantes/{id}/pdf`

### 3.2. Regeneración Automática de Archivos ✅
**Problema resuelto**: Si se eliminaban físicamente los PDFs, no se podían volver a descargar.

**Solución**:
- `descargarYMarcarImpreso()` detecta archivos faltantes y los regenera
- `reimprimirComprobante()` verifica existencia y regenera si es necesario
- Se mantiene la misma numeración y datos originales

**Casos de uso**:
- Limpieza de archivos antiguos
- Migración de servidores
- Pérdida accidental de archivos

### 3.3. Validaciones Mejoradas en Anulación ✅
**Mejoras implementadas**:
- ✅ No permite anular comprobantes ya anulados
- ✅ No permite anular comprobantes con error
- ✅ Motivo obligatorio
- ✅ Registra usuario que anuló
- ✅ Registra fecha de anulación
- ✅ Impide operaciones sobre comprobantes anulados

**Ejemplo de uso**:
```bash
POST /api/v1/comprobantes/123/anular?motivo=Error en el pedido
```

### 3.4. Envío por Email ✅ (Parcial)
**Estado**: Base implementada, requiere configuración de JavaMailSender

**Funcionalidad actual**:
- Valida email
- Verifica que el PDF existe
- Marca como ENVIADO
- Registra email y fecha de envío

**Pendiente**:
- Configurar SMTP en `application.properties`
- Implementar JavaMailSender
- Diseñar plantilla de email
- Adjuntar PDF

**Endpoint**:
```bash
POST /api/v1/comprobantes/123/enviar?email=cliente@example.com
```

---

## 4. Contador de Impresiones

### Funcionamiento
- Se incrementa en cada descarga/impresión
- Se mantiene histórico incluso si se regenera el archivo
- Útil para auditoría y control

### Ejemplo de uso:
```java
// Al descargar:
Comprobante comprobante = ...;
comprobante.getContadorImpresiones(); // 1
comprobante.getFechaPrimeraImpresion(); // 2025-01-15 10:30:00

// Al reimprimir:
comprobante.getContadorImpresiones(); // 2
```

---

## 5. Migración de Base de Datos

**Archivo**: `V005__agregar_campos_comprobantes.sql`

**Columnas agregadas**:
```sql
- contador_impresiones INTEGER DEFAULT 0
- fecha_primera_impresion TIMESTAMP
- fecha_anulacion TIMESTAMP
- usuario_anulacion VARCHAR(255)
- fecha_envio_digital TIMESTAMP
- email_envio VARCHAR(255)
```

**Ejecución**: Automática con Flyway al iniciar la aplicación

---

## 6. Endpoints Actualizados

### GET /api/v1/comprobantes/{id}/ticket
- ✅ Auto-marca como IMPRESO
- ✅ Incrementa contador
- ✅ Regenera si no existe

### GET /api/v1/comprobantes/{id}/ticket/imprimir
- ✅ Auto-marca como IMPRESO
- ✅ Header para impresión automática
- ✅ Regenera si no existe

### GET /api/v1/comprobantes/{id}/pdf
- ✅ Auto-marca como IMPRESO
- ✅ Incrementa contador
- ✅ Regenera si no existe

### POST /api/v1/comprobantes/{id}/reimprimir
- ✅ Valida no anulado
- ✅ Regenera archivos faltantes
- ✅ Incrementa contador

### POST /api/v1/comprobantes/{id}/anular?motivo=...
- ✅ Validaciones mejoradas
- ✅ Registra usuario
- ✅ Registra fecha
- ✅ Motivo obligatorio

### POST /api/v1/comprobantes/{id}/enviar?email=...
- ✅ Nuevo endpoint
- ✅ Valida email
- ✅ Marca como ENVIADO
- ⚠️ Envío real pendiente de configuración SMTP

---

## 7. Casos de Uso

### Caso 1: Cliente pierde el comprobante
```bash
# Reimprimir sin problemas
POST /api/v1/comprobantes/123/reimprimir

# El sistema:
# - Verifica que exista el archivo
# - Si no existe, lo regenera automáticamente
# - Incrementa contador de impresiones
# - Marca como IMPRESO
```

### Caso 2: Error en el pedido
```bash
# Anular comprobante
POST /api/v1/comprobantes/123/anular?motivo=Error en productos

# El sistema:
# - Valida que no esté ya anulado
# - Registra usuario que anuló
# - Registra fecha de anulación
# - Impide futuras operaciones
```

### Caso 3: Envío digital
```bash
# Enviar por email
POST /api/v1/comprobantes/123/enviar?email=cliente@mail.com

# El sistema:
# - Valida email
# - Marca como ENVIADO
# - Registra email y fecha
# (Pendiente: envío real por SMTP)
```

### Caso 4: Auditoría
```bash
# Consultar historial
GET /api/v1/comprobantes/123

Response:
{
  "contadorImpresiones": 3,
  "fechaPrimeraImpresion": "2025-01-15T10:30:00",
  "estado": "IMPRESO",
  "fechaCreacion": "2025-01-15T10:00:00",
  ...
}
```

---

## 8. Beneficios

✅ **Mayor control**: Contador de impresiones y fechas
✅ **Auditoría completa**: Quién, cuándo y por qué se anuló
✅ **Resiliencia**: Regeneración automática de archivos
✅ **Mejor UX**: No errores por archivos faltantes
✅ **Trazabilidad**: Historial completo de cada comprobante
✅ **Preparado para futuro**: Base para envío por email/WhatsApp

---

## 9. Próximos Pasos (Opcional)

### Envío de Email Completo
1. Agregar dependencia de JavaMailSender
2. Configurar SMTP en `application.properties`
3. Crear plantilla HTML de email
4. Implementar lógica de envío real

### WhatsApp Integration
1. Integrar con API de WhatsApp Business
2. Agregar campo de teléfono en Comprobante
3. Crear endpoint de envío por WhatsApp

### Reportes de Auditoría
1. Endpoint para obtener historial de impresiones
2. Filtros por fecha, usuario, estado
3. Exportar a Excel/PDF

---

## 10. Notas Técnicas

- Todos los métodos son transaccionales para garantizar consistencia
- Se usa `synchronized` en generación de correlativos para evitar duplicados
- Los archivos se regeneran con los mismos datos originales (no se pierde numeración)
- La anulación es permanente e irreversible
- Los comprobantes anulados mantienen su numeración para auditoría SUNAT
