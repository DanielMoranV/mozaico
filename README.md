# Mozaico - Sistema de Gestión de Restaurante

Este proyecto es un sistema de gestión de restaurante que abarca diversas funcionalidades para la administración eficiente de las operaciones diarias.

## 🚀 Funcionalidades Implementadas

Hasta la fecha, se han implementado las siguientes funcionalidades, incluyendo sus respectivos módulos CRUD (Crear, Leer, Actualizar, Eliminar), búsqueda avanzada, y lógica de negocio integrada:

### **Módulos CRUD Completos con Búsqueda Avanzada:**

*   **Categorías:** Gestión de categorías de productos.
*   **Clientes:** Gestión de la información de los clientes.
*   **Mesas:** Gestión de las mesas del restaurante.
*   **Productos:** Gestión de los productos ofrecidos (con subida de imágenes).
*   **Métodos de Pago:** Gestión de las diferentes formas de pago aceptadas.
*   **Proveedores:** Gestión de la información de los proveedores.
*   **Inventario:** Gestión del stock de productos.
*   **Pedidos:** Gestión de los pedidos de los clientes.
*   **Detalles de Pedido:** Gestión de los ítems individuales dentro de un pedido.
*   **Pagos:** Gestión de los pagos asociados a los pedidos.
*   **Compras/Suministros:** Gestión de las compras realizadas a proveedores.
*   **Detalles de Compras:** Gestión de los ítems individuales dentro de una compra.
*   **Reservas:** Gestión de las reservas de mesas.

### **Lógica de Negocio Integrada:**

*   **Flujo de Pedidos Integrado:**
    *   **Creación de Pedidos:** Vinculación de clientes, mesas y usuarios (empleados) al crear un pedido.
    *   **Cálculo de Totales:** Recálculo automático de `subtotal`, `impuestos`, `descuento` y `total` del pedido cada vez que se modifican sus `DetallePedidos`.
    *   **Actualización de Inventario por Venta:** El `stock_actual` en `Inventario` se ajusta automáticamente al añadir, modificar o eliminar `DetallePedidos`.
    *   **Actualización de Estado de Mesa:** El estado de la `Mesa` cambia a `OCUPADA` cuando se crea un pedido para ella (si es de tipo MESA) y vuelve a `DISPONIBLE` cuando el pedido se marca como `ENTREGADO` o `CANCELADO`.
*   **Integración de Pagos:**
    *   **Actualización de Estado de Pedido:** Cuando un `Pago` se marca como `COMPLETADO`, el estado del `Pedido` asociado se actualiza automáticamente a `ENTREGADO`.
*   **Gestión de Inventario Avanzada:**
    *   **Actualización de Stock por Compra:** El `stock_actual` en `Inventario` se incrementa automáticamente al añadir, modificar o eliminar `DetalleCompras`.
    *   **Alertas de Stock Bajo:** Lógica básica implementada para mostrar alertas por consola cuando el stock de un producto cae por debajo de su mínimo.
*   **Gestión de Compras Integrada:**
    *   **Actualización de Inventario por Recepción:** Cuando el estado de una `Compra` cambia a `RECIBIDA`, el `stock_actual` en `Inventario` se actualiza automáticamente con los productos de sus `DetalleCompras`.
*   **Gestión de Reservas:**
    *   **Validación de Disponibilidad:** Lógica para verificar la disponibilidad de una mesa al crear o actualizar una reserva, evitando solapamientos.

## 🚧 Funcionalidades Pendientes y Posibles Mejoras

Estas son las áreas que aún no están cubiertas o que podrían ser mejoradas para una gestión más completa del restaurante:

1.  **Roles y Permisos de Usuario (RBAC):** Implementación de un sistema robusto de control de acceso basado en roles para restringir las acciones de los usuarios (empleados) según su `TipoUsuario`.
2.  **Flujo de Cocina/Preparación (KDS):** Desarrollo de un sistema para gestionar el ciclo de vida de los ítems de un pedido en la cocina (ej. panel de visualización para cocineros, marcaje de ítems como 'en preparación', 'listo').
3.  **Gestión de Menús:** Creación de una entidad o lógica para agrupar productos en "menús" (ej. menú del día, ofertas especiales) y gestionar su disponibilidad y precios.
4.  **Promociones y Descuentos Avanzados:** Implementación de un motor de reglas para aplicar descuentos y promociones más complejos (ej. 2x1, descuentos por volumen, cupones).
5.  **Alertas de Inventario Proactivas:** Integración de las alertas de stock bajo con sistemas de notificación (email, SMS, notificaciones push) en lugar de solo por consola.
6.  **Reabastecimiento Automático:** Lógica para generar automáticamente sugerencias u órdenes de compra a proveedores cuando el stock de un producto cae por debajo de su nivel mínimo.
7.  **Reportes y Análisis:** Desarrollo de módulos para generar reportes de ventas (diarias, semanales, mensuales), productos más vendidos, rendimiento de empleados, etc.
8.  **Gestión de Turnos/Horarios de Empleados:** Módulo para la planificación y gestión de los horarios de trabajo de los empleados.

## 🎯 Recomendaciones y Prioridades

Para continuar desarrollando el sistema de manera efectiva, sugiero la siguiente priorización:

### **Prioridad Alta:**

*   **1. Roles y Permisos de Usuario (RBAC):** Es fundamental para la seguridad y la integridad del sistema. Define quién puede hacer qué, lo cual es crítico en un entorno multiusuario como un restaurante.
*   **2. Flujo de Cocina/Preparación (KDS):** Es una funcionalidad central para la operación diaria del restaurante, mejorando la eficiencia en la preparación y entrega de pedidos.

### **Prioridad Media:**

*   **3. Alertas de Inventario Proactivas:** Mejorar las alertas de stock bajo con notificaciones reales es vital para evitar la falta de productos y optimizar las compras.
*   **4. Gestión de Menús:** Permite una presentación más flexible y atractiva de los productos a los clientes, y facilita la gestión de ofertas.

### **Prioridad Baja (pero importantes a largo plazo):**

*   **5. Promociones y Descuentos Avanzados:** Para estrategias de marketing y fidelización de clientes.
*   **6. Reabastecimiento Automático:** Optimiza la gestión de compras y reduce el trabajo manual.
*   **7. Reportes y Análisis:** Esencial para la toma de decisiones estratégicas y la evaluación del rendimiento del negocio.
*   **8. Gestión de Turnos/Horarios de Empleados:** Para una administración completa del personal.
