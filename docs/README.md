# Mozaico: Sistema de Gestión de Restaurantes

## Descripción General

Mozaico es un sistema de gestión integral diseñado para restaurantes, construido con Spring Boot. Proporciona una API RESTful robusta para manejar diversas operaciones relacionadas con la administración de un restaurante, incluyendo la gestión de categorías, productos, clientes, pedidos, inventario, mesas, pagos, usuarios y más.

## Tecnologías Utilizadas

*   **Spring Boot 3.5.6:** Framework principal para el desarrollo de la aplicación.
*   **Java 21:** Lenguaje de programación.
*   **Spring Data JPA:** Para la persistencia de datos y la interacción con la base de datos.
*   **PostgreSQL:** Base de datos relacional.
*   **Spring Security:** Para la autenticación y autorización de usuarios.
*   **Spring Web:** Para la construcción de APIs RESTful.
*   **Lombok:** Para reducir el código repetitivo (boilerplate).
*   **Jakarta Validation:** Para la validación de datos de entrada.
*   **Springdoc OpenAPI (Swagger UI):** Para la generación automática de documentación de la API y una interfaz interactiva para probar los endpoints.

## Módulos y Funcionalidades Principales

El sistema está estructurado en varios módulos, cada uno con sus propias entidades y controladores para gestionar las operaciones correspondientes.

### 1. Gestión de Categorías (`Categoria`)

*   **Entidad:** `Categoria` (id, nombre, descripción, fechas de creación/actualización).
*   **Funcionalidades API:**
    *   Crear nuevas categorías.
    *   Obtener todas las categorías.
    *   Obtener una categoría por su ID.
    *   Actualizar una categoría existente.
    *   Eliminar una categoría.

### 2. Gestión de Productos (`Producto`)

*   **Entidad:** `Producto` (id, nombre, descripción, precio, categoría, tiempo de preparación, disponibilidad, URL de imagen, ingredientes, calorías, código de barras, marca, presentación, requiere preparación, es alcohólico, estado, fechas de creación/actualización).
*   **Funcionalidades API:**
    *   Crear nuevos productos.
    *   Obtener todos los productos.
    *   Obtener un producto por su ID.
    *   Actualizar un producto existente.
    *   Eliminar un producto.
    *   Buscar productos por diversos criterios (nombre, descripción, categoría, disponibilidad, etc.).
    *   Activar/desactivar productos.
    *   Subir imágenes para productos.

### 3. Gestión de Clientes (`Cliente`)

*   **Entidad:** `Cliente` (id, nombre, apellido, email, teléfono, dirección, fechas de creación/actualización).
*   **Funcionalidades API:** CRUD completo para la gestión de clientes.

### 4. Gestión de Compras (`Compra`, `DetalleCompra`)

*   **Entidades:** `Compra` (id, proveedor, fecha, total, estado, fechas de creación/actualización), `DetalleCompra` (id, compra, producto, cantidad, precio unitario, subtotal).
*   **Funcionalidades API:** Gestión de compras a proveedores y sus detalles.

### 5. Gestión de Pedidos (`Pedido`, `DetallePedido`)

*   **Entidades:** `Pedido` (id, cliente, mesa, fecha, total, tipo de servicio, estado, fechas de creación/actualización), `DetallePedido` (id, pedido, producto, cantidad, precio unitario, subtotal, estado).
*   **Funcionalidades API:** Gestión de pedidos de clientes, incluyendo pedidos en mesa y para llevar, y sus detalles.

### 6. Gestión de Inventario (`Inventario`)

*   **Entidad:** `Inventario` (id, producto, cantidad, ubicación, fechas de entrada/salida, fechas de creación/actualización).
*   **Funcionalidades API:** Control de existencias de productos.

### 7. Gestión de Menús (`Menu`)

*   **Entidad:** `Menu` (id, nombre, descripción, productos asociados, fechas de creación/actualización).
*   **Funcionalidades API:** Creación y gestión de menús del restaurante.

### 8. Gestión de Mesas (`Mesa`)

*   **Entidad:** `Mesa` (id, número, capacidad, estado, fechas de creación/actualización).
*   **Funcionalidades API:** Gestión del estado y disponibilidad de las mesas.

### 9. Gestión de Métodos de Pago (`MetodoPago`)

*   **Entidad:** `MetodoPago` (id, nombre, descripción, fechas de creación/actualización).
*   **Funcionalidades API:** Gestión de los diferentes métodos de pago aceptados.

### 10. Gestión de Pagos (`Pago`)

*   **Entidad:** `Pago` (id, pedido, método de pago, monto, fecha, estado, fechas de creación/actualización).
*   **Funcionalidades API:** Registro y seguimiento de los pagos realizados por los pedidos.

### 11. Gestión de Proveedores (`Proveedor`)

*   **Entidad:** `Proveedor` (id, nombre, contacto, dirección, teléfono, email, fechas de creación/actualización).
*   **Funcionalidades API:** Gestión de la información de los proveedores.

### 12. Gestión de Reservas (`Reserva`)

*   **Entidad:** `Reserva` (id, cliente, mesa, fecha/hora, número de personas, estado, fechas de creación/actualización).
*   **Funcionalidades API:** Gestión de reservas de mesas.

### 13. Gestión de Usuarios (`Usuario`)

*   **Entidad:** `Usuario` (id, nombre de usuario, contraseña, roles, fechas de creación/actualización).
*   **Funcionalidades API:** Gestión de usuarios del sistema, incluyendo roles y permisos (integrado con Spring Security).

### Otros Componentes

*   **`DataLoader`:** Componente para la carga inicial de datos.
*   **`SecurityConfig`:** Configuración de seguridad de Spring Security.
*   **`WebConfig`:** Configuración web general.
*   **`FileStorageService`:** Servicio para la gestión de almacenamiento de archivos (e.g., imágenes de productos).
*   **`GlobalExceptionHandler`:** Manejo centralizado de excepciones para la API.
*   **`DocumentValidator`:** Validadores personalizados para documentos.

## Cómo Ejecutar el Proyecto

1.  **Requisitos:**
    *   Java 21
    *   Maven
    *   PostgreSQL
2.  **Configuración de la Base de Datos:**
    *   Crear una base de datos PostgreSQL.
    *   Configurar las credenciales de la base de datos en `src/main/resources/application.properties`.
3.  **Compilar y Ejecutar:**
    ```bash
    ./mvnw clean install
    ./mvnw spring-boot:run
    ```

## Documentación de la API

Una vez que la aplicación esté en ejecución, la documentación interactiva de la API (Swagger UI) estará disponible en:
`http://localhost:8080/swagger-ui.html` (o el puerto configurado en `application.properties`).
