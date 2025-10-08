# 📋 Changelog - Sistema Mozaico

## 🆕 Versión 2.2.0 - API Pública de Carta de Productos

### 🎯 **Resumen de Cambios**
Se implementaron endpoints públicos para mostrar la carta/menú de productos a los clientes sin necesidad de autenticación, ideal para aplicaciones web y móviles de cara al cliente.

---

### 🌐 **Nuevos Endpoints Públicos**

#### 1. **Endpoint de Carta Pública (Multitenant)**
**Archivo modificado:** `src/main/java/com/djasoft/mozaico/web/controllers/ProductoController.java`

**Endpoint agregado:**
```
GET /api/v1/productos/public/{idEmpresa}/carta
```

**Características:**
- ❌ **NO requiere autenticación** - Acceso público
- 🏢 **Multitenant** - Filtra por empresa obligatoriamente
- ✅ Filtra automáticamente productos disponibles (`disponible = true`)
- ✅ Filtra automáticamente productos activos (`estado = ACTIVO`)
- 🔍 Permite filtrado opcional por categoría (`?idCategoria=X`)
- 📱 Ideal para apps móviles y web de clientes

**Parámetros de ruta:**
- `idEmpresa` (Integer) - **OBLIGATORIO** - ID de la empresa

**Parámetros de consulta:**
- `idCategoria` (Long) - Opcional - Filtrar por categoría específica

**Respuesta:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Carta de productos obtenida exitosamente",
  "data": [
    {
      "idProducto": 1,
      "nombre": "Hamburguesa Clásica",
      "descripcion": "Deliciosa hamburguesa con carne de res",
      "precio": 12.00,
      "categoria": {
        "idCategoria": 1,
        "nombre": "Platos Principales"
      },
      "disponible": true,
      "imagenUrl": "/uploads/images/products/hamburguesa.jpg",
      "tiempoPreparacion": 15,
      "calorias": 550
    }
  ]
}
```

#### 2. **Endpoint de Carta por Categoría (Multitenant)**
```
GET /api/v1/productos/public/{idEmpresa}/carta/por-categoria
```

**Características:**
- 🏢 **Multitenant** - Requiere ID de empresa
- Agrupa productos por categoría
- Mismo filtrado automático (disponibles y activos)
- Útil para interfaces con categorías

---

### 🔒 **Configuración de Seguridad**

**Archivo modificado:** `src/main/java/com/djasoft/mozaico/config/SecurityConfig.java`

**Cambio realizado:**
```java
// Endpoints públicos para la carta de productos (sin autenticación)
.requestMatchers("/api/v1/productos/public/**").permitAll()
```

**Endpoints públicos totales:**
- `/api/v1/auth/**` - Autenticación
- `/api/v1/productos/public/**` - **Carta pública (NUEVO)**
- `/api/v1/comprobantes/**` - Comprobantes
- `/uploads/images/products/**` - Imágenes
- `/swagger-ui/**` - Documentación

---

### 📚 **Nueva Documentación**

#### 1. **Guía de API Pública**
**Archivo:** `docs/API_CARTA_PUBLICA.md`

**Contenido:**
- Descripción completa de endpoints
- Ejemplos de uso con cURL
- Integración con JavaScript Vanilla
- Ejemplos completos para Vue.js
- Ejemplos completos para React
- Casos de uso y ventajas
- Notas de seguridad

#### 2. **Página de Ejemplo Interactiva**
**Archivo:** `docs/ejemplo-carta-publica.html`

**Características:**
- 🎨 Diseño moderno y responsive
- 🔍 Búsqueda en tiempo real
- 📂 Filtrado por categorías
- 📊 Estadísticas de productos
- 💳 Simulación de carrito
- 📱 Compatible con móviles
- ⚡ Sin necesidad de autenticación

---

### 💻 **Ejemplos de Uso**

#### cURL (Sin autenticación)
```bash
# ⚠️ IMPORTANTE: Incluir ID de empresa (multitenant)

# Obtener toda la carta de la empresa 1
curl -X GET "http://localhost:8091/api/v1/productos/public/1/carta"

# Filtrar por categoría de la empresa 1 (ej: Bebidas - id 2)
curl -X GET "http://localhost:8091/api/v1/productos/public/1/carta?idCategoria=2"

# Obtener carta de otra empresa (empresa 2)
curl -X GET "http://localhost:8091/api/v1/productos/public/2/carta"
```

#### JavaScript
```javascript
// IMPORTANTE: Definir el ID de la empresa
const ID_EMPRESA = 1; // Cambiar según tu empresa

// Obtener carta completa
async function obtenerCarta() {
    const response = await fetch(
        `http://localhost:8091/api/v1/productos/public/${ID_EMPRESA}/carta`
    );
    const data = await response.json();
    return data.data; // Array de productos
}

// Filtrar por categoría
async function obtenerCartaPorCategoria(idCategoria) {
    const url = `http://localhost:8091/api/v1/productos/public/${ID_EMPRESA}/carta?idCategoria=${idCategoria}`;
    const response = await fetch(url);
    const data = await response.json();
    return data.data;
}
```

#### Vue.js
```vue
<template>
  <div>
    <select v-model="categoriaId" @change="cargarProductos">
      <option value="">Todas</option>
      <option v-for="cat in categorias" :value="cat.id">
        {{ cat.nombre }}
      </option>
    </select>

    <div v-for="producto in productos" :key="producto.idProducto">
      <h3>{{ producto.nombre }}</h3>
      <p>S/ {{ producto.precio }}</p>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      productos: [],
      categoriaId: '',
      idEmpresa: 1  // ID de la empresa (multitenant)
    }
  },
  methods: {
    async cargarProductos() {
      const url = this.categoriaId
        ? `/api/v1/productos/public/${this.idEmpresa}/carta?idCategoria=${this.categoriaId}`
        : `/api/v1/productos/public/${this.idEmpresa}/carta`;

      const response = await this.$axios.get(url);
      this.productos = response.data.data;
    }
  },
  mounted() {
    this.cargarProductos();
  }
}
</script>
```

---

### 🎯 **Casos de Uso**

1. **Aplicación Web de Pedidos**
   - Mostrar menú a clientes
   - No requiere login para ver productos
   - Filtrado por categorías (Entradas, Platos Principales, Postres, Bebidas)

2. **App Móvil para Clientes**
   - Carta digital interactiva
   - Búsqueda de productos
   - Información detallada (precio, calorías, tiempo de preparación)

3. **Quioscos Digitales**
   - Menús interactivos en el restaurante
   - Sin necesidad de autenticación
   - Interfaz táctil

4. **Integración con Delivery Apps**
   - APIs de terceros pueden acceder a la carta
   - Sincronización automática de productos

---

### ✅ **Ventajas**

- 🚀 **Rendimiento:** Sin overhead de validación JWT
- 🔓 **Accesibilidad:** Cualquiera puede ver la carta
- 🔒 **Seguridad:** Solo lectura, no expone datos sensibles
- 🎯 **Filtrado inteligente:** Solo productos disponibles y activos
- 🌐 **CORS habilitado:** Accesible desde cualquier dominio
- 📱 **Mobile-friendly:** Ideal para apps móviles

---

### 🔒 **Seguridad**

**Lo que SÍ permite:**
- ✅ Ver productos disponibles y activos
- ✅ Filtrar por categoría
- ✅ Ver información pública (nombre, precio, descripción, imagen)

**Lo que NO permite:**
- ❌ Ver productos inactivos o no disponibles
- ❌ Crear, editar o eliminar productos
- ❌ Ver información administrativa
- ❌ Acceder a otros recursos sin autenticación

---

### ⚠️ **Notas Importantes**

1. **Reiniciar la aplicación** después de los cambios en `SecurityConfig`
2. **CORS configurado** para `http://localhost:5173` (modificar según frontend)
3. **Imágenes públicas:** `/uploads/images/products/**` también accesibles sin auth
4. **Operaciones admin:** Usar endpoints con autenticación para gestión

---

## 🆕 Versión 2.1.0 - Sistema de Tickets PDF e Impresión Automática

### 🎯 **Resumen de Cambios**
Se implementó generación de tickets en formato PDF térmico (80mm) con funcionalidad de impresión automática, mejorando significativamente la experiencia de uso en puntos de venta.

---

### 🖨️ **Mejoras en Sistema de Comprobantes**

#### 1. **Tickets en Formato PDF (80mm)**
**Archivo modificado:** `src/main/java/com/djasoft/mozaico/application/services/impl/ComprobanteServiceImpl.java`

**Cambios realizados:**
- ✅ Tickets ahora se generan en formato PDF en lugar de TXT
- ✅ Tamaño optimizado para impresoras térmicas (80mm = 226.77 puntos)
- ✅ Diseño compacto con fuentes pequeñas (8-12pt)
- ✅ Formato profesional manteniendo toda la información necesaria
- ✅ Manejo robusto de errores con limpieza de archivos corruptos

**Características del Ticket PDF:**
- Encabezado con nombre de empresa y RUC
- Tipo y número de comprobante
- Información del pedido (fecha, mesa, cliente, empleado)
- Detalle de productos con cantidades y precios
- Subtotal, IGV (si aplica), descuentos
- Total destacado
- Forma de pago y referencia
- Mensaje personalizado de la empresa
- Hash de verificación

#### 2. **Corrección de PDFs Corruptos**
**Problema resuelto:** PDFs con 0 bytes o corruptos

**Soluciones implementadas:**
```java
// Validación previa antes de crear el archivo
if (pago == null || pago.getPedido() == null) {
    throw new IllegalArgumentException("Datos no válidos para generar PDF");
}

// Limpieza automática de archivos corruptos en caso de error
try {
    if (document != null) document.close();
    else if (pdfDoc != null) pdfDoc.close();
    else if (writer != null) writer.close();

    Files.deleteIfExists(rutaArchivo);
} catch (Exception cleanupEx) {
    log.error("Error al limpiar recursos", cleanupEx);
}
```

#### 3. **Nuevos Endpoints de Impresión**
**Archivo modificado:** `src/main/java/com/djasoft/mozaico/infrastructure/controllers/ComprobanteController.java`

**Endpoints agregados:**

**a) Descargar Ticket PDF**
```
GET /api/v1/comprobantes/{id}/ticket
```
- Descarga el ticket en formato PDF
- Content-Type: `application/pdf`
- Content-Disposition: `attachment`

**b) Imprimir Ticket Automáticamente**
```
GET /api/v1/comprobantes/{id}/ticket/imprimir
```
- Descarga con headers para impresión automática
- Content-Disposition: `inline`
- Header personalizado: `X-Auto-Print: true`
- Optimizado para abrir el diálogo de impresión del navegador

---

### 📚 **Nueva Documentación**

#### 1. **Guía de Implementación**
**Archivo:** `docs/IMPRESION_AUTOMATICA_TICKETS.md`

**Contenido:**
- Descripción de todos los endpoints
- Ejemplos de implementación en JavaScript Vanilla
- Ejemplos con Fetch API y Blob
- Integración con Vue.js y React
- Configuración de impresoras térmicas
- Notas de compatibilidad y seguridad

#### 2. **Página de Ejemplo Completa**
**Archivo:** `docs/ejemplo-impresion-ticket.html`

**Características:**
- Interfaz gráfica completa para pruebas
- Sistema de login integrado
- 4 opciones de impresión/descarga:
  - Imprimir con iframe (automático)
  - Imprimir con ventana nueva
  - Descargar ticket PDF
  - Descargar comprobante A4
- Diseño moderno y responsive
- Manejo de errores y feedback visual

---

### 🔧 **Cambios Técnicos**

#### Librerías Utilizadas
- **iText 7.2.5** para generación de PDFs
  - `kernel` - Motor PDF
  - `layout` - Sistema de layout
  - `io` - Manejo de fuentes e I/O

#### Formato del Ticket
```
Ancho: 80mm (226.77 puntos)
Alto: Variable según contenido
Márgenes: 10 puntos
Fuentes:
  - Título: Helvetica Bold 12pt
  - Normal: Helvetica 8pt
  - Detalles: Helvetica 7pt
  - Hash: Helvetica 6pt
```

---

### 💻 **Ejemplos de Uso**

#### JavaScript (Impresión Automática con iframe)
```javascript
async function imprimirTicket(idComprobante, token) {
    const response = await fetch(
        `http://localhost:8091/api/v1/comprobantes/${idComprobante}/ticket/imprimir`,
        { headers: { 'Authorization': `Bearer ${token}` } }
    );

    const blob = await response.blob();
    const url = URL.createObjectURL(blob);

    const iframe = document.createElement('iframe');
    iframe.style.display = 'none';
    iframe.src = url;

    iframe.onload = () => {
        setTimeout(() => {
            iframe.contentWindow.print();
        }, 500);
    };

    document.body.appendChild(iframe);
}
```

#### Vue.js / Axios
```javascript
async imprimirTicket(idComprobante) {
    const response = await this.$axios.get(
        `/api/v1/comprobantes/${idComprobante}/ticket/imprimir`,
        { responseType: 'blob' }
    );

    const url = URL.createObjectURL(response.data);
    // ... lógica de impresión
}
```

---

### ⚠️ **Notas Importantes**

1. **Compatibilidad de Navegadores:**
   - Chrome: ✅ Totalmente compatible
   - Firefox: ✅ Compatible
   - Edge: ✅ Compatible
   - Safari: ⚠️ Puede requerir permisos adicionales

2. **Configuración de Impresoras:**
   - **Térmicas (80mm):** Configurar como predeterminada, sin márgenes
   - **Normales:** Usar comprobante A4 (`/pdf`) en su lugar

3. **Seguridad:**
   - Todos los endpoints requieren autenticación JWT
   - Configurar CORS adecuadamente en producción

4. **Archivos Antiguos:**
   - Los tickets `.txt` existentes no se migran automáticamente
   - Nuevos comprobantes generarán `.pdf`
   - Considerar limpiar archivos antiguos si es necesario

---

## 🆕 Versión 2.0.0 - Sistema de Empresa y Facturación Flexible

### 🎯 **Resumen de Cambios**
Se implementó un sistema completo de gestión empresarial con configuración flexible de facturación, permitiendo operar desde negocio informal hasta facturación electrónica completa según reglamentación SUNAT 2024.

---

## 🏢 **Nuevas Entidades**

### 1. **Entidad `Empresa`**
Entidad principal que maneja la configuración básica de la empresa.

**Ubicación:** `src/main/java/com/djasoft/mozaico/domain/entities/Empresa.java`

**Campos principales:**
- `nombre`: Nombre comercial de la empresa
- `descripcion`: Descripción del negocio
- `direccion`, `telefono`, `email`: Datos de contacto
- `logoUrl`, `paginaWeb`: Identidad digital
- `activa`: Estado de la empresa
- `tipoOperacion`: Determina capacidades de emisión (TICKET_SIMPLE, BOLETA_MANUAL, FACTURACION_ELECTRONICA, MIXTO)
- `aplicaIgv`: Si la empresa incluye IGV en sus operaciones
- `porcentajeIgv`: Porcentaje de IGV (18% por defecto)
- `correlativoTicket`: Numeración de tickets internos
- `prefijoTicket`: Prefijo para tickets (ej: "MOZ")

### 2. **Entidad `DatosFacturacion` (Opcional)**
Entidad que contiene información para facturación electrónica SUNAT.

**Ubicación:** `src/main/java/com/djasoft/mozaico/domain/entities/DatosFacturacion.java`

**Características:**
- ✅ **Completamente opcional** - Solo existe si la empresa tiene RUC
- ✅ **Cumple reglamentación SUNAT 2024**
- ✅ **Series y numeración oficial**

**Campos clave:**
- `ruc`: Número RUC (11 dígitos)
- `razonSocial`, `nombreComercial`: Datos legales
- `direccionFiscal`: Domicilio fiscal
- `estadoFormalizacion`: Estado ante SUNAT
- `facturacionElectronicaActiva`: Si puede emitir comprobantes electrónicos
- `serieFactura`, `serieBoleta`: Series de comprobantes
- `oseProveedor`: Proveedor de servicios electrónicos

---

## 🎛️ **Nuevos Enums**

### 1. **`TipoOperacion`**
Define las capacidades de emisión de comprobantes:

```java
TICKET_SIMPLE          // Solo tickets internos sin valor tributario
BOLETA_MANUAL          // Boletas manuales sin facturación electrónica  
FACTURACION_ELECTRONICA // Facturación electrónica completa SUNAT
MIXTO                  // Tickets internos + Comprobantes electrónicos
```

### 2. **`EstadoFormalizacion`**
Estado de la empresa ante SUNAT:

```java
SIN_RUC                // Sin RUC - Negocio informal
CON_RUC_INACTIVO      // Tiene RUC pero sin facturación electrónica
CON_RUC_ACTIVO        // RUC activo con facturación electrónica
EN_TRAMITE            // En proceso de formalización
```

### 3. **`TipoContribuyente`** y **`RegimenTributario`**
Clasificaciones según normativa SUNAT.

---

## 🔧 **Nuevos Servicios**

### 1. **`EmpresaValidacionService`**
Servicio central para validaciones de configuración empresarial.

**Ubicación:** `src/main/java/com/djasoft/mozaico/application/services/EmpresaValidacionService.java`

**Métodos principales:**
- `validarConfiguracionIgv()`: Validación completa de configuración
- `puedeAplicarIgv()`: Verificación rápida de IGV
- `obtenerPorcentajeIgv()`: Obtiene porcentaje configurado

**Funcionalidades:**
- ✅ Valida configuración automáticamente
- ✅ Genera mensajes dinámicos para clientes
- ✅ Determina capacidades de emisión
- ✅ Detecta inconsistencias de configuración

### 2. **`PedidoCalculoService`**
Servicio para cálculos de pedidos con validación automática de IGV.

**Ubicación:** `src/main/java/com/djasoft/mozaico/application/services/PedidoCalculoService.java`

**Características:**
- ✅ Aplica IGV según configuración de empresa
- ✅ Calcula totales dinámicamente
- ✅ Incluye información para el cliente
- ✅ Soporte para simulaciones

---

## 🎮 **Nueva API REST**

### **`EmpresaValidacionController`**
Controlador para endpoints de validación empresarial.

**Base URL:** `/api/empresa/validacion`

**Endpoints disponibles:**

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/igv` | Obtiene validación completa de IGV |
| GET | `/igv/{idEmpresa}` | Validación específica por empresa |
| GET | `/aplica-igv` | Verificación rápida si aplica IGV |
| GET | `/porcentaje-igv` | Obtiene porcentaje de IGV |
| GET | `/mensaje-cliente` | Mensaje para mostrar al cliente |

---

## 📋 **Nuevo DTO**

### **`ValidacionIgvResponseDto`**
DTO completo que incluye toda la información de validación.

**Ubicación:** `src/main/java/com/djasoft/mozaico/application/dtos/empresa/ValidacionIgvResponseDto.java`

**Contiene:**
- Configuración de IGV (aplica/no aplica, porcentaje)
- Capacidades de emisión (facturas, boletas, tickets)
- Mensaje personalizado para el cliente
- Advertencias y limitaciones
- Información de la empresa y RUC

---

## 🗄️ **Nuevos Repositorios**

### 1. **`EmpresaRepository`**
```java
Optional<Empresa> findByActivaTrue()
List<Empresa> findByTipoOperacion(TipoOperacion tipoOperacion)
List<Empresa> findEmpresasConIgv()
Boolean existsByActivaTrue()
```

### 2. **`DatosFacturacionRepository`**
```java
Optional<DatosFacturacion> findByEmpresa(Empresa empresa)
Optional<DatosFacturacion> findByRuc(String ruc)
List<DatosFacturacion> findByFacturacionElectronicaActivaTrue()
Boolean existsByRuc(String ruc)
```

---

## 🔄 **Cambios en Componentes Existentes**

### **DataLoader.java**
**Cambios realizados:**

1. **Nueva configuración de empresa:**
   ```java
   // PASO 0: CONFIGURACIÓN DE EMPRESA - Negocio informal sin RUC
   Empresa empresaMozaico = createEmpresaInformal();
   ```

2. **Cálculo de IGV dinámico:**
   - Consulta automática de configuración empresarial
   - Aplicación de IGV solo si la empresa lo permite
   - Logs informativos sobre aplicación de impuestos

3. **Empresa configurada como informal:**
   - Nombre: "Restaurante Mozaico"
   - Tipo: `TICKET_SIMPLE` (solo tickets internos)
   - IGV: Desactivado (`aplicaIgv = false`)
   - Sin datos de facturación

---

## 🎯 **Casos de Uso Soportados**

### 1. **🎟️ Negocio Informal (Configuración Actual)**
```javascript
{
  "aplicaIgv": false,
  "tipoOperacion": "TICKET_SIMPLE",
  "mensajeCliente": "🎟️ Esta empresa opera como negocio informal. Los comprobantes emitidos son tickets internos sin valor tributario y NO incluyen IGV.",
  "comprobantesPermitidos": ["Ticket interno sin valor tributario"]
}
```

### 2. **📄 Negocio Formal con RUC**
```javascript
{
  "aplicaIgv": true,
  "porcentajeIgv": 18.00,
  "tipoOperacion": "BOLETA_MANUAL",
  "mensajeCliente": "📄 Esta empresa puede emitir boletas de venta manuales. Los precios incluyen IGV (18.0%)."
}
```

### 3. **✅ Facturación Electrónica Completa**
```javascript
{
  "aplicaIgv": true,
  "tipoOperacion": "FACTURACION_ELECTRONICA",
  "mensajeCliente": "✅ Esta empresa puede emitir comprobantes electrónicos válidos ante SUNAT. Los precios incluyen IGV (18.0%). RUC: 20123456789"
}
```

---

## 🔍 **Validaciones Implementadas**

### **Validaciones Automáticas:**
- ✅ **Configuración coherente**: IGV vs RUC vs tipo de operación
- ✅ **Estados consistentes**: Formalización vs capacidades
- ✅ **Datos completos**: Información requerida según tipo de operación
- ✅ **Advertencias**: Notificación de inconsistencias
- ✅ **Limitaciones**: Información clara de restricciones

### **Mensajes Dinámicos para Clientes:**
- 🎟️ Negocio informal: Explica que no hay IGV ni comprobantes válidos
- ⚠️ Configuración incompleta: Advierte sobre limitaciones
- ✅ Configuración completa: Confirma capacidades de facturación

---

## 📱 **Integración Frontend**

### **Verificación de IGV:**
```javascript
// Verificar configuración antes de mostrar precios
const validacion = await fetch('/api/empresa/validacion/igv').then(r => r.json());

// Mostrar mensaje al cliente
mostrarMensaje(validacion.mensajeCliente);

// Configurar cálculos
configurarCalculadora(validacion.aplicaIgv, validacion.porcentajeIgv);
```

### **Mensaje rápido:**
```javascript
const mensaje = await fetch('/api/empresa/validacion/mensaje-cliente').then(r => r.text());
mostrarNotificacion(mensaje);
```

---

## 🛠️ **Migración y Escalabilidad**

### **Migración de Informal a Formal:**
1. Cambiar `aplicaIgv` a `true`
2. Crear registro en `DatosFacturacion`
3. Actualizar `tipoOperacion`
4. Configurar series y numeración

### **Preparado para:**
- ✅ Múltiples empresas (aunque actualmente se usa una)
- ✅ Diferentes regímenes tributarios
- ✅ Integración con OSE (Operadores de Servicios Electrónicos)
- ✅ Facturación electrónica completa SUNAT
- ✅ Migración gradual sin pérdida de datos

---

## 🎊 **Beneficios del Sistema**

### **Para el Negocio:**
- 🚀 **Inicio inmediato**: Puede operar sin RUC desde el día 1
- 📈 **Crecimiento gradual**: Migración sin interrupciones
- ⚖️ **Cumplimiento legal**: Preparado para SUNAT 2024
- 🔧 **Flexibilidad total**: Adaptable a cualquier tipo de negocio

### **Para los Clientes:**
- 💡 **Transparencia**: Saben exactamente qué tipo de comprobante recibirán
- 📋 **Información clara**: Entienden si los precios incluyen IGV
- ✅ **Confianza**: Mensaje claro sobre capacidades de la empresa

### **Para los Desarrolladores:**
- 🎯 **API clara**: Endpoints específicos para validaciones
- 🔍 **Validaciones automáticas**: Detección de problemas de configuración
- 📚 **Documentación completa**: DTOs y servicios bien estructurados
- 🧪 **Fácil testing**: Simulaciones y validaciones independientes

---

## 📝 **Notas de Implementación**

- El sistema mantiene **una sola empresa activa** por instalación
- La configuración actual es **negocio informal sin IGV**
- Todos los cálculos se realizan **dinámicamente** consultando la configuración
- El sistema está **preparado para facturación electrónica** cuando sea necesario
- Las validaciones son **automáticas** y no requieren intervención manual