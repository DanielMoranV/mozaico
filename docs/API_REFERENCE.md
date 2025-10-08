# 📚 API Reference - Sistema de Empresa Mozaico

## 🎯 Base URL
```
http://localhost:8080/api/v1/empresa/validacion
```

---

## 🔍 **Endpoints de Validación**

### **1. Validación Completa de IGV**

#### `GET /igv`
Obtiene la validación completa de la configuración empresarial y capacidades de emisión.

**Headers:**
```http
Content-Type: application/json
Accept: application/json
```

**Response 200 OK:**
```json
{
  "aplicaIgv": false,
  "porcentajeIgv": 18.00,
  "moneda": "PEN",
  "tipoOperacion": "TICKET_SIMPLE",
  "puedeEmitirFacturas": false,
  "puedeEmitirBoletas": false,
  "puedeEmitirTickets": true,
  "facturacionElectronicaActiva": false,
  "mensajeCliente": "🎟️ Esta empresa opera como negocio informal. Los comprobantes emitidos son tickets internos sin valor tributario y NO incluyen IGV.",
  "tipoComprobanteDisponible": "Ticket interno",
  "comprobantesPermitidos": [
    "Ticket interno sin valor tributario"
  ],
  "nombreEmpresa": "Restaurante Mozaico",
  "ruc": null,
  "tieneRuc": false,
  "incluyeIgvEnPrecio": false,
  "formatoNumeracion": "MOZ-########",
  "prefijoComprobante": "MOZ",
  "empresaActiva": true,
  "configuracionValida": true,
  "advertencias": [],
  "limitaciones": [
    "Solo puede emitir tickets internos sin valor tributario",
    "No puede emitir comprobantes válidos ante SUNAT",
    "No puede incluir IGV en los comprobantes"
  ]
}
```

**Response 404 Not Found:**
```json
{
  "error": "No se encontró empresa activa configurada",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Ejemplo de uso:**
```javascript
const validacion = await fetch('/api/v1/empresa/validacion/igv')
  .then(response => response.json());

console.log('Aplica IGV:', validacion.aplicaIgv);
console.log('Mensaje para cliente:', validacion.mensajeCliente);
```

---

### **2. Validación por Empresa Específica**

#### `GET /igv/{idEmpresa}`
Obtiene la validación de una empresa específica por ID.

**Parameters:**
- `idEmpresa` (path) - ID de la empresa a validar

**Response 200 OK:**
```json
{
  // Mismo formato que /igv
}
```

**Response 404 Not Found:**
```json
{
  "error": "Empresa no encontrada con ID: 123",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Ejemplo:**
```javascript
const validacion = await fetch('/api/v1/empresa/validacion/igv/1')
  .then(response => response.json());
```

---

### **3. Verificación Rápida de IGV**

#### `GET /aplica-igv`
Verificación rápida si la empresa aplica IGV.

**Response 200 OK:**
```json
false
```

**Ejemplo:**
```javascript
const aplicaIgv = await fetch('/api/v1/empresa/validacion/aplica-igv')
  .then(response => response.json());

if (aplicaIgv) {
  mostrarCalculadoraConIgv();
} else {
  mostrarCalculadoraSinIgv();
}
```

---

### **4. Porcentaje de IGV**

#### `GET /porcentaje-igv`
Obtiene el porcentaje de IGV configurado en la empresa.

**Response 200 OK:**
```json
18.00
```

**Nota:** Retorna `0.00` si la empresa no aplica IGV.

**Ejemplo:**
```javascript
const porcentajeIgv = await fetch('/api/v1/empresa/validacion/porcentaje-igv')
  .then(response => response.json());

console.log(`IGV configurado: ${porcentajeIgv}%`);
```

---

### **5. Mensaje para Cliente**

#### `GET /mensaje-cliente`
Obtiene el mensaje personalizado para mostrar al cliente sobre las capacidades de facturación.

**Response 200 OK:**
```text
🎟️ Esta empresa opera como negocio informal. Los comprobantes emitidos son tickets internos sin valor tributario y NO incluyen IGV.
```

**Response 200 OK (Empresa formal):**
```text
✅ Esta empresa puede emitir comprobantes electrónicos válidos ante SUNAT. Los precios incluyen IGV (18.0%). RUC: 20123456789
```

**Ejemplo:**
```javascript
const mensaje = await fetch('/api/v1/empresa/validacion/mensaje-cliente')
  .then(response => response.text());

document.getElementById('info-empresa').innerHTML = mensaje;
```

---

## 📋 **Modelos de Datos**

### **ValidacionIgvResponseDto**
```typescript
interface ValidacionIgvResponseDto {
  // Configuración de IGV
  aplicaIgv: boolean;
  porcentajeIgv: number;
  moneda: string;
  
  // Capacidades de emisión
  tipoOperacion: 'TICKET_SIMPLE' | 'BOLETA_MANUAL' | 'FACTURACION_ELECTRONICA' | 'MIXTO';
  puedeEmitirFacturas: boolean;
  puedeEmitirBoletas: boolean;
  puedeEmitirTickets: boolean;
  facturacionElectronicaActiva: boolean;
  
  // Información para cliente
  mensajeCliente: string;
  tipoComprobanteDisponible: string;
  comprobantesPermitidos: string[];
  
  // Datos de empresa
  nombreEmpresa: string;
  ruc: string | null;
  tieneRuc: boolean;
  
  // Configuración de cálculos
  incluyeIgvEnPrecio: boolean;
  formatoNumeracion: string;
  prefijoComprobante: string;
  
  // Estado y validaciones
  empresaActiva: boolean;
  configuracionValida: boolean;
  advertencias: string[];
  limitaciones: string[];
}
```

### **TipoOperacion Enum**
```typescript
type TipoOperacion = 
  | 'TICKET_SIMPLE'          // Solo tickets internos
  | 'BOLETA_MANUAL'          // Boletas manuales 
  | 'FACTURACION_ELECTRONICA' // Comprobantes electrónicos
  | 'MIXTO';                 // Combinado
```

---

## 🔧 **Ejemplos de Integración**

### **React/Next.js**
```jsx
import { useState, useEffect } from 'react';

function EmpresaInfo() {
  const [validacion, setValidacion] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function cargarValidacion() {
      try {
        const response = await fetch('/api/v1/empresa/validacion/igv');
        const data = await response.json();
        setValidacion(data);
      } catch (error) {
        console.error('Error al cargar validación:', error);
      } finally {
        setLoading(false);
      }
    }

    cargarValidacion();
  }, []);

  if (loading) return <div>Cargando configuración...</div>;

  return (
    <div className="empresa-info">
      <h3>{validacion.nombreEmpresa}</h3>
      <div className="alert alert-info">
        {validacion.mensajeCliente}
      </div>
      
      <div className="capacidades">
        <h4>Comprobantes Disponibles:</h4>
        <ul>
          {validacion.comprobantesPermitidos.map((comprobante, index) => (
            <li key={index}>{comprobante}</li>
          ))}
        </ul>
      </div>

      {validacion.limitaciones.length > 0 && (
        <div className="limitaciones">
          <h4>Limitaciones:</h4>
          <ul>
            {validacion.limitaciones.map((limitacion, index) => (
              <li key={index}>{limitacion}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
```

### **Vanilla JavaScript**
```javascript
class EmpresaService {
  constructor(baseUrl = '/api/v1/empresa/validacion') {
    this.baseUrl = baseUrl;
  }

  async getValidacionCompleta() {
    const response = await fetch(`${this.baseUrl}/igv`);
    if (!response.ok) {
      throw new Error('Error al obtener validación de empresa');
    }
    return response.json();
  }

  async verificarAplicaIgv() {
    const response = await fetch(`${this.baseUrl}/aplica-igv`);
    return response.json();
  }

  async getMensajeCliente() {
    const response = await fetch(`${this.baseUrl}/mensaje-cliente`);
    return response.text();
  }

  async getPorcentajeIgv() {
    const response = await fetch(`${this.baseUrl}/porcentaje-igv`);
    return response.json();
  }
}

// Uso
const empresaService = new EmpresaService();

// Inicializar aplicación con configuración de empresa
empresaService.getValidacionCompleta()
  .then(validacion => {
    console.log('Configuración de empresa:', validacion);
    
    // Configurar calculadora de precios
    PriceCalculator.configure({
      aplicaIgv: validacion.aplicaIgv,
      porcentajeIgv: validacion.porcentajeIgv
    });
    
    // Mostrar información al usuario
    showEmpresaInfo(validacion);
  })
  .catch(error => {
    console.error('Error:', error);
    showErrorMessage('No se pudo cargar la configuración de la empresa');
  });
```

### **jQuery**
```javascript
$(document).ready(function() {
  // Cargar configuración de empresa al iniciar
  $.get('/api/v1/empresa/validacion/igv')
    .done(function(validacion) {
      // Mostrar información de empresa
      $('#empresa-nombre').text(validacion.nombreEmpresa);
      $('#empresa-mensaje').html(validacion.mensajeCliente);
      
      // Configurar interfaz según capacidades
      $('#btn-facturas').prop('disabled', !validacion.puedeEmitirFacturas);
      $('#btn-boletas').prop('disabled', !validacion.puedeEmitirBoletas);
      
      // Configurar calculadora
      window.aplicaIgv = validacion.aplicaIgv;
      window.porcentajeIgv = validacion.porcentajeIgv;
      
      // Mostrar/ocultar campos de IGV
      if (validacion.aplicaIgv) {
        $('.igv-fields').show();
      } else {
        $('.igv-fields').hide();
      }
    })
    .fail(function() {
      alert('Error al cargar configuración de empresa');
    });
});
```

---

## 🧮 **Calculadora de Precios**

### **Implementación Frontend**
```javascript
class PriceCalculator {
  constructor() {
    this.aplicaIgv = false;
    this.porcentajeIgv = 18.00;
    this.initialized = false;
  }

  async initialize() {
    try {
      const validacion = await fetch('/api/v1/empresa/validacion/igv')
        .then(r => r.json());
      
      this.aplicaIgv = validacion.aplicaIgv;
      this.porcentajeIgv = validacion.porcentajeIgv;
      this.initialized = true;
      
      console.log(`Calculadora inicializada: IGV ${this.aplicaIgv ? 'ACTIVADO' : 'DESACTIVADO'}`);
    } catch (error) {
      console.error('Error al inicializar calculadora:', error);
      // Usar valores por defecto
      this.initialized = true;
    }
  }

  calculate(subtotal) {
    if (!this.initialized) {
      throw new Error('Calculadora no inicializada. Llame a initialize() primero.');
    }

    const subtotalNum = parseFloat(subtotal) || 0;
    let igv = 0;

    if (this.aplicaIgv) {
      igv = subtotalNum * (this.porcentajeIgv / 100);
    }

    const total = subtotalNum + igv;

    return {
      subtotal: subtotalNum.toFixed(2),
      igv: igv.toFixed(2),
      total: total.toFixed(2),
      aplicaIgv: this.aplicaIgv,
      porcentajeIgv: this.porcentajeIgv
    };
  }

  formatPrice(amount) {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN'
    }).format(amount);
  }
}

// Uso global
const calculator = new PriceCalculator();

// Inicializar al cargar la página
calculator.initialize().then(() => {
  console.log('Calculadora lista para usar');
  
  // Ejemplo de cálculo
  const resultado = calculator.calculate(100.00);
  console.log('Cálculo de ejemplo:', resultado);
});
```

---

## ⚠️ **Manejo de Errores**

### **Códigos de Error Comunes**
| Código | Descripción | Solución |
|--------|-------------|----------|
| 404 | Empresa no encontrada | Verificar que existe una empresa activa |
| 500 | Error interno del servidor | Revisar logs del servidor |
| 400 | Parámetros inválidos | Validar parámetros de entrada |

### **Ejemplo de Manejo**
```javascript
async function obtenerValidacion() {
  try {
    const response = await fetch('/api/v1/empresa/validacion/igv');
    
    if (!response.ok) {
      if (response.status === 404) {
        throw new Error('No hay empresa configurada');
      }
      throw new Error(`Error del servidor: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Error al obtener validación:', error);
    
    // Mostrar mensaje amigable al usuario
    showNotification('No se pudo cargar la configuración de la empresa', 'error');
    
    // Retornar configuración por defecto
    return {
      aplicaIgv: false,
      mensajeCliente: 'Configuración no disponible',
      puedeEmitirTickets: true
    };
  }
}
```

---

## 🔒 **Consideraciones de Seguridad**

### **Headers Recomendados**
```http
Content-Type: application/json
Accept: application/json
X-Requested-With: XMLHttpRequest
```

### **CORS Configuration**
El controlador está configurado con:
```java
@CrossOrigin(origins = "*")
```

Para producción, especificar dominios específicos:
```java
@CrossOrigin(origins = {"https://midominio.com", "https://app.midominio.com"})
```

---

## 📊 **Rate Limiting**

### **Límites Recomendados**
- Validación completa: 100 requests/minuto por IP
- Verificaciones rápidas: 300 requests/minuto por IP
- Mensaje cliente: 200 requests/minuto por IP

### **Implementación con Spring Boot**
```java
@RateLimiter(name = "empresa-validacion", fallbackMethod = "fallbackValidacion")
@GetMapping("/igv")
public ResponseEntity<ValidacionIgvResponseDto> obtenerValidacionIgv() {
    // Implementación
}
```

---

## 🧪 **Testing de API**

### **Postman Collection**
```json
{
  "info": {
    "name": "Empresa Validación API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Validación Completa",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/api/v1/empresa/validacion/igv",
        "header": [
          {
            "key": "Accept",
            "value": "application/json"
          }
        ]
      }
    },
    {
      "name": "Verificar IGV",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/api/v1/empresa/validacion/aplica-igv"
      }
    }
  ],
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080"
    }
  ]
}
```

### **cURL Examples**
```bash
# Validación completa
curl -X GET "http://localhost:8080/api/v1/empresa/validacion/igv" \
     -H "Accept: application/json"

# Verificar IGV
curl -X GET "http://localhost:8080/api/v1/empresa/validacion/aplica-igv"

# Mensaje para cliente
curl -X GET "http://localhost:8080/api/v1/empresa/validacion/mensaje-cliente"
```