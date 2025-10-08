# 🏢 API de Gestión de Empresa

## 📋 Descripción

Endpoints para gestionar la información de la empresa a la que pertenece el usuario autenticado.

---

## 🔐 Control de Acceso por Roles

| Acción | Roles Permitidos | Endpoint |
|--------|------------------|----------|
| **Ver información** | Todos los usuarios autenticados | `GET /api/v1/empresa` |
| **Actualizar datos** | ADMIN, SUPER_ADMIN | `PUT /api/v1/empresa` |
| **Actualizar logo** | ADMIN, SUPER_ADMIN | `PUT /api/v1/empresa/logo` |
| **Cambiar slug** | Todos los usuarios autenticados | `PUT /api/v1/empresa/slug` |
| **Ver estadísticas** | ADMIN, SUPER_ADMIN | `GET /api/v1/empresa/estadisticas` |
| **Activar/Desactivar** | SUPER_ADMIN | `PATCH /api/v1/empresa/estado` |

---

## 📡 Endpoints Disponibles

### 1. **Obtener Información de Empresa**

```
GET /api/v1/empresa
```

**Autenticación:** ✅ Requerida (JWT)

**Permisos:** Todos los usuarios autenticados pueden ver su empresa

**Respuesta:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Información de empresa obtenida",
  "data": {
    "idEmpresa": 1,
    "nombre": "Restaurante Mozaico",
    "slug": "restaurante-mozaico",
    "descripcion": "Restaurante familiar especializado...",
    "direccion": "Jr. Los Pinos 456, Lima",
    "telefono": "01-234-5678",
    "email": "contacto@restaurantemozaico.com",
    "paginaWeb": "www.restaurantemozaico.com",
    "logoUrl": "/uploads/images/logo-mozaico.png",
    "activa": true,
    "tipoOperacion": "TICKET_SIMPLE",
    "aplicaIgv": false,
    "porcentajeIgv": 18.00,
    "moneda": "PEN",
    "prefijoTicket": "MOZ",
    "correlativoTicket": 1,
    "fechaCreacion": "2024-01-15T10:30:00",
    "fechaActualizacion": "2024-01-20T15:45:00"
  }
}
```

**Ejemplo:**
```bash
curl -X GET "http://localhost:8091/api/v1/empresa" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 2. **Actualizar Información de Empresa**

```
PUT /api/v1/empresa
```

**Autenticación:** ✅ Requerida (JWT)

**Permisos:** ⚠️ Solo ADMIN y SUPER_ADMIN

**Body:**
```json
{
  "nombre": "Restaurante Mozaico",
  "descripcion": "Restaurante familiar especializado en comida criolla y fusión",
  "direccion": "Jr. Los Pinos 456, Distrito de San Miguel, Lima",
  "telefono": "01-234-5678",
  "email": "contacto@restaurantemozaico.com",
  "paginaWeb": "www.restaurantemozaico.com",
  "tipoOperacion": "TICKET_SIMPLE",
  "aplicaIgv": false,
  "porcentajeIgv": 18.00,
  "moneda": "PEN",
  "prefijoTicket": "MOZ"
}
```

**Campos:**
- `nombre` (string, required): Nombre de la empresa
- `descripcion` (string, optional): Descripción de la empresa
- `direccion` (string, optional): Dirección física
- `telefono` (string, optional): Teléfono de contacto
- `email` (string, optional): Email de contacto
- `paginaWeb` (string, optional): Sitio web
- `tipoOperacion` (enum, required): `TICKET_SIMPLE` o `FACTURA_ELECTRONICA`
- `aplicaIgv` (boolean, required): Si la empresa aplica IGV (18%)
- `porcentajeIgv` (decimal, optional): Porcentaje de IGV (por defecto 18.00)
- `moneda` (string, optional): Código de moneda (por defecto "PEN")
- `prefijoTicket` (string, optional): Prefijo para tickets

**Respuesta:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Empresa actualizada exitosamente",
  "data": {
    "idEmpresa": 1,
    "nombre": "Restaurante Mozaico",
    ...
  }
}
```

**Ejemplo:**
```bash
curl -X PUT "http://localhost:8091/api/v1/empresa" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Restaurante Mozaico",
    "descripcion": "Restaurante familiar",
    "direccion": "Jr. Los Pinos 456, Lima",
    "telefono": "01-234-5678",
    "email": "contacto@restaurantemozaico.com",
    "paginaWeb": "www.restaurantemozaico.com",
    "tipoOperacion": "TICKET_SIMPLE",
    "aplicaIgv": false,
    "porcentajeIgv": 18.00,
    "moneda": "PEN",
    "prefijoTicket": "MOZ"
  }'
```

---

### 3. **Actualizar Logo de Empresa**

```
PUT /api/v1/empresa/logo
```

**Autenticación:** ✅ Requerida (JWT)

**Permisos:** ⚠️ Solo ADMIN y SUPER_ADMIN

**Content-Type:** `multipart/form-data`

**Form Data:**
- `file` (file, required): Archivo de imagen del logo (PNG, JPG)

**Respuesta:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Logo actualizado exitosamente",
  "data": {
    "idEmpresa": 1,
    "logoUrl": "/uploads/images/products/logo-123456.png",
    ...
  }
}
```

**Ejemplo:**
```bash
curl -X PUT "http://localhost:8091/api/v1/empresa/logo" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN" \
  -F "file=@/path/to/logo.png"
```

---

### 4. **Cambiar Estado de Empresa (Activar/Desactivar)**

```
PATCH /api/v1/empresa/estado?activa=true
```

**Autenticación:** ✅ Requerida (JWT)

**Permisos:** ⚠️ Solo SUPER_ADMIN

**Query Params:**
- `activa` (boolean, required): `true` para activar, `false` para desactivar

**Respuesta:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Estado de empresa actualizado",
  "data": {
    "idEmpresa": 1,
    "activa": true,
    ...
  }
}
```

**Ejemplo:**
```bash
# Desactivar empresa
curl -X PATCH "http://localhost:8091/api/v1/empresa/estado?activa=false" \
  -H "Authorization: Bearer SUPER_ADMIN_JWT_TOKEN"

# Activar empresa
curl -X PATCH "http://localhost:8091/api/v1/empresa/estado?activa=true" \
  -H "Authorization: Bearer SUPER_ADMIN_JWT_TOKEN"
```

---

### 5. **Obtener Estadísticas de Empresa**

```
GET /api/v1/empresa/estadisticas
```

**Autenticación:** ✅ Requerida (JWT)

**Permisos:** ⚠️ Solo ADMIN y SUPER_ADMIN

**Respuesta:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Estadísticas obtenidas",
  "data": {
    "totalProductos": 16,
    "totalClientes": 8,
    "totalEmpleados": 9,
    "totalPedidos": 7,
    "empresa": {
      "idEmpresa": 1,
      "nombre": "Restaurante Mozaico",
      ...
    }
  }
}
```

**Ejemplo:**
```bash
curl -X GET "http://localhost:8091/api/v1/empresa/estadisticas" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

---

## 🏷️ Gestión de Slug (Endpoints Adicionales)

Consultar [`GESTION_SLUGS_EMPRESA.md`](./GESTION_SLUGS_EMPRESA.md) para:
- `PUT /api/v1/empresa/slug` - Cambiar slug
- `POST /api/v1/empresa/slug/generar` - Generar slug automático
- `GET /api/v1/empresa/slug/disponible` - Verificar disponibilidad

---

## 🔒 Seguridad y Validaciones

### Validaciones de Datos

**Nombre de Empresa:**
- ✅ Obligatorio
- No puede estar vacío

**Email:**
- ✅ Debe tener formato válido de email

**Tipo de Operación:**
- ✅ Obligatorio
- Valores: `TICKET_SIMPLE` | `FACTURA_ELECTRONICA`

**Aplica IGV:**
- ✅ Obligatorio
- `true` o `false`

### Control de Permisos

El sistema verifica automáticamente:
1. Usuario autenticado con JWT válido
2. Usuario pertenece a una empresa
3. Usuario tiene el rol necesario para la operación

**Errores comunes:**

```json
{
  "status": "ERROR",
  "code": 403,
  "message": "Acceso denegado. Se requiere rol ADMIN o SUPER_ADMIN"
}
```

---

## 💡 Casos de Uso

### Caso 1: Usuario Regular Ve su Empresa

```javascript
// Cualquier usuario puede ver su empresa
const verMiEmpresa = async (token) => {
  const response = await fetch('http://localhost:8091/api/v1/empresa', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const data = await response.json();
  console.log('Mi empresa:', data.data);
};
```

### Caso 2: Admin Actualiza Información

```javascript
// Solo ADMIN o SUPER_ADMIN
const actualizarEmpresa = async (adminToken, datosEmpresa) => {
  const response = await fetch('http://localhost:8091/api/v1/empresa', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${adminToken}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(datosEmpresa)
  });

  const result = await response.json();

  if (result.status === 'SUCCESS') {
    alert('Empresa actualizada');
  } else if (response.status === 403) {
    alert('No tienes permisos. Requieres rol ADMIN');
  }
};
```

### Caso 3: Super Admin Desactiva Empresa

```javascript
// Solo SUPER_ADMIN
const desactivarEmpresa = async (superAdminToken) => {
  const response = await fetch(
    'http://localhost:8091/api/v1/empresa/estado?activa=false',
    {
      method: 'PATCH',
      headers: { 'Authorization': `Bearer ${superAdminToken}` }
    }
  );

  if (response.status === 403) {
    alert('Solo SUPER_ADMIN puede cambiar el estado');
  }
};
```

---

## 📱 Frontend Vue.js - Componente de Gestión

```vue
<template>
  <div class="empresa-config">
    <h2>Configuración de Empresa</h2>

    <!-- Ver Información (Todos) -->
    <div class="empresa-info">
      <h3>{{ empresa.nombre }}</h3>
      <p>Slug: <code>{{ empresa.slug }}</code></p>
      <p>Email: {{ empresa.email }}</p>
      <p>Estado: <span :class="empresa.activa ? 'activa' : 'inactiva'">
        {{ empresa.activa ? 'ACTIVA' : 'INACTIVA' }}
      </span></p>
    </div>

    <!-- Editar (Solo ADMIN/SUPER_ADMIN) -->
    <div v-if="esAdmin" class="empresa-form">
      <h3>Editar Información</h3>
      <form @submit.prevent="guardarCambios">
        <input v-model="form.nombre" placeholder="Nombre" required>
        <textarea v-model="form.descripcion" placeholder="Descripción"></textarea>
        <input v-model="form.direccion" placeholder="Dirección">
        <input v-model="form.telefono" placeholder="Teléfono">
        <input v-model="form.email" type="email" placeholder="Email">

        <select v-model="form.tipoOperacion" required>
          <option value="TICKET_SIMPLE">Ticket Simple</option>
          <option value="FACTURA_ELECTRONICA">Factura Electrónica</option>
        </select>

        <label>
          <input type="checkbox" v-model="form.aplicaIgv">
          Aplica IGV (18%)
        </label>

        <button type="submit">💾 Guardar Cambios</button>
      </form>

      <!-- Upload Logo -->
      <div class="logo-upload">
        <h4>Logo de Empresa</h4>
        <img v-if="empresa.logoUrl" :src="empresa.logoUrl" alt="Logo">
        <input type="file" @change="subirLogo" accept="image/*">
      </div>
    </div>

    <!-- Estadísticas (Solo ADMIN/SUPER_ADMIN) -->
    <div v-if="esAdmin" class="estadisticas">
      <h3>Estadísticas</h3>
      <div class="stats-grid">
        <div class="stat">
          <span class="number">{{ stats.totalProductos }}</span>
          <span class="label">Productos</span>
        </div>
        <div class="stat">
          <span class="number">{{ stats.totalClientes }}</span>
          <span class="label">Clientes</span>
        </div>
        <div class="stat">
          <span class="number">{{ stats.totalEmpleados }}</span>
          <span class="label">Empleados</span>
        </div>
        <div class="stat">
          <span class="number">{{ stats.totalPedidos }}</span>
          <span class="label">Pedidos</span>
        </div>
      </div>
    </div>

    <!-- Cambiar Estado (Solo SUPER_ADMIN) -->
    <div v-if="esSuperAdmin" class="admin-actions">
      <button
        @click="cambiarEstado(!empresa.activa)"
        :class="empresa.activa ? 'btn-danger' : 'btn-success'"
      >
        {{ empresa.activa ? '🔴 Desactivar Empresa' : '✅ Activar Empresa' }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      empresa: {},
      stats: {},
      form: {},
      userRole: ''
    }
  },

  computed: {
    esAdmin() {
      return ['ADMIN', 'SUPER_ADMIN'].includes(this.userRole);
    },
    esSuperAdmin() {
      return this.userRole === 'SUPER_ADMIN';
    }
  },

  async mounted() {
    await this.cargarEmpresa();
    if (this.esAdmin) {
      await this.cargarEstadisticas();
    }
    this.userRole = this.$store.state.user.tipoUsuario; // Desde Vuex
  },

  methods: {
    async cargarEmpresa() {
      const response = await this.$axios.get('/api/v1/empresa');
      this.empresa = response.data.data;
      this.form = { ...this.empresa }; // Copiar para formulario
    },

    async cargarEstadisticas() {
      try {
        const response = await this.$axios.get('/api/v1/empresa/estadisticas');
        this.stats = response.data.data;
      } catch (error) {
        if (error.response?.status === 403) {
          console.log('No tienes permisos para ver estadísticas');
        }
      }
    },

    async guardarCambios() {
      try {
        await this.$axios.put('/api/v1/empresa', this.form);
        this.$notify({ type: 'success', message: 'Empresa actualizada' });
        await this.cargarEmpresa();
      } catch (error) {
        if (error.response?.status === 403) {
          this.$notify({ type: 'error', message: 'No tienes permisos (requiere ADMIN)' });
        } else {
          this.$notify({ type: 'error', message: 'Error al actualizar' });
        }
      }
    },

    async subirLogo(event) {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append('file', file);

      try {
        await this.$axios.put('/api/v1/empresa/logo', formData);
        this.$notify({ type: 'success', message: 'Logo actualizado' });
        await this.cargarEmpresa();
      } catch (error) {
        this.$notify({ type: 'error', message: 'Error al subir logo' });
      }
    },

    async cambiarEstado(activa) {
      const confirmMsg = activa
        ? '¿Activar empresa?'
        : '¿Desactivar empresa? Los usuarios no podrán operar';

      if (!confirm(confirmMsg)) return;

      try {
        await this.$axios.patch(`/api/v1/empresa/estado?activa=${activa}`);
        this.$notify({
          type: 'success',
          message: activa ? 'Empresa activada' : 'Empresa desactivada'
        });
        await this.cargarEmpresa();
      } catch (error) {
        if (error.response?.status === 403) {
          this.$notify({
            type: 'error',
            message: 'Solo SUPER_ADMIN puede cambiar el estado'
          });
        }
      }
    }
  }
}
</script>
```

---

## 📝 Notas Importantes

### ⚠️ Cambiar Nombre de Empresa

Si cambias el nombre de la empresa, considera:
1. El **slug NO se actualiza automáticamente**
2. Usa `POST /api/v1/empresa/slug/generar` para obtener nuevo slug sugerido
3. Actualiza manualmente con `PUT /api/v1/empresa/slug`

### ⚠️ Desactivar Empresa

Cuando se desactiva una empresa (`activa = false`):
- Los usuarios NO podrán autenticarse
- Los endpoints públicos (carta, QR) seguirán funcionando
- Solo SUPER_ADMIN puede reactivarla

### 🔐 Jerarquía de Roles

```
SUPER_ADMIN
  ├─ Puede TODO
  ├─ Cambiar estado de empresa ✅
  └─ Ver/Editar empresa ✅

ADMIN
  ├─ Ver/Editar empresa ✅
  ├─ Ver estadísticas ✅
  └─ NO puede cambiar estado ❌

OTROS ROLES (CAJERO, MESERO, COCINERO)
  ├─ Ver empresa ✅
  └─ NO pueden editar ❌
```

---

## 🚀 Endpoints Relacionados

- **Gestión de Slugs:** [`GESTION_SLUGS_EMPRESA.md`](./GESTION_SLUGS_EMPRESA.md)
- **Carta Pública:** [`API_CARTA_PUBLICA.md`](./API_CARTA_PUBLICA.md)
- **QR Codes:** [`QR_CARTA_DIGITAL.md`](./QR_CARTA_DIGITAL.md)
- **Autenticación:** [`AUTENTICACION_JWT_Y_SEGURIDAD.md`](./AUTENTICACION_JWT_Y_SEGURIDAD.md)
