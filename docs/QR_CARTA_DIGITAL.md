# 📱 Sistema de Carta Digital con QR - Multitenant Seguro

## 🎯 Descripción

Sistema de carta digital con códigos QR que utiliza **slugs únicos** en lugar de IDs numéricos para mayor seguridad y mejor experiencia de usuario.

---

## 🔒 Seguridad Mejorada con Slugs

### ⚠️ Problema Anterior

```
❌ URL con ID numérico (inseguro):
http://localhost:8091/api/v1/productos/public/1/carta

Problema: Fácil de manipular (1, 2, 3, 4...)
Un usuario podría cambiar el número y ver productos de otras empresas
```

### ✅ Solución con Slug

```
✅ URL con slug único (seguro):
http://localhost:8091/api/v1/productos/public/restaurante-mozaico/carta

Ventajas:
- No se puede adivinar fácilmente
- Amigable para SEO
- Identificador único y legible
- Mejor experiencia de usuario
```

---

## 📋 Endpoints Disponibles

### 1. **Obtener Carta Pública por Slug**

```
GET /api/v1/productos/public/{slug}/carta
```

**Autenticación:** ❌ NO requerida (público)

**Parámetros:**
- `slug` (path, required): Slug único de la empresa (ej: "restaurante-mozaico")
- `idCategoria` (query, optional): Filtrar por categoría

**Ejemplo:**
```bash
# Obtener toda la carta
curl http://localhost:8091/api/v1/productos/public/restaurante-mozaico/carta

# Filtrar por categoría
curl http://localhost:8091/api/v1/productos/public/restaurante-mozaico/carta?idCategoria=2
```

**Respuesta:**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Carta de productos obtenida exitosamente",
  "data": [
    {
      "idProducto": 1,
      "nombre": "Hamburguesa Clásica",
      "descripcion": "Hamburguesa con carne de res...",
      "precio": 12.00,
      "categoria": {
        "idCategoria": 1,
        "nombre": "Platos Principales"
      },
      "disponible": true,
      "imagenUrl": "/uploads/images/hamburguesa.jpg"
    }
  ]
}
```

---

### 2. **Generar QR de Carta (Autenticado)**

```
GET /api/v1/carta-qr/generar
```

**Autenticación:** ✅ Requerida (JWT)

**Descripción:** Genera un código QR para la carta digital de la empresa del usuario autenticado

**Respuesta:** Imagen PNG del código QR

**Ejemplo:**
```bash
curl -X GET "http://localhost:8091/api/v1/carta-qr/generar" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output qr-carta.png
```

---

### 3. **Generar QR Público**

```
GET /api/v1/carta-qr/public/{slug}
```

**Autenticación:** ❌ NO requerida (público)

**Descripción:** Genera un código QR para cualquier empresa (útil para marketing)

**Parámetros:**
- `slug` (path, required): Slug de la empresa

**Respuesta:** Imagen PNG del código QR

**Ejemplo:**
```bash
curl -X GET "http://localhost:8091/api/v1/carta-qr/public/restaurante-mozaico" \
  --output qr-carta-mozaico.png
```

---

## 🔧 Configuración

### application.properties

```properties
# URL del frontend para los códigos QR
app.frontend.url=http://localhost:5173

# En producción:
# app.frontend.url=https://mirestaurante.com
```

---

## 📊 Flujo Completo de Uso

### 1. **Configurar Slug de Empresa**

Cada empresa debe tener un slug único configurado:

```java
Empresa empresa = Empresa.builder()
    .nombre("Restaurante Mozaico")
    .slug("restaurante-mozaico")  // ← Slug único
    .build();
```

**Reglas para slugs:**
- Solo letras minúsculas, números y guiones
- Debe ser único en el sistema
- Ejemplos válidos:
  - `restaurante-el-buen-sabor`
  - `pizzeria-mama-mia-2024`
  - `cafe-central`

---

### 2. **Generar Código QR**

**Opción A: Desde el sistema (requiere login)**

```bash
# 1. Autenticarse
curl -X POST "http://localhost:8091/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "dmoran",
    "password": "123456"
  }'

# 2. Generar QR
curl -X GET "http://localhost:8091/api/v1/carta-qr/generar" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output mi-carta-qr.png
```

**Opción B: Sin autenticación (si conoces el slug)**

```bash
curl -X GET "http://localhost:8091/api/v1/carta-qr/public/restaurante-mozaico" \
  --output carta-qr.png
```

---

### 3. **Imprimir y Colocar QR**

Una vez generado el QR, puedes:

1. **Imprimir en mesas del restaurante**
   - Tamaño recomendado: 5x5 cm o 7x7 cm
   - Incluir texto: "Escanea para ver nuestra carta"

2. **Incluir en volantes/flyers**
   - QR grande para facilitar escaneo

3. **Mostrar en pantallas digitales**
   - Kioscos, pantallas de entrada, etc.

4. **Redes sociales**
   - Compartir la imagen del QR en Instagram, Facebook

---

### 4. **Cliente Escanea QR**

```
1. Cliente escanea QR con su móvil
   ↓
2. Se abre: http://localhost:5173/carta/restaurante-mozaico
   ↓
3. Frontend llama a: /api/v1/productos/public/restaurante-mozaico/carta
   ↓
4. Cliente ve la carta digital con:
   - Fotos de productos
   - Precios
   - Descripciones
   - Filtros por categoría
   - Búsqueda
```

---

## 🎨 Frontend Vue.js

### Ejemplo de Página de Carta

```vue
<template>
  <div class="carta-digital">
    <header>
      <h1>{{ empresaNombre }}</h1>
      <p>Nuestra Carta Digital</p>
    </header>

    <div class="filtros">
      <select v-model="categoriaSeleccionada" @change="cargarProductos">
        <option value="">Todas las categorías</option>
        <option v-for="cat in categorias" :key="cat.id" :value="cat.id">
          {{ cat.nombre }}
        </option>
      </select>

      <input
        v-model="busqueda"
        @input="filtrarProductos"
        placeholder="🔍 Buscar productos..."
      >
    </div>

    <div class="productos-grid">
      <div
        v-for="producto in productosFiltrados"
        :key="producto.idProducto"
        class="producto-card"
      >
        <img :src="producto.imagenUrl" :alt="producto.nombre">
        <h3>{{ producto.nombre }}</h3>
        <p>{{ producto.descripcion }}</p>
        <p class="precio">S/ {{ producto.precio.toFixed(2) }}</p>
        <span v-if="producto.categoria" class="categoria">
          {{ producto.categoria.nombre }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      slug: '',
      empresaNombre: '',
      productos: [],
      categorias: [],
      categoriaSeleccionada: '',
      busqueda: ''
    }
  },

  computed: {
    productosFiltrados() {
      let filtrados = this.productos;

      // Filtrar por búsqueda
      if (this.busqueda) {
        const busquedaLower = this.busqueda.toLowerCase();
        filtrados = filtrados.filter(p =>
          p.nombre.toLowerCase().includes(busquedaLower) ||
          (p.descripcion && p.descripcion.toLowerCase().includes(busquedaLower))
        );
      }

      return filtrados;
    }
  },

  async mounted() {
    // Obtener slug de la URL
    this.slug = this.$route.params.slug;
    await this.cargarProductos();
  },

  methods: {
    async cargarProductos() {
      try {
        const url = this.categoriaSeleccionada
          ? `http://localhost:8091/api/v1/productos/public/${this.slug}/carta?idCategoria=${this.categoriaSeleccionada}`
          : `http://localhost:8091/api/v1/productos/public/${this.slug}/carta`;

        const response = await fetch(url);
        const data = await response.json();

        if (data.status === 'SUCCESS') {
          this.productos = data.data;

          // Extraer categorías únicas
          const categoriasMap = new Map();
          this.productos.forEach(p => {
            if (p.categoria) {
              categoriasMap.set(p.categoria.idCategoria, p.categoria);
            }
          });
          this.categorias = Array.from(categoriasMap.values());

          // Obtener nombre de empresa del primer producto
          if (this.productos.length > 0 && this.productos[0].categoria) {
            this.empresaNombre = "Carta Digital";
          }
        }
      } catch (error) {
        console.error('Error al cargar productos:', error);
        alert('Error al cargar la carta digital');
      }
    }
  }
}
</script>

<style scoped>
.carta-digital {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.productos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.producto-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
  transition: transform 0.2s;
}

.producto-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.precio {
  font-size: 1.5em;
  color: #e91e63;
  font-weight: bold;
}
</style>
```

### Vue Router

```javascript
// router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import CartaDigital from '@/views/CartaDigital.vue'

const routes = [
  {
    path: '/carta/:slug',
    name: 'carta-digital',
    component: CartaDigital
  }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
```

---

## 📱 Casos de Uso

### Caso 1: Mesa del Restaurante

```
Cliente sentado en Mesa 5
   ↓
Escanea QR pegado en la mesa
   ↓
Ve la carta en su móvil
   ↓
Puede buscar, filtrar por categorías
   ↓
Llama al mesero para ordenar
```

### Caso 2: Delivery/Takeout

```
Cliente ve QR en volante o redes sociales
   ↓
Escanea QR desde casa
   ↓
Revisa la carta completa
   ↓
Llama para hacer pedido delivery
```

### Caso 3: Eventos Corporativos

```
Empresa organiza evento
   ↓
Imprime QR en invitaciones
   ↓
Asistentes escanean para ver menú del evento
   ↓
Pueden ver opciones vegetarianas, alérgenos, etc.
```

---

## 🔐 Seguridad

### ✅ Lo que SÍ permite

- Ver carta pública de cualquier empresa (si conoces el slug)
- Filtrar productos por categoría
- Generar QR público (sin autenticación)

### ❌ Lo que NO permite

- Adivinar slugs de otras empresas fácilmente
- Ver productos inactivos o no disponibles
- Modificar precios o información
- Acceder a datos administrativos

---

## 🚀 Ventajas del Sistema

1. **Seguridad**: Slugs difíciles de adivinar vs IDs secuenciales
2. **SEO Friendly**: URLs legibles (`/carta/restaurante-mozaico`)
3. **Multitenant**: Cada empresa tiene su propia carta aislada
4. **Sin contacto**: Clientes ven la carta sin tocar menús físicos
5. **Actualización en tiempo real**: Cambios reflejados inmediatamente
6. **Sostenible**: Reduce uso de papel en menús impresos
7. **Estadísticas**: Se puede trackear cuántos escaneos tiene el QR

---

## 📈 Próximas Mejoras

- [ ] Analíticas de escaneos de QR
- [ ] Múltiples idiomas en la carta
- [ ] Información nutricional y alérgenos
- [ ] Integración con sistema de pedidos online
- [ ] QR dinámicos con promociones por horario
- [ ] Sistema de recomendaciones personalizado

---

## 📝 Changelog

### Versión 2.3.0 - Sistema de Carta Digital con QR

**Nuevas funcionalidades:**
- ✅ Campo `slug` único en entidad Empresa
- ✅ Endpoints públicos usando slug en lugar de ID
- ✅ Servicio de generación de códigos QR (ZXing)
- ✅ Endpoints para generar QR autenticado y público
- ✅ Configuración de URL de frontend
- ✅ Documentación completa

---

## 🛠️ Soporte Técnico

**Dependencias:**
```xml
<!-- ZXing para generación de QR -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.3</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.3</version>
</dependency>
```

**Configuración mínima requerida:**
1. Slug configurado en la empresa
2. Frontend URL en application.properties
3. Endpoint público habilitado en SecurityConfig
