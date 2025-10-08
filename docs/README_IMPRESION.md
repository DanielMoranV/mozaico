# 🖨️ Guía Rápida de Impresión de Tickets

## Inicio Rápido

### 1. Probar el Sistema

**Opción A: Página de Ejemplo (Recomendado)**
```bash
# Abrir el archivo HTML en tu navegador
firefox docs/ejemplo-impresion-ticket.html
# o
google-chrome docs/ejemplo-impresion-ticket.html
```

**Opción B: Usar cURL**
```bash
# 1. Obtener token
TOKEN=$(curl -s -X POST http://localhost:8091/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"dmoran","password":"123456"}' | \
  jq -r '.data.accessToken')

# 2. Descargar ticket
curl -X GET "http://localhost:8091/api/v1/comprobantes/1/ticket" \
  -H "Authorization: Bearer $TOKEN" \
  -o ticket.pdf

# 3. Abrir PDF
xdg-open ticket.pdf
```

### 2. Crear un Nuevo Comprobante para Probar

```bash
# 1. Login
TOKEN=$(curl -s -X POST http://localhost:8091/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"dmoran","password":"123456"}' | \
  jq -r '.data.accessToken')

# 2. Crear pago completo (genera comprobante automáticamente)
curl -X POST "http://localhost:8091/api/v1/pagos/completo" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idPedido": 1,
    "idMetodoPago": 1,
    "monto": 50.00,
    "referencia": "Test de impresión"
  }'
```

## Endpoints Disponibles

| Endpoint | Descripción | Uso |
|----------|-------------|-----|
| `GET /api/v1/comprobantes/{id}/ticket` | Descarga ticket PDF | Para guardar/visualizar |
| `GET /api/v1/comprobantes/{id}/ticket/imprimir` | Ticket con auto-impresión | Para imprimir directamente |
| `GET /api/v1/comprobantes/{id}/pdf` | Comprobante A4 completo | Para impresoras normales |

## Integración en tu Frontend

### Vue.js

```javascript
// En tu componente de pago
methods: {
  async procesarPago() {
    // 1. Crear el pago
    const response = await this.$axios.post('/api/v1/pagos/completo', this.datosPago);
    const idComprobante = response.data.data.comprobante.idComprobante;

    // 2. Imprimir automáticamente
    await this.imprimirTicket(idComprobante);
  },

  async imprimirTicket(idComprobante) {
    const response = await this.$axios.get(
      `/api/v1/comprobantes/${idComprobante}/ticket/imprimir`,
      { responseType: 'blob' }
    );

    const url = URL.createObjectURL(response.data);
    const iframe = document.createElement('iframe');
    iframe.style.display = 'none';
    iframe.src = url;

    iframe.onload = () => {
      setTimeout(() => {
        iframe.contentWindow.print();
        setTimeout(() => {
          document.body.removeChild(iframe);
          URL.revokeObjectURL(url);
        }, 1000);
      }, 500);
    };

    document.body.appendChild(iframe);
  }
}
```

### React

```javascript
import { useState } from 'react';

function PagoComponent() {
  const imprimirTicket = async (idComprobante) => {
    try {
      const response = await fetch(
        `http://localhost:8091/api/v1/comprobantes/${idComprobante}/ticket/imprimir`,
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
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
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <button onClick={() => imprimirTicket(1)}>
      Imprimir Ticket
    </button>
  );
}
```

## Configuración de Impresora Térmica

### Windows
1. Panel de Control → Dispositivos e impresoras
2. Click derecho en la impresora térmica → "Establecer como predeterminada"
3. Click derecho → Preferencias de impresión
4. Configurar:
   - Tamaño de papel: Personalizado (80mm x 297mm)
   - Márgenes: 0mm
   - Orientación: Vertical

### Linux (CUPS)
```bash
# Instalar drivers de impresora térmica
sudo apt-get install printer-driver-escpr

# Configurar con CUPS
# Abrir http://localhost:631
# Administration → Add Printer
# Configurar tamaño: 80mm x variable
```

### macOS
1. Preferencias del Sistema → Impresoras y escáneres
2. Agregar impresora térmica
3. En opciones de impresión:
   - Tamaño de papel: Personalizado (80mm)
   - Sin márgenes

## Solución de Problemas

### El PDF no se imprime automáticamente
**Causa:** Bloqueador de ventanas emergentes

**Solución:**
1. Permitir ventanas emergentes para tu sitio
2. Usar la opción de "ventana nueva" en lugar de iframe
3. Verificar permisos del navegador

### El ticket se ve muy pequeño
**Causa:** Impresora no configurada para 80mm

**Solución:**
1. Usar una impresora térmica de 80mm
2. O usar el comprobante A4 completo: `/api/v1/comprobantes/{id}/pdf`

### Error 401 Unauthorized
**Causa:** Token JWT expirado o inválido

**Solución:**
```javascript
// Renovar token antes de imprimir
if (tokenExpirado()) {
  await renovarToken();
}
await imprimirTicket(id);
```

### No se encuentra el comprobante (404)
**Causa:** El comprobante aún no se ha generado

**Solución:**
```javascript
// Esperar a que se genere el comprobante
await new Promise(resolve => setTimeout(resolve, 1000));
await imprimirTicket(id);
```

## Documentación Completa

- **Guía detallada:** `docs/IMPRESION_AUTOMATICA_TICKETS.md`
- **CHANGELOG:** `CHANGELOG.md` (Versión 2.1.0)
- **Ejemplo interactivo:** `docs/ejemplo-impresion-ticket.html`

## Soporte

Para problemas o preguntas:
1. Revisar la documentación completa
2. Verificar los logs del backend
3. Probar con el ejemplo HTML incluido
