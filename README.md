# 🏪 Sistema Mozaico - Gestión de Restaurantes

## 📖 Descripción
Sistema completo de gestión para restaurantes con configuración empresarial flexible, desde negocios informales hasta facturación electrónica completa según normativa SUNAT 2024.

## ✨ Características Principales

### 🏢 **Sistema de Empresa Flexible**
- ✅ **Negocio Informal**: Opera sin RUC, emite tickets internos sin IGV
- ✅ **Negocio Formal**: Con RUC, boletas manuales con IGV
- ✅ **Facturación Electrónica**: Comprobantes electrónicos SUNAT
- ✅ **Migración Gradual**: Evolución sin pérdida de datos

### 🎯 **Configuración Actual**
```yaml
Empresa: "Restaurante Mozaico"
Tipo: Negocio Informal
IGV: No aplica
Comprobantes: Solo tickets internos
```

### 🧮 **Módulos CRUD Completos con Búsqueda Avanzada:**

*   **🏢 Sistema de Empresa:** Configuración empresarial flexible con validación automática de IGV
*   **📋 Categorías:** Gestión de categorías de productos organizadas
*   **👥 Clientes:** Gestión integral de información de clientes
*   **🪑 Mesas:** Gestión de mesas con control de estados y ocupación
*   **🛍️ Productos:** Catálogo completo con imágenes y precios dinámicos
*   **💳 Métodos de Pago:** Gestión de formas de pago (efectivo, tarjetas, transferencias)
*   **🏭 Proveedores:** Gestión de proveedores con datos completos de contacto
*   **📦 Inventario:** Control de stock con alertas automáticas y costos
*   **🍽️ Pedidos:** Gestión integral con cálculo automático de totales e IGV
*   **📝 Detalles de Pedido:** Gestión de ítems individuales con estados de preparación
*   **💰 Pagos:** Procesamiento de pagos con validación de empresa
*   **🛒 Compras/Suministros:** Gestión de adquisiciones a proveedores
*   **📋 Detalles de Compras:** Control detallado de productos adquiridos
*   **📅 Reservas:** Sistema de reservas con validación de disponibilidad
*   **🍴 Menús Especiales:** Combos y ofertas con precios especiales

### 🔧 **Lógica de Negocio Integrada:**

*   **🏢 Validación Empresarial Automática:**
    *   **Configuración Dinámica:** Consulta automática de capacidades de emisión
    *   **Cálculo de IGV:** Aplicación automática según configuración empresarial
    *   **Mensajes al Cliente:** Notificación clara sobre tipos de comprobante disponibles
    *   **Validaciones de Coherencia:** Detección automática de inconsistencias de configuración

*   **🍽️ Flujo de Pedidos Integrado:**
    *   **Creación de Pedidos:** Vinculación de clientes, mesas y usuarios (empleados)
    *   **Cálculo Dinámico de Totales:** Recálculo automático considerando configuración de IGV empresarial
    *   **Actualización de Inventario:** Ajuste automático de stock por ventas
    *   **Control de Estado de Mesa:** Cambio automático entre DISPONIBLE/OCUPADA/RESERVADA

*   **💰 Integración de Pagos:**
    *   **Validación de Empresa:** Verificación de capacidades antes de procesar pagos
    *   **Actualización de Estado:** Cambio automático de estado del pedido al completar pago
    *   **Múltiples Métodos:** Soporte para efectivo, tarjetas y transferencias digitales

*   **📦 Gestión de Inventario Avanzada:**
    *   **Actualización por Compras:** Incremento automático de stock al recibir mercancía
    *   **Alertas Inteligentes:** Notificaciones de stock bajo con recomendaciones
    *   **Control de Costos:** Seguimiento de costos unitarios y márgenes

*   **🛒 Gestión de Compras Integrada:**
    *   **Flujo Completo:** Desde solicitud hasta recepción de mercancía
    *   **Actualización Automática:** Incremento de inventario al marcar como RECIBIDA
    *   **Control de Proveedores:** Gestión integral de cadena de suministro

*   **📅 Gestión de Reservas:**
    *   **Validación de Disponibilidad:** Prevención de solapamientos automática
    *   **Estados Dinámicos:** Control de confirmación, cancelación y finalización

---

## 🚀 Inicio Rápido

### **Prerrequisitos**
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- Git

### **Instalación**
```bash
# Clonar repositorio
git clone https://github.com/tu-usuario/mozaico.git
cd mozaico

# Configurar base de datos en application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/mozaico
spring.datasource.username=tu_usuario
spring.datasource.password=tu_password

# Ejecutar aplicación
mvn spring-boot:run
```

### **Primera Ejecución**
Al iniciar, el sistema carga automáticamente datos de prueba:
```
✅ Carga de datos completada exitosamente!
📊 Resumen de datos cargados:
   - 1 Empresa configurada (INFORMAL - Sin RUC)
   - 5 Categorías de productos
   - 16 Productos en catálogo
   - 10 Mesas distribuidas
   - 8 Empleados registrados
   - 6 Métodos de pago
🎟️ CONFIGURACIÓN: Negocio informal - Solo emite tickets sin IGV
```

---

## 🎮 **API Endpoints**

### **Validación de Empresa**
```http
GET /api/v1/empresa/validacion/igv           # Validación completa
GET /api/v1/empresa/validacion/aplica-igv    # Verificación rápida IGV
GET /api/v1/empresa/validacion/mensaje-cliente # Mensaje para cliente
```

### **Ejemplo de Respuesta**
```json
{
  "aplicaIgv": false,
  "tipoOperacion": "TICKET_SIMPLE",
  "mensajeCliente": "🎟️ Esta empresa opera como negocio informal. Los comprobantes emitidos son tickets internos sin valor tributario y NO incluyen IGV.",
  "comprobantesPermitidos": ["Ticket interno sin valor tributario"],
  "limitaciones": [
    "Solo puede emitir tickets internos sin valor tributario",
    "No puede incluir IGV en los comprobantes"
  ]
}
```

---

## 📱 **Integración Frontend**

### **Verificación de Configuración**
```javascript
// Obtener configuración al cargar
const validacion = await fetch('/api/v1/empresa/validacion/igv')
  .then(r => r.json());

// Configurar interfaz según capacidades
configurarCalculadora(validacion.aplicaIgv, validacion.porcentajeIgv);
mostrarMensajeCliente(validacion.mensajeCliente);
```

### **Calculadora de Precios**
```javascript
class PriceCalculator {
  async initialize() {
    const config = await fetch('/api/v1/empresa/validacion/igv').then(r => r.json());
    this.aplicaIgv = config.aplicaIgv;
    this.porcentajeIgv = config.porcentajeIgv;
  }
  
  calculate(subtotal) {
    const igv = this.aplicaIgv ? subtotal * (this.porcentajeIgv / 100) : 0;
    return { subtotal, igv, total: subtotal + igv };
  }
}
```

---

## 📚 **Documentación**

### **Documentos Disponibles**
- 📋 **[CHANGELOG.md](CHANGELOG.md)** - Historial de cambios y nuevas funcionalidades
- 🏢 **[EMPRESA_SISTEMA.md](docs/EMPRESA_SISTEMA.md)** - Documentación técnica del sistema de empresa
- 📚 **[API_REFERENCE.md](docs/API_REFERENCE.md)** - Referencia completa de la API

---

## 🚧 Funcionalidades Pendientes y Posibles Mejoras

### **🔒 Prioridad Alta:**
1.  **Roles y Permisos de Usuario (RBAC):** Sistema robusto de control de acceso por roles
2.  **Flujo de Cocina/Preparación (KDS):** Panel para gestión de pedidos en cocina
3.  **Dashboard Empresarial:** Interfaz para cambiar configuración de empresa (informal ↔ formal)

### **⚡ Prioridad Media:**
4.  **Alertas de Inventario Proactivas:** Notificaciones por email/SMS de stock bajo
5.  **Facturación Electrónica Real:** Integración completa con proveedores OSE/SUNAT
6.  **Reportes Avanzados:** Dashboard con métricas de ventas y rendimiento

### **📈 Prioridad Baja (Futuro):**
7.  **Promociones y Descuentos Avanzados:** Motor de reglas para ofertas complejas
8.  **Reabastecimiento Automático:** Sugerencias automáticas de compra
9.  **Gestión de Turnos:** Planificación de horarios de empleados
10. **App Móvil:** Aplicación para tablets y smartphones

---

## 🔧 **Configuración Avanzada**

### **Tipos de Empresa Soportados**
```java
TICKET_SIMPLE          // Solo tickets internos (actual)
BOLETA_MANUAL          // Boletas manuales con IGV
FACTURACION_ELECTRONICA // Comprobantes electrónicos SUNAT
MIXTO                  // Combinación de tipos
```

### **Migración de Configuración**
```java
// Ejemplo: De informal a formal
empresa.setAplicaIgv(true);
empresa.setTipoOperacion(TipoOperacion.BOLETA_MANUAL);
// Crear DatosFacturacion con RUC
```

---

## 🧪 **Testing**

### **Ejecutar Tests**
```bash
# Tests unitarios
mvn test

# Tests de integración
mvn test -Dtest=**/*IntegrationTest

# Verificar API
curl http://localhost:8080/api/v1/empresa/validacion/igv
```

---

## 🤝 **Contribuir**

1. Fork del repositorio
2. Crear rama feature: `git checkout -b feature/nueva-funcionalidad`
3. Commits descriptivos
4. Push y crear Pull Request

---

## 📞 **Soporte**

- 📧 Email: soporte@mozaico.com
- 📖 [Documentación Completa](docs/)
- 🐛 [Reportar Bugs](issues/)

---

**🎯 ¡Mozaico - Gestión de restaurantes desde informal hasta empresarial!**
