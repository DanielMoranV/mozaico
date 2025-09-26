## Guía para Claude Code: Desarrollo Frontend (Vue 3 + TypeScript)

Esta guía está diseñada para ayudar a Claude Code a generar componentes y lógica de frontend para el proyecto Mozaico, un sistema de gestión de restaurantes. Cada sección representa un módulo o funcionalidad específica del backend, y contiene la información necesaria para desarrollar la interfaz de usuario correspondiente utilizando Vue 3 y TypeScript.

### Instrucciones para Claude Code:
- Utiliza los tools `Read`, `Write`, `Edit` y `MultiEdit` para examinar y modificar archivos
- Emplea `Glob` y `Grep` para explorar la estructura del proyecto
- Usa `TodoWrite` para planificar y rastrear el progreso de tareas complejas
- Los endpoints del backend están en `/api/v1/` y utilizan `ApiResponse<T>` como wrapper
- Todos los DTOs están en el paquete `com.djasoft.mozaico.web.dtos`

---

### Módulo: Gestión de Categorías

**Funcionalidad:** CRUD completo para la administración de categorías de productos.

**API Endpoints (Backend):**

*   `POST /api/v1/categorias`: Crear una nueva categoría.
*   `GET /api/v1/categorias`: Obtener todas las categorías.
*   `GET /api/v1/categorias/{id}`: Obtener una categoría por su ID.
*   `PUT /api/v1/categorias/{id}`: Actualizar una categoría existente.
*   `DELETE /api/v1/categorias/{id}`: Eliminar una categoría.

**Modelos de Datos (DTOs - Backend):**

*   **`CategoriaRequestDTO` (para `POST` y `PUT`):**
    ```typescript
    interface CategoriaRequestDTO {
        nombre: string;
        descripcion?: string;
    }
    ```
*   **`CategoriaResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface CategoriaResponseDTO {
        idCategoria: number;
        nombre: string;
        descripcion?: string;
        fechaCreacion: string; // ISO 8601 datetime string (se serializa desde LocalDateTime)
        fechaActualizacion: string; // ISO 8601 datetime string (se serializa desde LocalDateTime)
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`CategoriaManager.vue`) que permita:

1.  **Visualizar Categorías:** Mostrar una tabla con todas las categorías existentes (id, nombre, descripción, fecha de creación/actualización).
2.  **Crear Categoría:** Un formulario modal para añadir una nueva categoría (campos: nombre, descripción).
3.  **Editar Categoría:** Un formulario modal pre-llenado para modificar una categoría existente.
4.  **Eliminar Categoría:** Un botón de eliminación con confirmación.
5.  **Validación:** Implementar validación básica en el formulario (ej. nombre es requerido).
6.  **Manejo de Estado:** Utilizar Pinia o Vuex (si es necesario, pero preferiblemente Pinia para simplicidad) para gestionar el estado de las categorías.
7.  **Interacción con API:** Utilizar `axios` (o `fetch` API) para interactuar con los endpoints del backend.
8.  **Notificaciones:** Mostrar mensajes de éxito o error al usuario (ej. usando una librería de notificaciones simple o un `alert` básico).
9.  **Estilo:** Utilizar un framework CSS como Bootstrap 5 (clases básicas) para la tabla y los formularios.

**Instrucciones para Claude Code:**

```
Genera un componente Vue 3 (`CategoriaManager.vue`) con TypeScript para gestionar categorías. Debe incluir:

1. Usar `Read` para examinar DTOs existentes en `src/main/java/com/djasoft/mozaico/web/dtos/Categoria*.java`
2. Crear tabla para listar categorías con interfaces TypeScript apropiadas
3. Formularios modales para crear/editar con validación
4. Funcionalidad de eliminación con confirmación
5. Usar Pinia para estado global
6. Axios para llamadas API a `/api/v1/categorias`
7. Bootstrap 5 para estilos
8. Manejo de respuestas `ApiResponse<T>` del backend

Estructura de archivos sugerida:
- `src/types/categoria.ts` - Interfaces TypeScript
- `src/stores/categoriaStore.ts` - Store de Pinia
- `src/components/CategoriaManager.vue` - Componente principal
```

---

### Módulo: Gestión de Productos

**Funcionalidad:** CRUD completo, búsqueda avanzada, activación/desactivación y subida de imágenes para productos.

**API Endpoints (Backend):**

*   `POST /api/v1/productos`: Crear un nuevo producto.
*   `GET /api/v1/productos`: Obtener todos los productos.
*   `GET /api/v1/productos/{id}`: Obtener un producto por su ID.
*   `PUT /api/v1/productos/{id}`: Actualizar un producto existente.
*   `DELETE /api/v1/productos/{id}`: Eliminar un producto.
*   `GET /api/v1/productos/buscar?nombre={nombre}&idCategoria={idCategoria}...`: Buscar productos por criterios.
*   `PATCH /api/v1/productos/{id}/activar`: Activar un producto.
*   `PATCH /api/v1/productos/{id}/desactivar`: Desactivar un producto.
*   `POST /api/v1/productos/{id}/image`: Subir imagen para un producto (multipart/form-data).

**Modelos de Datos (DTOs - Backend):**

*   **`ProductoRequestDTO` (para `POST`):**
    ```typescript
    interface ProductoRequestDTO {
        nombre: string;
        descripcion?: string;
        precio: number;
        idCategoria: number;
        tiempoPreparacion?: number;
        disponible?: boolean;
        imagenUrl?: string;
        ingredientes?: string;
        calorias?: number;
        codigoBarras?: string;
        marca?: string;
        presentacion?: string;
        requierePreparacion?: boolean;
        esAlcoholico?: boolean;
        estado?: 'ACTIVO' | 'INACTIVO';
    }
    ```
*   **`ProductoUpdateDTO` (para `PUT`):** Similar a `ProductoRequestDTO`, pero todos los campos son opcionales.
    ```typescript
    interface ProductoUpdateDTO {
        nombre?: string;
        descripcion?: string;
        precio?: number; // Se serializa desde BigDecimal en el backend
        idCategoria?: number;
        tiempoPreparacion?: number;
        disponible?: boolean;
        imagenUrl?: string;
        ingredientes?: string;
        calorias?: number;
        codigoBarras?: string;
        marca?: string;
        presentacion?: string;
        requierePreparacion?: boolean;
        esAlcoholico?: boolean;
        estado?: 'ACTIVO' | 'INACTIVO';
    }
    ```
*   **`ProductoResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface ProductoResponseDTO {
        idProducto: number;
        nombre: string;
        descripcion?: string;
        precio: number;
        categoria: {
            idCategoria: number;
            nombre: string;
        };
        tiempoPreparacion?: number;
        disponible: boolean;
        imagenUrl?: string;
        ingredientes?: string;
        calorias?: number;
        codigoBarras?: string;
        marca?: string;
        presentacion?: string;
        requierePreparacion: boolean;
        esAlcoholico: boolean;
        estado: 'ACTIVO' | 'INACTIVO';
        fechaCreacion: string;
        fechaActualizacion: string;
    }
    ```
*   **`EstadoProducto` (Enum):**
    ```typescript
    type EstadoProducto = 'ACTIVO' | 'INACTIVO';
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`ProductManager.vue`) que permita:

1.  **Visualizar Productos:** Una tabla paginada con filtros de búsqueda (por nombre, categoría, disponibilidad, estado, etc.) y la información clave de cada producto (nombre, precio, categoría, estado, imagen).
2.  **Crear Producto:** Un formulario modal para añadir un nuevo producto con todos los campos relevantes, incluyendo un selector de categoría (que debería cargar las categorías disponibles del backend).
3.  **Editar Producto:** Un formulario modal pre-llenado para modificar un producto existente.
4.  **Eliminar Producto:** Un botón de eliminación con confirmación.
5.  **Activar/Desactivar:** Botones para cambiar el estado de un producto.
6.  **Subir Imagen:** Un campo de subida de archivo para asociar una imagen a un producto.
7.  **Validación:** Implementar validación robusta en los formularios.
8.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de los productos y las categorías (para el selector).
9.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
10. **Notificaciones:** Mostrar mensajes de éxito o error.
11. **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`ProductManager.vue`) con TypeScript para gestionar productos. Debe incluir una tabla con paginación y filtros de búsqueda, formularios modales para crear y editar (con validación), funcionalidad para eliminar, activar/desactivar, y subir imágenes. Utiliza Pinia para el estado, axios para las llamadas API a `/api/v1/productos` y `/api/v1/categorias` (para el selector), y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `ProductoRequestDTO` y `ProductoResponseDTO`."
```

---

### Módulo: Gestión de Clientes

**Funcionalidad:** CRUD completo para la administración de clientes.

**API Endpoints (Backend):**

*   `POST /api/v1/clientes`: Crear un nuevo cliente.
*   `GET /api/v1/clientes`: Obtener todos los clientes.
*   `GET /api/v1/clientes/{id}`: Obtener un cliente por su ID.
*   `PUT /api/v1/clientes/{id}`: Actualizar un cliente existente.
*   `DELETE /api/v1/clientes/{id}`: Eliminar un cliente.

**Modelos de Datos (DTOs - Backend):**

*   **`ClienteRequestDTO` (para `POST` y `PUT`):**
    ```typescript
    interface ClienteRequestDTO {
        nombre: string;
        apellido: string;
        email?: string;
        telefono?: string;
        fechaNacimiento?: string; // ISO 8601 date string (YYYY-MM-DD)
        direccion?: string;
        preferenciasAlimentarias?: string;
    }
    ```
*   **`ClienteResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface ClienteResponseDTO {
        idCliente: number;
        nombre: string;
        apellido: string;
        email?: string;
        telefono?: string;
        fechaNacimiento?: string; // ISO 8601 date string (YYYY-MM-DD)
        direccion?: string;
        preferenciasAlimentarias?: string;
        puntosFidelidad: number;
        fechaRegistro: string; // ISO 8601 date string
        activo: boolean;
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`ClienteManager.vue`) que permita:

1.  **Visualizar Clientes:** Mostrar una tabla con todos los clientes, incluyendo nombre, apellido, email, teléfono, puntos de fidelidad, estado activo).
2.  **Crear Cliente:** Un formulario modal para añadir una nuevo cliente.
3.  **Editar Cliente:** Un formulario modal pre-llenado para modificar un cliente existente.
4.  **Eliminar Cliente:** Un botón de eliminación con confirmación.
5.  **Validación:** Implementar validación básica en el formulario (ej. nombre y apellido son requeridos).
6.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de los clientes.
7.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
8.  **Notificaciones:** Mostrar mensajes de éxito o error.
9.  **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`ClienteManager.vue`) con TypeScript para gestionar clientes. Debe incluir una tabla para listar clientes, formularios modales para crear y editar (con validación básica), y funcionalidad para eliminar. Utiliza Pinia para el estado, axios para las llamadas API a `/api/v1/clientes` y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `ClienteRequestDTO` y `ClienteResponseDTO`."
```

---

### Módulo: Gestión de Compras

**Funcionalidad:** CRUD completo para la administración de compras a proveedores, incluyendo sus detalles.

**API Endpoints (Backend):**

*   `POST /api/v1/compras`: Crear una nueva compra.
*   `GET /api/v1/compras`: Obtener todas las compras.
*   `GET /api/v1/compras/{id}`: Obtener una compra por su ID.
*   `PUT /api/v1/compras/{id}`: Actualizar una compra existente.
*   `DELETE /api/v1/compras/{id}`: Eliminar una compra.
*   `GET /api/v1/compras/{compraId}/detalles`: Obtener detalles de una compra.
*   `POST /api/v1/compras/{compraId}/detalles`: Añadir detalle a una compra.
*   `PUT /api/v1/compras/{compraId}/detalles/{detalleId}`: Actualizar detalle de una compra.
*   `DELETE /api/v1/compras/{compraId}/detalles/{detalleId}`: Eliminar detalle de una compra.

**Modelos de Datos (DTOs - Backend):**

*   **`EstadoCompra` (Enum):**
    ```typescript
    type EstadoCompra = 'PENDIENTE' | 'COMPLETADA' | 'CANCELADA';
    ```
*   **`DetalleCompraRequestDTO` (para `POST` y `PUT` de detalles):**
    ```typescript
    interface DetalleCompraRequestDTO {
        idProducto: number;
        cantidad: number;
        precioUnitario: number;
    }
    ```
*   **`CompraRequestDTO` (para `POST` y `PUT` de compras):**
    ```typescript
    interface CompraRequestDTO {
        idProveedor: number;
        fechaCompra: string; // ISO 8601 date string
        estado: EstadoCompra;
        detalles: DetalleCompraRequestDTO[]; // Lista de detalles de compra
    }
    ```
*   **`DetalleCompraResponseDTO` (para `GET` y respuestas de detalles):**
    ```typescript
    interface DetalleCompraResponseDTO {
        idDetalleCompra: number;
        idCompra: number;
        producto: {
            idProducto: number;
            nombre: string;
        };
        cantidad: number;
        precioUnitario: number;
        subtotal: number;
    }
    ```
*   **`CompraResponseDTO` (para `GET` y respuestas de compras):**
    ```typescript
    interface CompraResponseDTO {
        idCompra: number;
        proveedor: {
            idProveedor: number;
            nombre: string;
        };
        fechaCompra: string; // ISO 8601 date string
        total: number;
        estado: EstadoCompra;
        fechaCreacion: string; // ISO 8601 date string
        fechaActualizacion: string; // ISO 8601 date string
        detalles: DetalleCompraResponseDTO[];
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`CompraManager.vue`) que permita:

1.  **Visualizar Compras:** Mostrar una tabla con las compras, incluyendo información clave como proveedor, fecha, total y estado.
2.  **Ver Detalles de Compra:** Al seleccionar una compra, mostrar sus detalles (productos, cantidades, precios unitarios, subtotales).
3.  **Crear Compra:** Un formulario modal para registrar una nueva compra, permitiendo añadir múltiples detalles de productos. Deberá cargar proveedores y productos disponibles.
4.  **Editar Compra:** Un formulario modal para modificar una compra existente y sus detalles.
5.  **Eliminar Compra:** Un botón de eliminación con confirmación.
6.  **Validación:** Implementar validación en los formularios.
7.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de compras, proveedores y productos.
8.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
9.  **Notificaciones:** Mostrar mensajes de éxito o error.
10. **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`CompraManager.vue`) con TypeScript para gestionar compras. Debe incluir una tabla para listar compras, una vista de detalles de compra, formularios modales para crear y editar compras (con la capacidad de añadir/editar múltiples detalles de productos), y funcionalidad para eliminar. Utiliza Pinia para el estado (incluyendo proveedores y productos para selectores), axios para las llamadas API a `/api/v1/compras`, `/api/v1/proveedores` y `/api/v1/productos`, y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `EstadoCompra`, `DetalleCompraRequestDTO`, `CompraRequestDTO`, `DetalleCompraResponseDTO` y `CompraResponseDTO`."
```

---

### Módulo: Gestión de Pedidos

**Funcionalidad:** CRUD completo para la administración de pedidos de clientes, incluyendo sus detalles, tipos de servicio (mesa, llevar, delivery) y estados.

**API Endpoints (Backend):**

*   `POST /api/v1/pedidos`: Crear un nuevo pedido.
*   `GET /api/v1/pedidos`: Obtener todos los pedidos.
*   `GET /api/v1/pedidos/{id}`: Obtener un pedido por su ID.
*   `PUT /api/v1/pedidos/{id}`: Actualizar un pedido existente.
*   `DELETE /api/v1/pedidos/{id}`: Eliminar un pedido.
*   `GET /api/v1/pedidos/{pedidoId}/detalles`: Obtener detalles de un pedido.
*   `POST /api/v1/pedidos/{pedidoId}/detalles`: Añadir detalle a un pedido.
*   `PUT /api/v1/pedidos/{pedidoId}/detalles/{detalleId}`: Actualizar detalle de un pedido.
*   `DELETE /api/v1/pedidos/{pedidoId}/detalles/{detalleId}`: Eliminar detalle de un pedido.
*   `PATCH /api/v1/pedidos/{id}/estado`: Actualizar el estado de un pedido.

**Modelos de Datos (DTOs - Backend):**

*   **`EstadoPedido` (Enum):**
    ```typescript
    type EstadoPedido = 'PENDIENTE' | 'EN_PREPARACION' | 'LISTO' | 'ENTREGADO' | 'CANCELADO';
    ```
*   **`TipoServicio` (Enum):**
    ```typescript
    type TipoServicio = 'MESA' | 'LLEVAR' | 'DELIVERY';
    ```
*   **`EstadoDetallePedido` (Enum):**
    ```typescript
    type EstadoDetallePedido = 'PENDIENTE' | 'EN_PREPARACION' | 'LISTO' | 'ENTREGADO';
    ```
*   **`DetallePedidoRequestDTO` (para `POST` y `PUT` de detalles):**
    ```typescript
    interface DetallePedidoRequestDTO {
        idProducto: number;
        cantidad: number;
        observaciones?: string;
    }
    ```
*   **`PedidoRequestDTO` (para `POST` y `PUT` de pedidos):**
    ```typescript
    interface PedidoRequestDTO {
        idCliente?: number;
        idMesa?: number;
        idEmpleado?: number;
        tipoServicio: TipoServicio;
        observaciones?: string;
        direccionDelivery?: string;
        detalles: DetallePedidoRequestDTO[]; // Lista de detalles de pedido
    }
    ```
*   **`DetallePedidoResponseDTO` (para `GET` y respuestas de detalles):**
    ```typescript
    interface DetallePedidoResponseDTO {
        idDetalle: number;
        idPedido: number;
        producto: {
            idProducto: number;
            nombre: string;
        };
        cantidad: number;
        precioUnitario: number;
        subtotal: number;
        observaciones?: string;
        estado: EstadoDetallePedido;
    }
    ```
*   **`PedidoResponseDTO` (para `GET` y respuestas de pedidos):**
    ```typescript
    interface PedidoResponseDTO {
        idPedido: number;
        cliente?: {
            idCliente: number;
            nombre: string;
            apellido: string;
        };
        mesa?: {
            idMesa: number;
            numero: string;
        };
        empleado?: {
            idUsuario: number;
            nombreUsuario: string;
        };
        fechaPedido: string; // ISO 8601 date string
        estado: EstadoPedido;
        tipoServicio: TipoServicio;
        subtotal: number;
        impuestos: number;
        descuento: number;
        total: number;
        observaciones?: string;
        direccionDelivery?: string;
        detalles: DetallePedidoResponseDTO[];
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`PedidoManager.vue`) que permita:

1.  **Visualizar Pedidos:** Mostrar una tabla con los pedidos, incluyendo información clave como cliente, mesa, tipo de servicio, estado y total.
2.  **Ver Detalles de Pedido:** Al seleccionar un pedido, mostrar sus detalles (productos, cantidades, precios unitarios, subtotales, estado del detalle).
3.  **Crear Pedido:** Un formulario modal para registrar un nuevo pedido, permitiendo seleccionar cliente, mesa (si aplica), tipo de servicio y añadir múltiples detalles de productos. Deberá cargar clientes, mesas y productos disponibles.
4.  **Editar Pedido:** Un formulario modal para modificar un pedido existente y sus detalles.
5.  **Actualizar Estado de Pedido:** Un mecanismo para cambiar el estado de un pedido (ej. de PENDIENTE a EN_PREPARACION).
6.  **Eliminar Pedido:** Un botón de eliminación con confirmación.
7.  **Validación:** Implementar validación en los formularios.
8.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de pedidos, clientes, mesas y productos.
9.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
10. **Notificaciones:** Mostrar mensajes de éxito o error.
11. **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`PedidoManager.vue`) con TypeScript para gestionar pedidos. Debe incluir una tabla para listar pedidos, una vista de detalles de pedido, formularios modales para crear y editar pedidos (con la capacidad de añadir/editar múltiples detalles de productos, seleccionar cliente, mesa, tipo de servicio), funcionalidad para actualizar el estado y eliminar. Utiliza Pinia para el estado (incluyendo clientes, mesas y productos para selectores), axios para las llamadas API a `/api/v1/pedidos`, `/api/v1/clientes`, `/api/v1/mesas` y `/api/v1/productos`, y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `EstadoPedido`, `TipoServicio`, `EstadoDetallePedido`, `DetallePedidoRequestDTO`, `PedidoRequestDTO`, `DetallePedidoResponseDTO` y `PedidoResponseDTO`."
```

---

### Módulo: Gestión de Inventario

**Funcionalidad:** Gestión del inventario de productos, incluyendo stock actual, stock mínimo, stock máximo y costo unitario.

**API Endpoints (Backend):**

*   `POST /api/v1/inventario`: Crear una nueva entrada de inventario.
*   `GET /api/v1/inventario`: Obtener todas las entradas de inventario.
*   `GET /api/v1/inventario/{id}`: Obtener una entrada de inventario por su ID.
*   `PUT /api/v1/inventario/{id}`: Actualizar una entrada de inventario existente.
*   `DELETE /api/v1/inventario/{id}`: Eliminar una entrada de inventario.
*   `PATCH /api/v1/inventario/{id}/ajustar-stock`: Ajustar el stock de un producto en el inventario.

**Modelos de Datos (DTOs - Backend):**

*   **`InventarioRequestDTO` (para `POST` y `PUT`):**
    ```typescript
    interface InventarioRequestDTO {
        idProducto: number;
        stockActual: number;
        stockMinimo?: number;
        stockMaximo?: number;
        costoUnitario?: number;
    }
    ```
*   **`InventarioUpdateDTO` (para `PUT`):** Similar a `InventarioRequestDTO`, pero todos los campos son opcionales.
    ```typescript
    interface InventarioUpdateDTO {
        stockActual?: number;
        stockMinimo?: number;
        stockMaximo?: number;
        costoUnitario?: number;
    }
    ```
*   **`InventarioResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface InventarioResponseDTO {
        idInventario: number;
        producto: {
            idProducto: number;
            nombre: string;
        };
        stockActual: number;
        stockMinimo: number;
        stockMaximo: number;
        costoUnitario?: number;
        fechaActualizacion: string; // ISO 8601 date string
    }
    ```
*   **`AjustarStockRequestDTO` (para `PATCH /api/v1/inventario/{id}/ajustar-stock`):**
    ```typescript
    interface AjustarStockRequestDTO {
        cantidad: number; // Cantidad a añadir o restar
        tipoAjuste: 'ENTRADA' | 'SALIDA'; // O un enum más descriptivo
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`InventarioManager.vue`) que permita:

1.  **Visualizar Inventario:** Mostrar una tabla con todas las entradas de inventario, incluyendo producto, stock actual, stock mínimo, stock máximo y costo unitario.
2.  **Crear Entrada de Inventario:** Un formulario modal para añadir una nueva entrada de inventario, seleccionando un producto.
3.  **Editar Entrada de Inventario:** Un formulario modal pre-llenado para modificar una entrada existente.
4.  **Eliminar Entrada de Inventario:** Un botón de eliminación con confirmación.
5.  **Ajustar Stock:** Un formulario modal para ajustar el stock de un producto (añadir o restar cantidad).
6.  **Validación:** Implementar validación en los formularios.
7.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado del inventario y los productos.
8.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
9.  **Notificaciones:** Mostrar mensajes de éxito o error.
10. **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`InventarioManager.vue`) con TypeScript para gestionar el inventario. Debe incluir una tabla para listar entradas de inventario, formularios modales para crear y editar (con selección de producto), funcionalidad para eliminar y ajustar stock. Utiliza Pinia para el estado (incluyendo productos para selectores), axios para las llamadas API a `/api/v1/inventario` y `/api/v1/productos`, y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `InventarioRequestDTO`, `InventarioUpdateDTO`, `InventarioResponseDTO` y `AjustarStockRequestDTO`."
```

---

### Módulo: Gestión de Menús

**Funcionalidad:** CRUD completo para la administración de menús, incluyendo la asociación de productos, disponibilidad y rangos de fechas.

**API Endpoints (Backend):**

*   `POST /api/v1/menus`: Crear un nuevo menú.
*   `GET /api/v1/menus`: Obtener todos los menús.
*   `GET /api/v1/menus/{id}`: Obtener un menú por su ID.
*   `PUT /api/v1/menus/{id}`: Actualizar un menú existente.
*   `DELETE /api/v1/menus/{id}`: Eliminar un menú.
*   `POST /api/v1/menus/{id}/productos/{productoId}`: Añadir un producto a un menú.
*   `DELETE /api/v1/menus/{id}/productos/{productoId}`: Eliminar un producto de un menú.

**Modelos de Datos (DTOs - Backend):**

*   **`MenuRequestDTO` (para `POST` y `PUT`):**
    ```typescript
    interface MenuRequestDTO {
        nombre: string;
        descripcion?: string;
        precio: number;
        disponible?: boolean;
        fechaInicio?: string; // ISO 8601 date string (YYYY-MM-DD)
        fechaFin?: string; // ISO 8601 date string (YYYY-MM-DD)
        productosIds?: number[]; // IDs de productos asociados
    }
    ```
*   **`MenuResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface MenuResponseDTO {
        idMenu: number;
        nombre: string;
        descripcion?: string;
        precio: number;
        disponible: boolean;
        fechaInicio?: string; // ISO 8601 date string (YYYY-MM-DD)
        fechaFin?: string; // ISO 8601 date string (YYYY-MM-DD)
        productos: Array<{
            idProducto: number;
            nombre: string;
        }>; // Lista de productos asociados (simplificado)
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`MenuManager.vue`) que permita:

1.  **Visualizar Menús:** Mostrar una tabla con todos los menús, incluyendo nombre, descripción, precio, disponibilidad y fechas.
2.  **Ver Productos del Menú:** Al seleccionar un menú, mostrar los productos asociados.
3.  **Crear Menú:** Un formulario modal para añadir un nuevo menú, permitiendo seleccionar múltiples productos de una lista de productos disponibles.
4.  **Editar Menú:** Un formulario modal pre-llenado para modificar un menú existente, incluyendo la gestión de productos asociados.
5.  **Eliminar Menú:** Un botón de eliminación con confirmación.
6.  **Validación:** Implementar validación en los formularios.
7.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de menús y productos.
8.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
9.  **Notificaciones:** Mostrar mensajes de éxito o error.
10. **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`MenuManager.vue`) con TypeScript para gestionar menús. Debe incluir una tabla para listar menús, una vista de productos asociados, formularios modales para crear y editar menús (con selección múltiple de productos), y funcionalidad para eliminar. Utiliza Pinia para el estado (incluyendo productos para selectores), axios para las llamadas API a `/api/v1/menus` y `/api/v1/productos`, y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `MenuRequestDTO` y `MenuResponseDTO`."
```

---

### Módulo: Gestión de Mesas

**Funcionalidad:** CRUD completo para la administración de mesas, incluyendo su número, capacidad, ubicación, estado y observaciones.

**API Endpoints (Backend):**

*   `POST /api/v1/mesas`: Crear una nueva mesa.
*   `GET /api/v1/mesas`: Obtener todas las mesas.
*   `GET /api/v1/mesas/{id}`: Obtener una mesa por su ID.
*   `PUT /api/v1/mesas/{id}`: Actualizar una mesa existente.
*   `DELETE /api/v1/mesas/{id}`: Eliminar una mesa.
*   `PATCH /api/v1/mesas/{id}/estado`: Actualizar el estado de una mesa.

**Modelos de Datos (DTOs - Backend):**

*   **`EstadoMesa` (Enum):**
    ```typescript
    type EstadoMesa = 'DISPONIBLE' | 'OCUPADA' | 'RESERVADA' | 'MANTENIMIENTO';
    ```
*   **`MesaRequestDTO` (para `POST` y `PUT`):**
    ```typescript
    interface MesaRequestDTO {
        numeroMesa: number;
        capacidad: number;
        ubicacion?: string;
        estado?: EstadoMesa;
        observaciones?: string;
    }
    ```
*   **`MesaResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface MesaResponseDTO {
        idMesa: number;
        numeroMesa: number;
        capacidad: number;
        ubicacion?: string;
        estado: EstadoMesa;
        observaciones?: string;
        fechaCreacion: string; // ISO 8601 date string
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`MesaManager.vue`) que permita:

1.  **Visualizar Mesas:** Mostrar una tabla con todas las mesas, incluyendo número, capacidad, ubicación y estado.
2.  **Crear Mesa:** Un formulario modal para añadir una nueva mesa.
3.  **Editar Mesa:** Un formulario modal pre-llenado para modificar una mesa existente.
4.  **Eliminar Mesa:** Un botón de eliminación con confirmación.
5.  **Actualizar Estado de Mesa:** Un mecanismo para cambiar el estado de una mesa (ej. de DISPONIBLE a OCUPADA).
6.  **Validación:** Implementar validación en los formularios.
7.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de las mesas.
8.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
9.  **Notificaciones:** Mostrar mensajes de éxito o error.
10. **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`MesaManager.vue`) con TypeScript para gestionar mesas. Debe incluir una tabla para listar mesas, formularios modales para crear y editar, funcionalidad para eliminar y actualizar el estado. Utiliza Pinia para el estado, axios para las llamadas API a `/api/v1/mesas` y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `EstadoMesa`, `MesaRequestDTO` y `MesaResponseDTO`."
```

---

### Módulo: Gestión de Métodos de Pago

**Funcionalidad:** CRUD completo para la administración de métodos de pago, incluyendo su nombre y estado de actividad.

**API Endpoints (Backend):**

*   `POST /api/v1/metodos-pago`: Crear un nuevo método de pago.
*   `GET /api/v1/metodos-pago`: Obtener todos los métodos de pago.
*   `GET /api/v1/metodos-pago/{id}`: Obtener un método de pago por su ID.
*   `PUT /api/v1/metodos-pago/{id}`: Actualizar un método de pago existente.
*   `DELETE /api/v1/metodos-pago/{id}`: Eliminar un método de pago.

**Modelos de Datos (DTOs - Backend):**

*   **`MetodoPagoRequestDTO` (para `POST` y `PUT`):**
    ```typescript
    interface MetodoPagoRequestDTO {
        nombre: string;
        activo?: boolean;
    }
    ```
*   **`MetodoPagoResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface MetodoPagoResponseDTO {
        idMetodo: number;
        nombre: string;
        activo: boolean;
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`MetodoPagoManager.vue`) que permita:

1.  **Visualizar Métodos de Pago:** Mostrar una tabla con todos los métodos de pago, incluyendo nombre y estado activo.
2.  **Crear Método de Pago:** Un formulario modal para añadir un nuevo método de pago.
3.  **Editar Método de Pago:** Un formulario modal pre-llenado para modificar un método de pago existente.
4.  **Eliminar Método de Pago:** Un botón de eliminación con confirmación.
5.  **Validación:** Implementar validación básica en el formulario (ej. nombre es requerido).
6.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de los métodos de pago.
7.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
8.  **Notificaciones:** Mostrar mensajes de éxito o error.
9.  **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`MetodoPagoManager.vue`) con TypeScript para gestionar métodos de pago. Debe incluir una tabla para listar métodos de pago, formularios modales para crear y editar (con validación básica), y funcionalidad para eliminar. Utiliza Pinia para el estado, axios para las llamadas API a `/api/v1/metodos-pago` y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `MetodoPagoRequestDTO` y `MetodoPagoResponseDTO`."
```

---

### Módulo: Gestión de Pagos

**Funcionalidad:** CRUD completo para la administración de pagos, incluyendo el pedido asociado, método de pago, monto, fecha, referencia y estado.

**API Endpoints (Backend):**

*   `POST /api/v1/pagos`: Crear un nuevo pago.
*   `GET /api/v1/pagos`: Obtener todos los pagos.
*   `GET /api/v1/pagos/{id}`: Obtener un pago por su ID.
*   `PUT /api/v1/pagos/{id}`: Actualizar un pago existente.
*   `DELETE /api/v1/pagos/{id}`: Eliminar un pago.

**Modelos de Datos (DTOs - Backend):**

*   **`EstadoPago` (Enum):**
    ```typescript
    type EstadoPago = 'COMPLETADO' | 'PENDIENTE' | 'FALLIDO';
    ```
*   **`PagoRequestDTO` (para `POST` y `PUT`):**
    ```typescript
    interface PagoRequestDTO {
        idPedido: number;
        idMetodoPago: number;
        monto: number;
        referencia?: string;
        estado?: EstadoPago;
    }
    ```
*   **`PagoResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface PagoResponseDTO {
        idPago: number;
        pedido: {
            idPedido: number;
            total: number;
        };
        metodoPago: {
            idMetodo: number;
            nombre: string;
        };
        monto: number;
        fechaPago: string; // ISO 8601 date string
        referencia?: string;
        estado: EstadoPago;
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`PagoManager.vue`) que permita:

1.  **Visualizar Pagos:** Mostrar una tabla con todos los pagos, incluyendo pedido, método de pago, monto, fecha y estado.
2.  **Crear Pago:** Un formulario modal para añadir un nuevo pago, permitiendo seleccionar un pedido y un método de pago.
3.  **Editar Pago:** Un formulario modal pre-llenado para modificar un pago existente.
4.  **Eliminar Pago:** Un botón de eliminación con confirmación.
5.  **Validación:** Implementar validación en los formularios.
6.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de pagos, pedidos y métodos de pago.
7.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
8.  **Notificaciones:** Mostrar mensajes de éxito o error.
9.  **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`PagoManager.vue`) con TypeScript para gestionar pagos. Debe incluir una tabla para listar pagos, formularios modales para crear y editar (con selección de pedido y método de pago), y funcionalidad para eliminar. Utiliza Pinia para el estado (incluyendo pedidos y métodos de pago para selectores), axios para las llamadas API a `/api/v1/pagos`, `/api/v1/pedidos` y `/api/v1/metodos-pago`, y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `EstadoPago`, `PagoRequestDTO` y `PagoResponseDTO`."
```

---

### Módulo: Gestión de Proveedores

**Funcionalidad:** CRUD completo para la administración de proveedores, incluyendo su información de contacto y estado de actividad.

**API Endpoints (Backend):**

*   `POST /api/v1/proveedores`: Crear un nuevo proveedor.
*   `GET /api/v1/proveedores`: Obtener todos los proveedores.
*   `GET /api/v1/proveedores/{id}`: Obtener un proveedor por su ID.
*   `PUT /api/v1/proveedores/{id}`: Actualizar un proveedor existente.
*   `DELETE /api/v1/proveedores/{id}`: Eliminar un proveedor.

**Modelos de Datos (DTOs - Backend):**

*   **`ProveedorRequestDTO` (para `POST` y `PUT`):**
    ```typescript
    interface ProveedorRequestDTO {
        nombre: string;
        contacto?: string;
        telefono?: string;
        email?: string;
        direccion?: string;
        activo?: boolean;
    }
    ```
*   **`ProveedorResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface ProveedorResponseDTO {
        idProveedor: number;
        nombre: string;
        contacto?: string;
        telefono?: string;
        email?: string;
        direccion?: string;
        activo: boolean;
        fechaCreacion: string; // ISO 8601 date string
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`ProveedorManager.vue`) que permita:

1.  **Visualizar Proveedores:** Mostrar una tabla con todos los proveedores, incluyendo nombre, contacto, teléfono, email y estado activo.
2.  **Crear Proveedor:** Un formulario modal para añadir un nuevo proveedor.
3.  **Editar Proveedor:** Un formulario modal pre-llenado para modificar un proveedor existente.
4.  **Eliminar Proveedor:** Un botón de eliminación con confirmación.
5.  **Validación:** Implementar validación básica en el formulario (ej. nombre es requerido).
6.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de los proveedores.
7.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
8.  **Notificaciones:** Mostrar mensajes de éxito o error.
9.  **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`ProveedorManager.vue`) con TypeScript para gestionar proveedores. Debe incluir una tabla para listar proveedores, formularios modales para crear y editar (con validación básica), y funcionalidad para eliminar. Utiliza Pinia para el estado, axios para las llamadas API a `/api/v1/proveedores` y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `ProveedorRequestDTO` y `ProveedorResponseDTO`."
```

---

### Módulo: Gestión de Reservas

**Funcionalidad:** CRUD completo para la administración de reservas de mesas, incluyendo cliente, mesa, fecha/hora, número de personas, estado y observaciones.

**API Endpoints (Backend):**

*   `POST /api/v1/reservas`: Crear una nueva reserva.
*   `GET /api/v1/reservas`: Obtener todas las reservas.
*   `GET /api/v1/reservas/{id}`: Obtener una reserva por su ID.
*   `PUT /api/v1/reservas/{id}`: Actualizar una reserva existente.
*   `DELETE /api/v1/reservas/{id}`: Eliminar una reserva.
*   `PATCH /api/v1/reservas/{id}/estado`: Actualizar el estado de una reserva.

**Modelos de Datos (DTOs - Backend):**

*   **`EstadoReserva` (Enum):**
    ```typescript
    type EstadoReserva = 'CONFIRMADA' | 'PENDIENTE' | 'CANCELADA';
    ```
*   **`ReservaRequestDTO` (para `POST` y `PUT`):**
    ```typescript
    interface ReservaRequestDTO {
        idCliente: number;
        idMesa: number;
        fechaHoraReserva: string; // ISO 8601 date-time string
        numeroPersonas: number;
        estado?: EstadoReserva;
        observaciones?: string;
    }
    ```
*   **`ReservaResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface ReservaResponseDTO {
        idReserva: number;
        cliente: {
            idCliente: number;
            nombre: string;
            apellido: string;
        };
        mesa: {
            idMesa: number;
            numeroMesa: number;
        };
        fechaHoraReserva: string; // ISO 8601 date-time string
        numeroPersonas: number;
        estado: EstadoReserva;
        observaciones?: string;
        fechaCreacion: string; // ISO 8601 date string
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):

Necesito un componente Vue (`ReservaManager.vue`) que permita:

1.  **Visualizar Reservas:** Mostrar una tabla con todas las reservas, incluyendo cliente, mesa, fecha/hora, número de personas y estado.
2.  **Crear Reserva:** Un formulario modal para añadir una nueva reserva, permitiendo seleccionar cliente y mesa, e introducir fecha/hora y número de personas.
3.  **Editar Reserva:** Un formulario modal pre-llenado para modificar una reserva existente.
4.  **Eliminar Reserva:** Un botón de eliminación con confirmación.
5.  **Actualizar Estado de Reserva:** Un mecanismo para cambiar el estado de una reserva.
6.  **Validación:** Implementar validación en los formularios.
7.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de reservas, clientes y mesas.
8.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
9.  **Notificaciones:** Mostrar mensajes de éxito o error.
10. **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`ReservaManager.vue`) con TypeScript para gestionar reservas. Debe incluir una tabla para listar reservas, formularios modales para crear y editar (con selección de cliente y mesa), funcionalidad para eliminar y actualizar el estado. Utiliza Pinia para el estado (incluyendo clientes y mesas para selectores), axios para las llamadas API a `/api/v1/reservas`, `/api/v1/clientes` y `/api/v1/mesas`, y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `EstadoReserva`, `ReservaRequestDTO` y `ReservaResponseDTO`."
```

---

### Módulo: Gestión de Usuarios

**Funcionalidad:** CRUD completo para la administración de usuarios, incluyendo su información personal, credenciales, roles, estado y detalles de acceso.

**API Endpoints (Backend):**

*   `POST /api/v1/usuarios`: Crear un nuevo usuario.
*   `GET /api/v1/usuarios`: Obtener todos los usuarios.
*   `GET /api/v1/usuarios/{id}`: Obtener un usuario por su ID.
*   `PUT /api/v1/usuarios/{id}`: Actualizar un usuario existente.
*   `DELETE /api/v1/usuarios/{id}`: Eliminar un usuario.
*   `PATCH /api/v1/usuarios/{id}/estado`: Actualizar el estado de un usuario.
*   `PATCH /api/v1/usuarios/{id}/password`: Cambiar la contraseña de un usuario.

**Modelos de Datos (DTOs - Backend):**

*   **`EstadoUsuario` (Enum):**
    ```typescript
    type EstadoUsuario = 'ACTIVO' | 'INACTIVO' | 'SUSPENDIDO';
    ```
*   **`TipoDocumentoIdentidad` (Enum):**
    ```typescript
    type TipoDocumentoIdentidad = 'DNI' | 'PASAPORTE' | 'CARNE_EXTRANJERIA' | 'OTROS';
    ```
*   **`TipoUsuario` (Enum):**
    ```typescript
    type TipoUsuario = 'ADMIN' | 'MESERO' | 'COCINERO' | 'CAJERO' | 'GERENTE';
    ```
*   **`UsuarioRequestDTO` (para `POST`):**
    ```typescript
    interface UsuarioRequestDTO {
        nombre: string;
        username: string;
        numeroDocumentoIdentidad: string;
        tipoDocumentoIdentidad: TipoDocumentoIdentidad;
        email: string;
        password: string; // Solo para creación
        tipoUsuario: TipoUsuario;
    }
    ```
*   **`UsuarioUpdateDTO` (para `PUT`):**
    ```typescript
    interface UsuarioUpdateDTO {
        nombre?: string;
        username?: string;
        numeroDocumentoIdentidad?: string;
        tipoDocumentoIdentidad?: TipoDocumentoIdentidad;
        email?: string;
        tipoUsuario?: TipoUsuario;
        estado?: EstadoUsuario;
    }
    ```
*   **`UsuarioResponseDTO` (para `GET` y respuestas de `POST`/`PUT`):**
    ```typescript
    interface UsuarioResponseDTO {
        idUsuario: number;
        nombre: string;
        username: string;
        numeroDocumentoIdentidad: string;
        tipoDocumentoIdentidad: TipoDocumentoIdentidad;
        email: string;
        tipoUsuario: TipoUsuario;
        estado: EstadoUsuario;
        fechaCreacion: string; // ISO 8601 date-time string
        fechaUltimoAcceso?: string; // ISO 8601 date-time string
        intentosFallidos: number;
        ipUltimoAcceso?: string;
        createdAt: string; // ISO 8601 date-time string
        updatedAt: string; // ISO 8601 date-time string
    }
    ```
*   **`ChangePasswordRequestDTO` (para `PATCH /api/v1/usuarios/{id}/password`):**
    ```typescript
    interface ChangePasswordRequestDTO {
        currentPassword?: string; // Si se requiere la contraseña actual para cambiarla
        newPassword: string;
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesito un componente Vue (`UsuarioManager.vue`) que permita:

1.  **Visualizar Usuarios:** Mostrar una tabla con todos los usuarios, incluyendo nombre, username, email, tipo de usuario y estado.
2.  **Crear Usuario:** Un formulario modal para añadir un nuevo usuario, incluyendo todos los campos relevantes y selección de tipo de documento y tipo de usuario.
3.  **Editar Usuario:** Un formulario modal pre-llenado para modificar un usuario existente (excluyendo la contraseña directamente).
4.  **Eliminar Usuario:** Un botón de eliminación con confirmación.
5.  **Actualizar Estado de Usuario:** Un mecanismo para cambiar el estado de un usuario (ej. ACTIVO, INACTIVO, SUSPENDIDO).
6.  **Cambiar Contraseña:** Un formulario modal para que un administrador pueda cambiar la contraseña de un usuario.
7.  **Validación:** Implementar validación robusta en los formularios.
8.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de los usuarios.
9.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
10. **Notificaciones:** Mostrar mensajes de éxito o error.
11. **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`UsuarioManager.vue`) con TypeScript para gestionar usuarios. Debe incluir una tabla para listar usuarios, formularios modales para crear y editar (con selección de tipo de documento y tipo de usuario), funcionalidad para eliminar, actualizar el estado y cambiar la contraseña. Utiliza Pinia para el estado, axios para las llamadas API a `/api/v1/usuarios` y Bootstrap 5 para el estilo. Incluye interfaces TypeScript para `EstadoUsuario`, `TipoDocumentoIdentidad`, `TipoUsuario`, `UsuarioRequestDTO`, `UsuarioUpdateDTO`, `UsuarioResponseDTO` y `ChangePasswordRequestDTO`."
```

---

### Módulo: Reportes

**Funcionalidad:** Generación de informes clave para la toma de decisiones.

**API Endpoints (Backend):**

*   `GET /api/v1/reports/sales-summary`: Obtener un resumen de ventas por período.
*   `GET /api/v1/reports/product-sales`: Obtener un informe de ventas por producto.
*   `GET /api/v1/reports/low-stock-inventory`: Obtener un informe de productos con bajo stock.

**Modelos de Datos (DTOs - Backend):**

*   **`SalesSummaryResponseDTO`:**
    ```typescript
    interface SalesSummaryResponseDTO {
        startDate: string; // ISO 8601 date string (YYYY-MM-DD) - se serializa desde LocalDate
        endDate: string; // ISO 8601 date string (YYYY-MM-DD) - se serializa desde LocalDate
        totalRevenue: number; // Se serializa desde BigDecimal en el backend
        totalOrders: number; // Se serializa desde Long en el backend
        averageOrderValue: number; // Se serializa desde BigDecimal en el backend
    }
    ```
*   **`ProductSalesResponseDTO`:**
    ```typescript
    interface ProductSalesResponseDTO {
        idProducto: number;
        nombreProducto: string;
        nombreCategoria: string;
        cantidadVendida: number;
        totalVentas: number;
    }
    ```
*   **`LowStockItemResponseDTO`:**
    ```typescript
    interface LowStockItemResponseDTO {
        idProducto: number;
        nombreProducto: string;
        stockActual: number;
        stockMinimo: number;
        // Opcional: nombreProveedor?: string;
    }
    ```

**Requerimientos Frontend (Vue 3 + TypeScript):**

Necesitarás un componente Vue (`ReportManager.vue`) que permita:

1.  **Selección de Reporte:** Un selector para elegir el tipo de reporte a generar.
2.  **Parámetros de Filtro:** Formularios dinámicos para ingresar los parámetros necesarios para cada reporte (ej. rango de fechas para ventas).
3.  **Visualización de Resultados:** Mostrar los datos del reporte en tablas o gráficos (si se implementa una librería de gráficos).
4.  **Interacción con API:** Utilizar `axios` para interactuar con los endpoints del backend.
5.  **Manejo de Estado:** Utilizar Pinia para gestionar el estado de los reportes y sus datos.
6.  **Notificaciones:** Mostrar mensajes de éxito o error.
7.  **Estilo:** Utilizar Bootstrap 5 para la interfaz.

**Prompt para Gemini:**

```
"Genera un componente Vue 3 (`ReportManager.vue`) con TypeScript para visualizar el 'Sales Summary Report'. Debe incluir un selector de rango de fechas y mostrar los resultados en una tabla. Utiliza Pinia para el estado, axios para la llamada API a `/api/v1/reports/sales-summary` y Bootstrap 5 para el estilo. Incluye la interfaz TypeScript para `SalesSummaryResponseDTO`."
```
