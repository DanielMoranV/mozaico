# 🏷️ Gestión de Slugs de Empresa

## 📋 Descripción

Sistema para gestionar el **slug único** de cada empresa, que se utiliza en las URLs públicas de la carta digital.

---

## ¿Qué es un Slug?

Un **slug** es un identificador único, amigable y seguro para URLs.

### Ejemplos

| Nombre de Empresa | Slug Generado | URL Pública |
|-------------------|---------------|-------------|
| Restaurante Mozaico | `restaurante-mozaico` | `/public/restaurante-mozaico/carta` |
| Pizzería Mamá Mía | `pizzeria-mama-mia` | `/public/pizzeria-mama-mia/carta` |
| Café Central #1 | `cafe-central-1` | `/public/cafe-central-1/carta` |
| El Buen Sabor 2024 | `el-buen-sabor-2024` | `/public/el-buen-sabor-2024/carta` |

---

## 🔧 Generación Automática

### Reglas de Generación

El slug se genera automáticamente del nombre de la empresa siguiendo estas reglas:

1. **Convertir a minúsculas**
   ```
   "Restaurante Mozaico" → "restaurante mozaico"
   ```

2. **Eliminar acentos y caracteres especiales**
   ```
   "Pizzería Mamá Mía" → "pizzeria mama mia"
   ```

3. **Reemplazar espacios con guiones**
   ```
   "restaurante mozaico" → "restaurante-mozaico"
   ```

4. **Eliminar caracteres no permitidos**
   ```
   "Café #1!" → "cafe-1"
   ```

5. **Sin guiones al inicio/final ni consecutivos**
   ```
   "---mi-empresa---" → "mi-empresa"
   ```

### Formato Válido

✅ **Permitido:**
- Letras minúsculas (a-z)
- Números (0-9)
- Guiones simples (-) entre palabras

❌ **No permitido:**
- Mayúsculas
- Espacios
- Caracteres especiales (@, #, $, etc.)
- Acentos (á, é, í, ó, ú)
- Guiones al inicio o final
- Guiones consecutivos (---)

---

## 🚀 Endpoints Disponibles

### 1. **Obtener Información de Empresa**

```
GET /api/v1/empresa
```

**Autenticación:** ✅ Requerida (JWT)

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
    "descripcion": "Restaurante familiar...",
    "telefono": "01-234-5678",
    "email": "contacto@restaurantemozaico.com"
  }
}
```

**Ejemplo:**
```bash
curl -X GET "http://localhost:8091/api/v1/empresa" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 2. **Actualizar Slug de Empresa**

```
PUT /api/v1/empresa/slug
```

**Autenticación:** ✅ Requerida (JWT)

**Body:**
```json
{
  "slug": "mi-nuevo-slug"
}
```

**Validaciones:**
- El slug debe tener formato válido (solo minúsculas, números y guiones)
- No puede estar en uso por otra empresa
- Se actualiza en tiempo real

**Respuesta exitosa:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Slug actualizado exitosamente. URL pública: /public/mi-nuevo-slug/carta",
  "data": {
    "idEmpresa": 1,
    "nombre": "Restaurante Mozaico",
    "slug": "mi-nuevo-slug",
    ...
  }
}
```

**Errores posibles:**
```json
{
  "status": "ERROR",
  "code": 400,
  "message": "El slug 'otro-restaurant' ya está en uso por otra empresa"
}
```

**Ejemplo:**
```bash
curl -X PUT "http://localhost:8091/api/v1/empresa/slug" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"slug": "restaurante-mozaico-oficial"}'
```

---

### 3. **Generar Slug Automático**

```
POST /api/v1/empresa/slug/generar
```

**Autenticación:** ✅ Requerida (JWT)

**Descripción:** Genera un slug basado en el nombre actual de la empresa

**Respuesta:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Slug generado: 'restaurante-mozaico'. Usa PUT /api/v1/empresa/slug para guardarlo.",
  "data": "restaurante-mozaico"
}
```

Si el slug ya existe, agrega un número:
```json
{
  "data": "restaurante-mozaico-2"
}
```

**Ejemplo:**
```bash
curl -X POST "http://localhost:8091/api/v1/empresa/slug/generar" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

### 4. **Verificar Disponibilidad de Slug**

```
GET /api/v1/empresa/slug/disponible?slug=mi-slug
```

**Autenticación:** ✅ Requerida (JWT)

**Parámetros:**
- `slug` (query, required): Slug a verificar

**Respuesta - Disponible:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Slug disponible",
  "data": true
}
```

**Respuesta - No disponible:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Slug no disponible (ya está en uso)",
  "data": false
}
```

**Respuesta - Formato inválido:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Formato inválido. Solo letras minúsculas, números y guiones.",
  "data": false
}
```

**Ejemplo:**
```bash
curl -X GET "http://localhost:8091/api/v1/empresa/slug/disponible?slug=restaurante-test" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 💡 Flujo de Trabajo Recomendado

### Cambiar Slug de tu Empresa

```
1. Login
   POST /api/v1/auth/login

2. Ver slug actual
   GET /api/v1/empresa

3. Generar sugerencia automática (opcional)
   POST /api/v1/empresa/slug/generar

4. Verificar disponibilidad del nuevo slug
   GET /api/v1/empresa/slug/disponible?slug=nuevo-slug

5. Actualizar slug
   PUT /api/v1/empresa/slug
   Body: {"slug": "nuevo-slug"}

6. Probar URL pública
   GET /api/v1/productos/public/nuevo-slug/carta
```

---

## 🎨 Ejemplos de Uso

### JavaScript/Fetch

```javascript
// 1. Obtener información de empresa
const obtenerEmpresa = async (token) => {
  const response = await fetch('http://localhost:8091/api/v1/empresa', {
    headers: { 'Authorization': `Bearer ${token}` }
  });
  const data = await response.json();
  console.log('Slug actual:', data.data.slug);
  return data.data;
};

// 2. Verificar disponibilidad
const verificarSlug = async (token, slug) => {
  const response = await fetch(
    `http://localhost:8091/api/v1/empresa/slug/disponible?slug=${slug}`,
    { headers: { 'Authorization': `Bearer ${token}` } }
  );
  const data = await response.json();
  return data.data; // true o false
};

// 3. Actualizar slug
const actualizarSlug = async (token, nuevoSlug) => {
  const disponible = await verificarSlug(token, nuevoSlug);

  if (!disponible) {
    alert('Slug no disponible');
    return;
  }

  const response = await fetch('http://localhost:8091/api/v1/empresa/slug', {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ slug: nuevoSlug })
  });

  const data = await response.json();
  if (data.status === 'SUCCESS') {
    alert('Slug actualizado exitosamente!');
    console.log('Nueva URL:', `/public/${nuevoSlug}/carta`);
  }
};
```

### Vue.js Component

```vue
<template>
  <div class="slug-manager">
    <h2>Gestionar Slug de Empresa</h2>

    <div class="current-slug">
      <label>Slug Actual:</label>
      <span>{{ empresa.slug }}</span>
      <small>URL: /public/{{ empresa.slug }}/carta</small>
    </div>

    <div class="slug-form">
      <label>Nuevo Slug:</label>
      <input
        v-model="nuevoSlug"
        @input="verificarDisponibilidad"
        placeholder="mi-nuevo-slug"
      >

      <div v-if="verificando" class="status">
        ⏳ Verificando...
      </div>
      <div v-else-if="nuevoSlug && slugDisponible" class="status success">
        ✅ Slug disponible
      </div>
      <div v-else-if="nuevoSlug && !slugDisponible" class="status error">
        ❌ Slug no disponible
      </div>

      <button
        @click="generarAutomatico"
        class="btn-secondary"
      >
        🔄 Generar Automático
      </button>

      <button
        @click="guardarSlug"
        :disabled="!slugDisponible || !nuevoSlug"
        class="btn-primary"
      >
        💾 Guardar Slug
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      empresa: {},
      nuevoSlug: '',
      slugDisponible: false,
      verificando: false,
      timeoutId: null
    }
  },

  async mounted() {
    await this.cargarEmpresa();
  },

  methods: {
    async cargarEmpresa() {
      const response = await this.$axios.get('/api/v1/empresa');
      this.empresa = response.data.data;
      this.nuevoSlug = this.empresa.slug;
    },

    verificarDisponibilidad() {
      // Debounce
      clearTimeout(this.timeoutId);

      if (!this.nuevoSlug) return;

      this.verificando = true;
      this.timeoutId = setTimeout(async () => {
        const response = await this.$axios.get(
          `/api/v1/empresa/slug/disponible?slug=${this.nuevoSlug}`
        );
        this.slugDisponible = response.data.data;
        this.verificando = false;
      }, 500);
    },

    async generarAutomatico() {
      const response = await this.$axios.post('/api/v1/empresa/slug/generar');
      this.nuevoSlug = response.data.data;
      await this.verificarDisponibilidad();
    },

    async guardarSlug() {
      try {
        await this.$axios.put('/api/v1/empresa/slug', {
          slug: this.nuevoSlug
        });

        this.$notify({
          type: 'success',
          message: 'Slug actualizado exitosamente'
        });

        await this.cargarEmpresa();
      } catch (error) {
        this.$notify({
          type: 'error',
          message: error.response.data.message || 'Error al actualizar slug'
        });
      }
    }
  }
}
</script>

<style scoped>
.slug-manager {
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.current-slug {
  margin-bottom: 20px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
}

.slug-form input {
  width: 100%;
  padding: 10px;
  margin: 10px 0;
  border: 2px solid #ddd;
  border-radius: 4px;
}

.status {
  padding: 10px;
  margin: 10px 0;
  border-radius: 4px;
}

.status.success {
  background: #d4edda;
  color: #155724;
}

.status.error {
  background: #f8d7da;
  color: #721c24;
}

button {
  margin: 5px;
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
```

---

## 🔒 Seguridad

### ✅ Garantías de Seguridad

1. **Slugs únicos**: No pueden existir dos empresas con el mismo slug
2. **Validación de formato**: Solo caracteres permitidos
3. **Autenticación requerida**: Solo el propietario puede cambiar su slug
4. **Verificación en tiempo real**: Antes de guardar, se verifica disponibilidad

### ⚠️ Importante

- **Cambiar el slug invalida QR codes anteriores**: Si cambias el slug, los QR codes impresos dejarán de funcionar
- **Planifica bien tu slug**: Usa un slug profesional y memorable
- **No cambies frecuentemente**: El slug debe ser estable

---

## 📊 Base de Datos

### Estructura

```sql
ALTER TABLE empresas ADD COLUMN slug VARCHAR(100);
ALTER TABLE empresas ADD CONSTRAINT uk_empresas_slug UNIQUE (slug);
CREATE INDEX idx_empresas_slug ON empresas(slug);
```

### Migración Automática

Si la empresa ya existe sin slug, se genera automáticamente:

```sql
UPDATE empresas
SET slug = LOWER(
    REGEXP_REPLACE(
        REGEXP_REPLACE(nombre, '[^a-zA-Z0-9\s-]', '', 'g'),
        '\s+', '-', 'g'
    )
)
WHERE slug IS NULL;
```

---

## 🛠️ Utilidad SlugGenerator

```java
import com.djasoft.mozaico.infrastructure.utils.SlugGenerator;

// Generar slug desde texto
String slug = SlugGenerator.generateSlug("Restaurante Mozaico");
// Resultado: "restaurante-mozaico"

// Validar formato
boolean valido = SlugGenerator.isValidSlug("mi-slug");
// true

// Generar slug único (con número si existe)
String slugUnico = SlugGenerator.generateUniqueSlug(
    "restaurante-mozaico",
    empresaRepository::existsBySlug
);
// Si existe: "restaurante-mozaico-2"
```

---

## 📝 Mejores Prácticas

### ✅ Buenos Slugs

- `restaurante-el-rincon`
- `pizzeria-don-jose`
- `cafe-central-lima`
- `bistro-gourmet-2024`

### ❌ Malos Slugs

- `R3$t@ur@nt3` (caracteres especiales)
- `MI-RESTAURANT` (mayúsculas)
- `cafe--central` (guiones dobles)
- `-mi-slug-` (guiones al inicio/final)

---

## 🚀 Próximas Mejoras

- [ ] Historial de cambios de slug
- [ ] Redirecciones automáticas de slugs antiguos
- [ ] Sugerencias de slugs disponibles similares
- [ ] Validación de slugs reservados del sistema

---

## 📞 Soporte

**Endpoints relacionados:**
- Carta pública: `GET /api/v1/productos/public/{slug}/carta`
- QR público: `GET /api/v1/carta-qr/public/{slug}`

**Documentación relacionada:**
- `QR_CARTA_DIGITAL.md`: Sistema de códigos QR
- `API_CARTA_PUBLICA.md`: API pública de productos
