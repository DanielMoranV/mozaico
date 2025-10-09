# API KDS - Kitchen Display System

## Descripción General

El sistema KDS (Kitchen Display System) permite a la cocina gestionar el estado de preparación de los productos en los pedidos. Esta API proporciona endpoints para visualizar y actualizar el estado de cada producto desde que se ordena hasta que se entrega al cliente.

**IMPORTANTE**: El sistema filtra automáticamente productos de pedidos que ya no están activos. Solo muestra productos de pedidos con estado `ABIERTO` o `ATENDIDO`, excluyendo automáticamente pedidos `PAGADO` o `CANCELADO`.

## Estados de Preparación

### Estados del Detalle de Pedido

Los productos pueden tener los siguientes estados:

| Estado | Descripción | Cuándo se usa |
|--------|-------------|---------------|
| `PEDIDO` | Cliente pidió el producto | Estado inicial cuando se crea el pedido |
| `EN_PREPARACION` | Cocina está preparando | Cuando cocina comienza a preparar el producto |
| `SERVIDO` | Producto entregado al cliente | Cuando el mesero entrega el producto a la mesa |
| `CANCELADO` | Producto cancelado | Cuando se cancela el producto del pedido |

### Estados del Pedido (Filtro Automático)

El sistema solo muestra productos de pedidos activos:

| Estado | Se muestra en KDS | Descripción |
|--------|-------------------|-------------|
| `ABIERTO` | ✅ SÍ | Mesa ocupada, pueden seguir pidiendo productos |
| `ATENDIDO` | ✅ SÍ | Cliente terminó de pedir, esperando finalizar consumo |
| `PAGADO` | ❌ NO | Mesa pagó, pedido completado |
| `CANCELADO` | ❌ NO | Pedido cancelado |

## Flujo de Trabajo

```
PEDIDO → EN_PREPARACION → SERVIDO
   ↓
CANCELADO
```

1. **Cocina** ve los productos en estado `PEDIDO`
2. **Cocina** cambia a `EN_PREPARACION` cuando comienza a preparar
3. **Cocina** cambia a `SERVIDO` cuando el producto está listo
4. **Mesero** ve los productos `SERVIDO` para entregarlos a la mesa

---

## Endpoints

### 1. Obtener Detalles por Estado

Obtiene todos los productos de pedidos filtrados por estado de preparación.

**Endpoint:**
```
GET /api/v1/kds/detalles?estado={ESTADO}&requierePreparacion={true|false}
```

**Parámetros Query:**
- `estado` (string, requerido): Estado del detalle de pedido
  - Valores posibles: `PEDIDO`, `EN_PREPARACION`, `SERVIDO`, `CANCELADO`
- `requierePreparacion` (boolean, opcional, default: `true`): Filtrar solo productos que requieren preparación en cocina
  - `true`: Solo muestra productos con `requierePreparacion = true` (bebidas preparadas, platos cocinados, etc.)
  - `false`: Muestra TODOS los productos sin filtrar

**Filtros Automáticos Aplicados:**
- ✅ Solo pedidos con estado `ABIERTO` o `ATENDIDO`
- ❌ Excluye automáticamente pedidos `PAGADO` o `CANCELADO`
- 📊 Ordenados por fecha de pedido (más antiguos primero)

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Respuesta Exitosa (200 OK):**
```json
{
  "success": true,
  "message": "Detalles KDS obtenidos exitosamente",
  "data": [
    {
      "idDetalle": 1,
      "pedido": {
        "idPedido": 5,
        "mesa": {
          "idMesa": 3,
          "numeroMesa": 5
        },
        "cliente": {
          "idCliente": 2,
          "nombre": "Juan",
          "apellido": "Pérez"
        },
        "empleado": {
          "idUsuario": 1,
          "nombre": "María García",
          "username": "mgarcia"
        },
        "fechaPedido": "2025-10-08T14:30:00",
        "estado": "EN_PROCESO",
        "tipoServicio": "MESA",
        "subtotal": 45.00,
        "impuestos": 8.10,
        "descuento": 0.00,
        "total": 53.10,
        "observaciones": "Sin cebolla"
      },
      "producto": {
        "idProducto": 10,
        "nombre": "Lomo Saltado",
        "precio": 45.00
      },
      "cantidad": 1,
      "precioUnitario": 45.00,
      "subtotal": 45.00,
      "observaciones": "Término medio, sin cebolla",
      "estado": "PEDIDO"
    }
  ]
}
```

**Casos de Uso:**
- **Cocina:** `GET /api/v1/kds/detalles?estado=PEDIDO` - Ver productos pendientes por preparar (solo requieren cocina de pedidos activos)
- **Cocina:** `GET /api/v1/kds/detalles?estado=PEDIDO&requierePreparacion=true` - Explícitamente solo productos que requieren preparación de pedidos activos
- **Cocina:** `GET /api/v1/kds/detalles?estado=EN_PREPARACION` - Ver productos que se están preparando (solo de pedidos activos)
- **Meseros:** `GET /api/v1/kds/detalles?estado=SERVIDO` - Ver productos listos para entregar (solo de pedidos activos)
- **Bar:** `GET /api/v1/kds/detalles?estado=PEDIDO&requierePreparacion=false` - Ver TODOS los productos de pedidos activos (incluye bebidas embotelladas, etc.)

**Nota**: Todos los endpoints filtran automáticamente solo pedidos con estado `ABIERTO` o `ATENDIDO`. Los pedidos `PAGADO` o `CANCELADO` no aparecen en el KDS.

---

### 2. Cambiar Estado de Detalle

Actualiza el estado de preparación de un producto específico.

**Endpoint:**
```
PUT /api/v1/kds/detalles/{id}/estado?estado={NUEVO_ESTADO}
```

**Parámetros:**
- `id` (Integer, path, requerido): ID del detalle de pedido
- `estado` (string, query, requerido): Nuevo estado del detalle
  - Valores posibles: `PEDIDO`, `EN_PREPARACION`, `SERVIDO`, `CANCELADO`

**Headers:**
```
Authorization: Bearer {token}
Content-Type: application/json
```

**Ejemplo de Petición:**
```
PUT /api/v1/kds/detalles/1/estado?estado=EN_PREPARACION
```

**Respuesta Exitosa (200 OK):**
```json
{
  "success": true,
  "message": "Estado del detalle actualizado exitosamente",
  "data": {
    "idDetalle": 1,
    "pedido": {
      "idPedido": 5,
      "mesa": {
        "idMesa": 3,
        "numeroMesa": 5
      },
      "fechaPedido": "2025-10-08T14:30:00",
      "estado": "EN_PROCESO"
    },
    "producto": {
      "idProducto": 10,
      "nombre": "Lomo Saltado",
      "precio": 45.00
    },
    "cantidad": 1,
    "precioUnitario": 45.00,
    "subtotal": 45.00,
    "observaciones": "Término medio, sin cebolla",
    "estado": "EN_PREPARACION"
  }
}
```

**Respuesta de Error (404 Not Found):**
```json
{
  "success": false,
  "message": "Detalle de pedido no encontrado con id: 1",
  "data": null
}
```

**Respuesta de Error (400 Bad Request):**
```json
{
  "success": false,
  "message": "Estado inválido: INVALIDO",
  "data": null
}
```

---

## Tipado TypeScript

### Interfaces

```typescript
// Enums
export enum EstadoDetallePedido {
  PEDIDO = 'PEDIDO',
  EN_PREPARACION = 'EN_PREPARACION',
  SERVIDO = 'SERVIDO',
  CANCELADO = 'CANCELADO'
}

export enum EstadoPedido {
  EN_PROCESO = 'EN_PROCESO',
  COMPLETADO = 'COMPLETADO',
  CANCELADO = 'CANCELADO'
}

export enum TipoServicio {
  MESA = 'MESA',
  DELIVERY = 'DELIVERY',
  PARA_LLEVAR = 'PARA_LLEVAR'
}

// DTOs
export interface ProductoResponseDTO {
  idProducto: number;
  nombre: string;
  precio: number;
}

export interface MesaResponseDTO {
  idMesa: number;
  numeroMesa: number;
}

export interface ClienteResponseDTO {
  idCliente: number;
  nombre: string;
  apellido: string;
}

export interface UsuarioResponseDTO {
  idUsuario: number;
  nombre: string;
  username: string;
}

export interface PedidoResponseDTO {
  idPedido: number;
  cliente?: ClienteResponseDTO;
  mesa?: MesaResponseDTO;
  empleado?: UsuarioResponseDTO;
  fechaPedido: string; // ISO 8601 format
  estado: EstadoPedido;
  tipoServicio: TipoServicio;
  subtotal: number;
  impuestos: number;
  descuento: number;
  total: number;
  observaciones?: string;
  direccionDelivery?: string;
}

export interface DetallePedidoResponseDTO {
  idDetalle: number;
  pedido: PedidoResponseDTO;
  producto: ProductoResponseDTO;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  observaciones?: string;
  estado: EstadoDetallePedido;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
```

### Servicios / Funciones

```typescript
import axios from 'axios';

const API_BASE_URL = '/api/v1/kds';

// Obtener detalles por estado
export async function obtenerDetallesPorEstado(
  estado: EstadoDetallePedido,
  token: string,
  requierePreparacion: boolean = true
): Promise<ApiResponse<DetallePedidoResponseDTO[]>> {
  const response = await axios.get(`${API_BASE_URL}/detalles`, {
    params: {
      estado,
      requierePreparacion
    },
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    }
  });
  return response.data;
}

// Cambiar estado de un detalle
export async function cambiarEstadoDetalle(
  idDetalle: number,
  nuevoEstado: EstadoDetallePedido,
  token: string
): Promise<ApiResponse<DetallePedidoResponseDTO>> {
  const response = await axios.put(
    `${API_BASE_URL}/detalles/${idDetalle}/estado`,
    null,
    {
      params: { estado: nuevoEstado },
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      }
    }
  );
  return response.data;
}
```

---

## Ejemplos de Uso

### Ejemplo 1: Dashboard de Cocina

```typescript
// Obtener solo productos que requieren preparación en cocina (default)
const productosPendientes = await obtenerDetallesPorEstado(
  EstadoDetallePedido.PEDIDO,
  userToken
  // requierePreparacion = true por defecto
);

// Mostrar en la pantalla de cocina
productosPendientes.data.forEach(detalle => {
  console.log(`
    Mesa: ${detalle.pedido.mesa?.numeroMesa}
    Producto: ${detalle.producto.nombre}
    Cantidad: ${detalle.cantidad}
    Observaciones: ${detalle.observaciones || 'Ninguna'}
  `);
});
```

### Ejemplo 1b: Dashboard de Bar (todos los productos)

```typescript
// Obtener TODOS los productos incluidos los que no requieren preparación
const todosLosProductos = await obtenerDetallesPorEstado(
  EstadoDetallePedido.PEDIDO,
  userToken,
  false // Incluye bebidas embotelladas, etc.
);

// El bar puede ver bebidas embotelladas que no requieren preparación
todosLosProductos.data.forEach(detalle => {
  console.log(`
    Mesa: ${detalle.pedido.mesa?.numeroMesa}
    Producto: ${detalle.producto.nombre}
    Requiere Preparación: ${detalle.producto.requierePreparacion ? 'Sí' : 'No'}
  `);
});
```

### Ejemplo 2: Iniciar Preparación

```typescript
// Cuando cocina toma un pedido para preparar
async function iniciarPreparacion(idDetalle: number) {
  try {
    const resultado = await cambiarEstadoDetalle(
      idDetalle,
      EstadoDetallePedido.EN_PREPARACION,
      userToken
    );

    if (resultado.success) {
      console.log('Producto en preparación:', resultado.data.producto.nombre);
      // Actualizar UI
    }
  } catch (error) {
    console.error('Error al cambiar estado:', error);
  }
}
```

### Ejemplo 3: Marcar como Listo

```typescript
// Cuando el producto está listo para servir
async function marcarComoListo(idDetalle: number) {
  try {
    const resultado = await cambiarEstadoDetalle(
      idDetalle,
      EstadoDetallePedido.SERVIDO,
      userToken
    );

    if (resultado.success) {
      // Notificar a meseros que el producto está listo
      notificarMeseros(resultado.data);
    }
  } catch (error) {
    console.error('Error al marcar como listo:', error);
  }
}
```

### Ejemplo 4: Panel de Meseros

```typescript
// Ver productos listos para entregar
const productosListos = await obtenerDetallesPorEstado(
  EstadoDetallePedido.SERVIDO,
  userToken
);

// Agrupar por mesa
const productosPorMesa = productosListos.data.reduce((acc, detalle) => {
  const numeroMesa = detalle.pedido.mesa?.numeroMesa || 0;
  if (!acc[numeroMesa]) {
    acc[numeroMesa] = [];
  }
  acc[numeroMesa].push(detalle);
  return acc;
}, {} as Record<number, DetallePedidoResponseDTO[]>);

// Mostrar notificaciones
Object.entries(productosPorMesa).forEach(([mesa, productos]) => {
  console.log(`Mesa ${mesa}: ${productos.length} producto(s) listo(s)`);
});
```

---

## Notas Importantes

### Permisos
- **Cocina**: Debe tener permiso para cambiar estados a `EN_PREPARACION` y `SERVIDO`
- **Meseros**: Pueden consultar estados pero típicamente no cambian el estado (excepto a `SERVIDO` si lo entregan)
- **Administradores**: Acceso completo a todos los estados

### Buenas Prácticas

1. **Polling vs WebSocket**: Para actualizaciones en tiempo real, considerar implementar WebSocket en lugar de polling
2. **Agrupación**: Agrupar productos del mismo pedido para mejor organización en cocina
3. **Priorización**: ✅ Ya implementado - Los productos se ordenan automáticamente por fecha de pedido (más antiguos primero)
4. **Notificaciones**: Implementar notificaciones push cuando productos cambien a `SERVIDO`
5. **Auditoría**: Considerar registrar quién y cuándo cambió cada estado
6. **Filtro de Pedidos**: ✅ Ya implementado - Solo muestra productos de pedidos activos (ABIERTO/ATENDIDO)

### Optimizaciones Sugeridas

```typescript
// Usar query con múltiples estados
// Futuro: GET /api/v1/kds/detalles?estados=PEDIDO,EN_PREPARACION

// Filtrar por empresa (multi-tenant)
// ✅ Ya implementado en el backend con @CurrentCompany

// Filtrar por estado de pedido
// ✅ Ya implementado - Solo muestra pedidos ABIERTO y ATENDIDO

// Ordenamiento por antigüedad
// ✅ Ya implementado - ORDER BY fechaPedido ASC

// Filtrar por categoría de producto
// Futuro: Útil para separar cocina caliente, fría, bar, etc.
```

### Gestión del Campo `requierePreparacion` en Productos

**IMPORTANTE**: Para que el filtro funcione correctamente, debes asegurarte de configurar el campo `requierePreparacion` en cada producto:

```typescript
// Ejemplos de productos y su configuración:

// Productos que REQUIEREN preparación (requierePreparacion = true):
✅ Lomo Saltado
✅ Ceviche
✅ Pizza
✅ Hamburguesa
✅ Café expreso
✅ Jugos naturales
✅ Cócteles

// Productos que NO requieren preparación (requierePreparacion = false):
❌ Coca Cola (botella)
❌ Agua mineral
❌ Cerveza embotellada
❌ Postres pre-elaborados (tiramisú comprado)
❌ Chips/snacks empaquetados
```

**Configuración en el Producto:**
```json
{
  "nombre": "Lomo Saltado",
  "precio": 45.00,
  "requierePreparacion": true,  // ← Cocina debe verlo
  "tiempoPreparacion": 20
}

{
  "nombre": "Coca Cola 500ml",
  "precio": 5.00,
  "requierePreparacion": false,  // ← Cocina NO debe verlo
  "tiempoPreparacion": 0
}
```

---

## Testing

### Test con cURL

```bash
# Obtener productos pendientes que REQUIEREN preparación (cocina)
curl -X GET "http://localhost:8080/api/v1/kds/detalles?estado=PEDIDO&requierePreparacion=true" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Obtener productos pendientes (por defecto filtra requierePreparacion=true)
curl -X GET "http://localhost:8080/api/v1/kds/detalles?estado=PEDIDO" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Obtener TODOS los productos sin filtrar (bar)
curl -X GET "http://localhost:8080/api/v1/kds/detalles?estado=PEDIDO&requierePreparacion=false" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Cambiar estado a en preparación
curl -X PUT "http://localhost:8080/api/v1/kds/detalles/1/estado?estado=EN_PREPARACION" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Marcar como servido
curl -X PUT "http://localhost:8080/api/v1/kds/detalles/1/estado?estado=SERVIDO" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Test Unitario (Jest)

```typescript
import { obtenerDetallesPorEstado, cambiarEstadoDetalle } from './kdsService';
import { EstadoDetallePedido } from './types';

describe('KDS Service', () => {
  const mockToken = 'test-token';

  it('debe obtener detalles por estado PEDIDO', async () => {
    const result = await obtenerDetallesPorEstado(
      EstadoDetallePedido.PEDIDO,
      mockToken
    );

    expect(result.success).toBe(true);
    expect(Array.isArray(result.data)).toBe(true);
  });

  it('debe cambiar estado a EN_PREPARACION', async () => {
    const result = await cambiarEstadoDetalle(
      1,
      EstadoDetallePedido.EN_PREPARACION,
      mockToken
    );

    expect(result.success).toBe(true);
    expect(result.data.estado).toBe(EstadoDetallePedido.EN_PREPARACION);
  });
});
```

---

## Soporte

Para más información sobre la API completa del sistema, consultar:
- [API Reference](./API_REFERENCE.md)
- [Documentación de Pedidos](./API_REFERENCE.md#pedidos)
- [Autenticación JWT](./AUTENTICACION_JWT_Y_SEGURIDAD.md)
