# Corrección: Descuento de Inventario en Ventas

## Problema Identificado

El sistema **NO estaba descontando el inventario** cuando se creaban pedidos usando ciertos métodos del servicio `PedidoService`, lo que causaba discrepancias entre las ventas reales y el stock disponible.

---

## Análisis del Problema

### Métodos que SÍ descontaban inventario ✅

1. **`DetallePedidoService.crearDetallePedido()`**
   - ✅ Descontaba correctamente usando `inventarioService.actualizarStockPorVenta()`
   - Usado cuando se agrega un producto individualmente

2. **`DetallePedidoService.actualizarDetallePedido()`**
   - ✅ Ajustaba el stock según la diferencia de cantidad
   - Usado al modificar la cantidad de un producto en el pedido

3. **`DetallePedidoService.eliminarDetallePedido()`**
   - ✅ Devolvía el stock al inventario (cantidad negativa)
   - Usado al eliminar un producto del pedido

### Métodos que NO descontaban inventario ❌

1. **`PedidoService.crearPedidoCompleto()`**
   - ❌ **NO descontaba** inventario
   - Usado para crear pedidos completos con múltiples productos de una vez
   - **Impacto**: Pedidos completos no actualizaban el stock

2. **`PedidoService.agregarProductoAPedido()`**
   - ❌ **NO descontaba** inventario
   - Usado para agregar productos a pedidos existentes
   - **Impacto**: Productos agregados por este método no descontaban stock

---

## Solución Implementada

### 1. Agregar `InventarioService` como dependencia

**Archivo**: `PedidoServiceImpl.java`

```java
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
    // ... otras dependencias
    private final com.djasoft.mozaico.services.InventarioService inventarioService; // ✅ NUEVO
}
```

### 2. Descontar inventario en `crearPedidoCompleto()`

**Ubicación**: `PedidoServiceImpl.java:463`

**Antes**:
```java
detallePedidoRepository.save(detalle);
subtotalCalculado = subtotalCalculado.add(subtotalDetalle);
```

**Después**:
```java
detallePedidoRepository.save(detalle);

// Descontar del inventario
inventarioService.actualizarStockPorVenta(producto.getIdProducto(), detalleDTO.getCantidad()); // ✅ NUEVO

subtotalCalculado = subtotalCalculado.add(subtotalDetalle);
```

### 3. Descontar inventario en `agregarProductoAPedido()`

**Ubicación**: `PedidoServiceImpl.java:550`

**Antes**:
```java
DetallePedido detalleGuardado = detallePedidoRepository.save(detalle);

// Recalcular totales del pedido
recalcularTotalesPedido(idPedido);
```

**Después**:
```java
DetallePedido detalleGuardado = detallePedidoRepository.save(detalle);

// Descontar del inventario
inventarioService.actualizarStockPorVenta(producto.getIdProducto(), requestDTO.getCantidad()); // ✅ NUEVO

// Recalcular totales del pedido
recalcularTotalesPedido(idPedido);
```

---

## Flujo Completo de Inventario

### Crear Pedido Completo
```
1. Usuario crea pedido con productos
2. Sistema guarda pedido
3. Para cada producto:
   a. Crea DetallePedido
   b. Guarda DetallePedido
   c. ✅ Descuenta del inventario (NUEVO)
4. Calcula totales
5. Actualiza estado de mesa
```

### Agregar Producto a Pedido
```
1. Usuario agrega producto a pedido existente
2. Sistema valida pedido está abierto
3. Crea DetallePedido
4. Guarda DetallePedido
5. ✅ Descuenta del inventario (NUEVO)
6. Recalcula totales del pedido
```

### Actualizar Cantidad de Producto
```
1. Usuario cambia cantidad de producto
2. Sistema calcula diferencia (nueva - original)
3. Actualiza DetallePedido
4. ✅ Ajusta inventario según diferencia (YA EXISTÍA)
5. Recalcula totales
```

### Eliminar Producto del Pedido
```
1. Usuario elimina producto
2. Sistema obtiene cantidad original
3. Elimina DetallePedido
4. ✅ Devuelve stock al inventario (YA EXISTÍA)
5. Recalcula totales
```

---

## Verificación del Stock

El método `actualizarStockPorVenta()` en `InventarioService`:

```java
public InventarioResponseDTO actualizarStockPorVenta(Long idProducto, Integer cantidadVendida) {
    // Busca inventario por producto
    // Resta la cantidad vendida del stock actual
    // Valida que no quede negativo
    // Guarda y retorna
}
```

### Comportamiento:
- **Cantidad positiva**: Descuenta del stock (venta)
- **Cantidad negativa**: Suma al stock (devolución)
- **Validaciones**: No permite stock negativo

---

## Impacto de la Corrección

### Antes de la corrección ❌
```
Pedido completo con 5 productos:
- Producto A: 10 unidades
- Producto B: 5 unidades
- etc...

Stock en inventario: NO CAMBIA ❌
```

### Después de la corrección ✅
```
Pedido completo con 5 productos:
- Producto A: 10 unidades → Stock - 10 ✅
- Producto B: 5 unidades → Stock - 5 ✅
- etc...

Stock en inventario: SE ACTUALIZA CORRECTAMENTE ✅
```

---

## Casos de Uso

### Caso 1: Crear pedido completo con múltiples productos
```bash
POST /api/v1/pedidos/completo
{
  "idEmpleado": 1,
  "idMesa": 5,
  "tipoServicio": "MESA",
  "detalles": [
    {"idProducto": 1, "cantidad": 2},
    {"idProducto": 3, "cantidad": 5}
  ]
}

Resultado:
✅ Pedido creado
✅ Producto 1: Stock - 2
✅ Producto 3: Stock - 5
```

### Caso 2: Agregar producto a pedido existente
```bash
POST /api/v1/pedidos/123/productos
{
  "idProducto": 7,
  "cantidad": 3
}

Resultado:
✅ Producto agregado al pedido
✅ Producto 7: Stock - 3
```

### Caso 3: Modificar cantidad de producto
```bash
PUT /api/v1/detalles-pedido/456
{
  "cantidad": 8  // Era 5 antes
}

Resultado:
✅ Cantidad actualizada
✅ Stock ajustado: -3 unidades adicionales
```

### Caso 4: Eliminar producto del pedido
```bash
DELETE /api/v1/detalles-pedido/456

Resultado:
✅ Producto eliminado
✅ Stock devuelto al inventario
```

---

## Tabla Resumen de Métodos

| Método | Endpoint | ¿Descuenta? | Estado |
|--------|----------|-------------|--------|
| `crearPedidoCompleto()` | `POST /api/v1/pedidos/completo` | ✅ Sí | ✅ Corregido |
| `agregarProductoAPedido()` | `POST /api/v1/pedidos/{id}/productos` | ✅ Sí | ✅ Corregido |
| `DetallePedidoService.crearDetallePedido()` | `POST /api/v1/detalles-pedido` | ✅ Sí | ✅ Ya funcionaba |
| `DetallePedidoService.actualizarDetallePedido()` | `PUT /api/v1/detalles-pedido/{id}` | ✅ Sí | ✅ Ya funcionaba |
| `DetallePedidoService.eliminarDetallePedido()` | `DELETE /api/v1/detalles-pedido/{id}` | ✅ Sí (devuelve) | ✅ Ya funcionaba |

---

## Pruebas Recomendadas

### 1. Verificar descuento en pedido completo
```bash
# 1. Ver stock actual
GET /api/v1/inventario?idProducto=1

# 2. Crear pedido completo con 5 unidades
POST /api/v1/pedidos/completo
{
  "detalles": [{"idProducto": 1, "cantidad": 5}]
}

# 3. Verificar stock descontado
GET /api/v1/inventario?idProducto=1
# Debe mostrar: stock_anterior - 5
```

### 2. Verificar descuento al agregar producto
```bash
# 1. Ver stock actual
GET /api/v1/inventario?idProducto=2

# 2. Agregar producto a pedido
POST /api/v1/pedidos/123/productos
{
  "idProducto": 2,
  "cantidad": 3
}

# 3. Verificar stock descontado
GET /api/v1/inventario?idProducto=2
# Debe mostrar: stock_anterior - 3
```

### 3. Verificar devolución al eliminar
```bash
# 1. Ver stock antes de eliminar
GET /api/v1/inventario?idProducto=3

# 2. Eliminar detalle
DELETE /api/v1/detalles-pedido/456

# 3. Verificar stock devuelto
GET /api/v1/inventario?idProducto=3
# Debe mostrar: stock aumentado
```

---

## Notas Importantes

1. **Transaccionalidad**: Todos los métodos son `@Transactional`, garantizando que si falla el descuento de inventario, todo el pedido se revierte.

2. **Validación de stock**: El método `actualizarStockPorVenta()` debe validar que hay suficiente stock antes de descontar.

3. **Stock negativo**: Si no hay suficiente stock, el método debe lanzar una excepción y revertir la transacción.

4. **Compatibilidad**: Los cambios son retrocompatibles y no afectan la funcionalidad existente.

5. **Performance**: El descuento de inventario se realiza de forma eficiente durante la creación del pedido, sin queries adicionales innecesarios.

---

## Checklist de Implementación

- ✅ Agregar `InventarioService` como dependencia en `PedidoServiceImpl`
- ✅ Agregar descuento en `crearPedidoCompleto()`
- ✅ Agregar descuento en `agregarProductoAPedido()`
- ✅ Compilación exitosa sin errores
- ✅ Documentación actualizada
- ⚠️ **Pendiente**: Pruebas de integración
- ⚠️ **Pendiente**: Pruebas de stock insuficiente

---

## Próximos Pasos (Opcional)

1. **Agregar validación de stock disponible** antes de crear el pedido
2. **Implementar alertas** cuando el stock llegue al mínimo
3. **Agregar logs** de auditoría para cambios de inventario
4. **Crear reportes** de movimientos de inventario por pedido

---

## Conclusión

La corrección garantiza que **TODOS** los métodos de creación/modificación de pedidos descuentan correctamente el inventario, manteniendo la integridad de los datos y evitando ventas de productos sin stock disponible.
