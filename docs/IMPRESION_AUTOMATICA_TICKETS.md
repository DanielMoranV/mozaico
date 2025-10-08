# Impresión Automática de Tickets

## Descripción
El sistema ahora genera tickets en formato PDF (80mm de ancho, formato térmico) y permite la impresión automática desde el navegador.

## Endpoints Disponibles

### 1. Descargar Ticket PDF
```
GET /api/v1/comprobantes/{id}/ticket
```
- Descarga el ticket en formato PDF para guardarlo o visualizarlo
- Content-Disposition: `attachment`

### 2. Imprimir Ticket Automáticamente
```
GET /api/v1/comprobantes/{id}/ticket/imprimir
```
- Descarga el ticket con headers configurados para impresión automática
- Content-Disposition: `inline`
- Header personalizado: `X-Auto-Print: true`

### 3. Descargar Comprobante Completo (A4)
```
GET /api/v1/comprobantes/{id}/pdf
```
- Descarga el comprobante completo en formato A4

## Implementación en el Frontend

### Opción 1: JavaScript Vanilla

```javascript
/**
 * Imprime automáticamente un ticket
 * @param {number} idComprobante - ID del comprobante
 * @param {string} token - JWT token de autenticación
 */
async function imprimirTicketAutomatico(idComprobante, token) {
    try {
        // Abrir el PDF en una nueva ventana
        const url = `http://localhost:8091/api/v1/comprobantes/${idComprobante}/ticket/imprimir`;

        // Crear iframe oculto para imprimir
        const iframe = document.createElement('iframe');
        iframe.style.display = 'none';
        iframe.src = url;

        // Agregar headers de autenticación
        iframe.onload = function() {
            try {
                // Esperar a que el PDF se cargue completamente
                setTimeout(() => {
                    iframe.contentWindow.print();

                    // Remover el iframe después de imprimir
                    setTimeout(() => {
                        document.body.removeChild(iframe);
                    }, 1000);
                }, 500);
            } catch (error) {
                console.error('Error al imprimir:', error);
            }
        };

        document.body.appendChild(iframe);
    } catch (error) {
        console.error('Error al cargar el ticket:', error);
    }
}

// Uso:
// imprimirTicketAutomatico(1, 'tu-jwt-token');
```

### Opción 2: Con Fetch API y Blob

```javascript
/**
 * Descarga e imprime un ticket usando fetch
 */
async function imprimirTicketConFetch(idComprobante, token) {
    try {
        const response = await fetch(
            `http://localhost:8091/api/v1/comprobantes/${idComprobante}/ticket/imprimir`,
            {
                headers: {
                    'Authorization': `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {
            throw new Error('Error al descargar el ticket');
        }

        const blob = await response.blob();
        const url = URL.createObjectURL(blob);

        // Crear iframe para imprimir
        const iframe = document.createElement('iframe');
        iframe.style.display = 'none';
        iframe.src = url;

        iframe.onload = function() {
            setTimeout(() => {
                iframe.contentWindow.print();
                setTimeout(() => {
                    document.body.removeChild(iframe);
                    URL.revokeObjectURL(url);
                }, 1000);
            }, 500);
        };

        document.body.appendChild(iframe);
    } catch (error) {
        console.error('Error:', error);
    }
}
```

### Opción 3: Vue.js / React

```javascript
// Vue.js
export default {
    methods: {
        async imprimirTicket(idComprobante) {
            const token = this.$store.state.auth.token; // O donde guardes el token

            try {
                const response = await this.$axios.get(
                    `/api/v1/comprobantes/${idComprobante}/ticket/imprimir`,
                    {
                        responseType: 'blob',
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    }
                );

                const blob = new Blob([response.data], { type: 'application/pdf' });
                const url = URL.createObjectURL(blob);

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
            } catch (error) {
                console.error('Error al imprimir ticket:', error);
            }
        }
    }
}
```

### Opción 4: Abrir en nueva ventana (más compatible)

```javascript
/**
 * Abre el ticket en una nueva ventana para imprimir
 * Más compatible con todos los navegadores
 */
function imprimirTicketNuevaVentana(idComprobante, token) {
    const url = `http://localhost:8091/api/v1/comprobantes/${idComprobante}/ticket/imprimir`;

    // Crear una ventana emergente
    const printWindow = window.open(url, '_blank', 'width=800,height=600');

    if (printWindow) {
        printWindow.addEventListener('load', function() {
            setTimeout(() => {
                printWindow.print();
            }, 500);
        });
    } else {
        alert('Por favor, permite las ventanas emergentes para imprimir');
    }
}
```

## Uso en el Flujo de Pago

Cuando se crea un pago completo, puedes imprimir automáticamente:

```javascript
// Ejemplo completo del flujo
async function procesarPago(datosPago) {
    try {
        // 1. Crear el pago
        const response = await fetch('http://localhost:8091/api/v1/pagos/completo', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(datosPago)
        });

        const resultado = await response.json();

        if (resultado.status === 'SUCCESS') {
            const idComprobante = resultado.data.comprobante.idComprobante;

            // 2. Imprimir automáticamente el ticket
            await imprimirTicketAutomatico(idComprobante, token);

            // 3. Mostrar mensaje de éxito
            alert('Pago procesado y ticket enviado a impresión');
        }
    } catch (error) {
        console.error('Error al procesar pago:', error);
    }
}
```

## Configuración de Impresora

### Para impresoras térmicas (80mm):
1. Configurar la impresora térmica como predeterminada en el sistema
2. El PDF está optimizado para 80mm de ancho
3. En las opciones de impresión del navegador, seleccionar:
   - Tamaño: Personalizado (80mm x variable)
   - Márgenes: Ninguno
   - Escala: 100%

### Para impresoras normales:
El ticket se puede imprimir en impresoras normales, pero se verá pequeño. Se recomienda:
- Usar el comprobante A4 completo para impresoras normales
- Usar el ticket solo para impresoras térmicas

## Notas Importantes

1. **Seguridad**: El endpoint requiere autenticación JWT
2. **CORS**: Asegúrate de tener configurado CORS correctamente si el frontend está en un dominio diferente
3. **Compatibilidad**: La impresión automática puede bloquearse por navegadores. Considera:
   - Usar la opción de nueva ventana en producción
   - Informar al usuario sobre bloqueo de ventanas emergentes
4. **Formato**: El ticket está optimizado para 80mm (226.77 puntos)
5. **Navegadores**: Funciona mejor en Chrome, Firefox y Edge modernos

## Ejemplo HTML Completo

Ver archivo: `ejemplo-impresion-ticket.html` para una implementación completa standalone.
