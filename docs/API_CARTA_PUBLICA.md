# 🍽️ API Pública de Carta de Productos (Multitenant)

## Descripción
Endpoints públicos para mostrar la carta/menú de productos a los clientes **sin necesidad de autenticación**.

La respuesta incluye **información completa de la empresa** (nombre, logo, dirección, teléfono, etc.) junto con los productos disponibles, ideal para cartas digitales completas.

⚠️ **IMPORTANTE:** Este sistema es **multitenant**. Cada empresa tiene su propia carta de productos identificada por un `slug` único (ej: "restaurante-mozaico"). El `slug` es **OBLIGATORIO** en todos los endpoints.

## Endpoints Disponibles

### 1. Obtener Carta Completa de una Empresa
```
GET /api/v1/productos/public/{slug}/carta
```

**Descripción:** Obtiene todos los productos disponibles y activos de una empresa específica, junto con información de la empresa (nombre, logo, dirección, etc.).

**Autenticación:** ❌ NO requerida (público)

**Parámetros de ruta:**
- `slug` (**OBLIGATORIO**): Slug único de la empresa (ej: "restaurante-mozaico")

**Parámetros de consulta:**
- `idCategoria` (opcional): Filtrar por ID de categoría

**Filtros aplicados automáticamente:**
- ✅ Solo productos de la empresa especificada por slug (**multitenant seguro**)
- ✅ Solo productos con `disponible = true`
- ✅ Solo productos con `estado = ACTIVO`

**Respuesta exitosa (200):**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Carta de productos obtenida exitosamente",
  "data": {
    "empresa": {
      "nombre": "Restaurante Mozaico",
      "slug": "restaurante-mozaico",
      "descripcion": "Comida peruana tradicional con ingredientes frescos",
      "direccion": "Av. Principal 123, Lima",
      "telefono": "987654321",
      "email": "contacto@mozaico.com",
      "logoUrl": "/uploads/logos/mozaico.png",
      "paginaWeb": "https://www.mozaico.com",
      "moneda": "PEN"
    },
    "productos": [
      {
        "idProducto": 1,
        "nombre": "Hamburguesa Clásica",
        "descripcion": "Hamburguesa con carne de res, lechuga, tomate y queso",
        "precio": 12.00,
        "categoria": {
          "idCategoria": 1,
          "nombre": "Platos Principales"
        },
        "tiempoPreparacion": 15,
        "disponible": true,
        "imagenUrl": "/uploads/images/products/hamburguesa.jpg",
        "ingredientes": "Carne de res, pan, lechuga, tomate, queso",
        "calorias": 550,
        "requierePreparacion": true,
        "esAlcoholico": false,
        "estado": "ACTIVO"
      }
    ]
  }
}
```

**Ejemplo de uso:**
```bash
# Obtener toda la carta de "restaurante-mozaico"
curl -X GET "http://localhost:8091/api/v1/productos/public/restaurante-mozaico/carta"

# Filtrar por categoría (ej: Bebidas - idCategoria=2)
curl -X GET "http://localhost:8091/api/v1/productos/public/restaurante-mozaico/carta?idCategoria=2"

# Obtener carta de otra empresa usando su slug
curl -X GET "http://localhost:8091/api/v1/productos/public/mi-cafeteria/carta"
```

---

### 2. Obtener Carta por Categoría de una Empresa
```
GET /api/v1/productos/public/{slug}/carta/por-categoria
```

**Descripción:** Obtiene todos los productos de una empresa agrupados por categoría, junto con información de la empresa.

**Autenticación:** ❌ NO requerida (público)

**Parámetros de ruta:**
- `slug` (**OBLIGATORIO**): Slug único de la empresa (ej: "restaurante-mozaico")

**Filtros aplicados automáticamente:**
- ✅ Solo productos de la empresa especificada por slug
- ✅ Solo productos disponibles
- ✅ Solo productos activos

**Respuesta exitosa (200):**
```json
{
  "status": "SUCCESS",
  "code": 200,
  "message": "Carta de productos por categoría obtenida exitosamente",
  "data": {
    "empresa": {
      "nombre": "Restaurante Mozaico",
      "slug": "restaurante-mozaico",
      "descripcion": "Comida peruana tradicional con ingredientes frescos",
      "direccion": "Av. Principal 123, Lima",
      "telefono": "987654321",
      "email": "contacto@mozaico.com",
      "logoUrl": "/uploads/logos/mozaico.png",
      "paginaWeb": "https://www.mozaico.com",
      "moneda": "PEN"
    },
    "productos": [
      {
        "idProducto": 1,
        "nombre": "Hamburguesa Clásica",
        "precio": 12.00,
        "categoria": {
          "idCategoria": 1,
          "nombre": "Platos Principales"
        }
      },
      {
        "idProducto": 2,
        "nombre": "Coca-Cola",
        "precio": 2.50,
        "categoria": {
          "idCategoria": 2,
          "nombre": "Bebidas"
        }
      }
    ]
  }
}
```

**Ejemplo de uso:**
```bash
# Obtener productos por categoría de "restaurante-mozaico"
curl -X GET "http://localhost:8091/api/v1/productos/public/restaurante-mozaico/carta/por-categoria"
```

---

## Integración en el Frontend

### JavaScript Vanilla

```javascript
// IMPORTANTE: Definir el slug de la empresa
const SLUG_EMPRESA = 'restaurante-mozaico'; // Cambiar según tu empresa

// Obtener carta completa de la empresa
async function obtenerCarta() {
    try {
        const response = await fetch(
            `http://localhost:8091/api/v1/productos/public/${SLUG_EMPRESA}/carta`
        );
        const data = await response.json();

        if (data.status === 'SUCCESS') {
            console.log('Empresa:', data.data.empresa);
            console.log('Productos:', data.data.productos);

            // Mostrar información de la empresa
            mostrarInfoEmpresa(data.data.empresa);

            // Mostrar productos
            mostrarProductos(data.data.productos);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

// Mostrar información de la empresa
function mostrarInfoEmpresa(empresa) {
    document.getElementById('nombre-empresa').textContent = empresa.nombre;
    document.getElementById('logo-empresa').src = empresa.logoUrl;
    document.getElementById('descripcion-empresa').textContent = empresa.descripcion;
    document.getElementById('direccion-empresa').textContent = empresa.direccion;
    document.getElementById('telefono-empresa').textContent = empresa.telefono;
}

// Filtrar por categoría
async function obtenerCartaPorCategoria(slugEmpresa, idCategoria) {
    const url = idCategoria
        ? `http://localhost:8091/api/v1/productos/public/${slugEmpresa}/carta?idCategoria=${idCategoria}`
        : `http://localhost:8091/api/v1/productos/public/${slugEmpresa}/carta`;

    const response = await fetch(url);
    const data = await response.json();
    return data.data; // Retorna { empresa: {...}, productos: [...] }
}
```

### Vue.js

```vue
<template>
  <div class="carta-menu">
    <h2>Nuestra Carta</h2>

    <!-- Filtro por categoría -->
    <select v-model="categoriaSeleccionada" @change="cargarProductos">
      <option value="">Todas las categorías</option>
      <option v-for="cat in categorias" :key="cat.id" :value="cat.id">
        {{ cat.nombre }}
      </option>
    </select>

    <!-- Lista de productos -->
    <div class="productos-grid">
      <div v-for="producto in productos" :key="producto.idProducto" class="producto-card">
        <img :src="producto.imagenUrl" :alt="producto.nombre">
        <h3>{{ producto.nombre }}</h3>
        <p>{{ producto.descripcion }}</p>
        <p class="precio">S/ {{ producto.precio.toFixed(2) }}</p>
        <button @click="agregarAlCarrito(producto)">Agregar</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      productos: [],
      categorias: [],
      categoriaSeleccionada: ''
    }
  },

  mounted() {
    this.cargarProductos();
    this.cargarCategorias();
  },

  methods: {
    async cargarProductos() {
      try {
        const url = this.categoriaSeleccionada
          ? `/api/v1/productos/public/carta?idCategoria=${this.categoriaSeleccionada}`
          : '/api/v1/productos/public/carta';

        const response = await this.$axios.get(url);
        this.productos = response.data.data;
      } catch (error) {
        console.error('Error al cargar productos:', error);
      }
    },

    async cargarCategorias() {
      // Asumiendo que tienes un endpoint para categorías
      // O extraerlas de los productos
      const response = await this.$axios.get('/api/v1/productos/public/carta');
      const productos = response.data.data;

      // Extraer categorías únicas
      const categoriasUnicas = [...new Map(
        productos.map(p => [p.categoria.idCategoria, p.categoria])
      ).values()];

      this.categorias = categoriasUnicas;
    },

    agregarAlCarrito(producto) {
      this.$store.commit('agregarProducto', producto);
      this.$notify({
        type: 'success',
        message: `${producto.nombre} agregado al carrito`
      });
    }
  }
}
</script>

<style scoped>
.productos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  padding: 20px;
}

.producto-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  text-align: center;
}

.producto-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 4px;
}

.precio {
  font-size: 1.5em;
  color: #e91e63;
  font-weight: bold;
}
</style>
```

### React

```jsx
import { useState, useEffect } from 'react';

function CartaMenu() {
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState('');

  useEffect(() => {
    cargarProductos();
  }, [categoriaSeleccionada]);

  const cargarProductos = async () => {
    try {
      const url = categoriaSeleccionada
        ? `http://localhost:8091/api/v1/productos/public/carta?idCategoria=${categoriaSeleccionada}`
        : 'http://localhost:8091/api/v1/productos/public/carta';

      const response = await fetch(url);
      const data = await response.json();

      if (data.status === 'SUCCESS') {
        setProductos(data.data);

        // Extraer categorías únicas
        const cats = [...new Map(
          data.data.map(p => [p.categoria.idCategoria, p.categoria])
        ).values()];
        setCategorias(cats);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  return (
    <div className="carta-menu">
      <h2>Nuestra Carta</h2>

      <select
        value={categoriaSeleccionada}
        onChange={(e) => setCategoriaSeleccionada(e.target.value)}
      >
        <option value="">Todas las categorías</option>
        {categorias.map(cat => (
          <option key={cat.idCategoria} value={cat.idCategoria}>
            {cat.nombre}
          </option>
        ))}
      </select>

      <div className="productos-grid">
        {productos.map(producto => (
          <div key={producto.idProducto} className="producto-card">
            <img src={producto.imagenUrl} alt={producto.nombre} />
            <h3>{producto.nombre}</h3>
            <p>{producto.descripcion}</p>
            <p className="precio">S/ {producto.precio.toFixed(2)}</p>
            <button onClick={() => agregarAlCarrito(producto)}>
              Agregar
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default CartaMenu;
```

---

## Características

### ✅ Ventajas
- **Sin autenticación:** Acceso público para todos los clientes
- **Filtrado automático:** Solo muestra productos disponibles y activos
- **Rendimiento:** Respuesta rápida sin validaciones JWT
- **CORS habilitado:** Accesible desde cualquier dominio frontend

### 🎯 Casos de Uso
1. **Aplicación web del restaurante** - Mostrar menú a clientes
2. **App móvil de pedidos** - Carta digital
3. **Quioscos digitales** - Menú interactivo
4. **Integración con sistemas externos** - APIs de terceros

### 🔒 Seguridad
- Solo lectura (GET)
- No expone información sensible
- No permite modificaciones
- Productos inactivos/no disponibles están ocultos

---

## Notas Importantes

1. **Reiniciar la aplicación** después de los cambios en `SecurityConfig`
2. **CORS está configurado** para `http://localhost:5173` (modifica según tu frontend)
3. **Imágenes de productos** también son públicas en `/uploads/images/products/**`
4. Para **operaciones administrativas** (crear, editar, eliminar) usar los endpoints con autenticación

---

## Endpoints Relacionados (con autenticación)

Para administración de productos (requieren JWT):
- `GET /api/v1/productos` - Lista todos los productos (admin)
- `POST /api/v1/productos` - Crear producto
- `PUT /api/v1/productos/{id}` - Actualizar producto
- `DELETE /api/v1/productos/{id}` - Eliminar producto
- `GET /api/v1/productos/buscar` - Búsqueda avanzada

---

## Ejemplo Completo HTML

```html
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carta - Mozaico Restaurant</title>
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        body { font-family: Arial, sans-serif; padding: 20px; }
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
        }
        .producto-card img {
            width: 100%;
            height: 200px;
            object-fit: cover;
            border-radius: 4px;
        }
        .precio {
            font-size: 1.5em;
            color: #e91e63;
            font-weight: bold;
            margin: 10px 0;
        }
        button {
            background: #4CAF50;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover { background: #45a049; }
    </style>
</head>
<body>
    <h1>🍽️ Carta de Productos</h1>
    <div id="productos" class="productos-grid"></div>

    <script>
        async function cargarCarta() {
            try {
                const response = await fetch('http://localhost:8091/api/v1/productos/public/carta');
                const data = await response.json();

                if (data.status === 'SUCCESS') {
                    mostrarProductos(data.data);
                }
            } catch (error) {
                console.error('Error:', error);
            }
        }

        function mostrarProductos(productos) {
            const container = document.getElementById('productos');
            container.innerHTML = productos.map(p => `
                <div class="producto-card">
                    <img src="${p.imagenUrl || '/placeholder.jpg'}" alt="${p.nombre}">
                    <h3>${p.nombre}</h3>
                    <p>${p.descripcion || ''}</p>
                    <p class="precio">S/ ${p.precio.toFixed(2)}</p>
                    <button onclick="alert('Agregado al carrito: ${p.nombre}')">
                        Agregar al carrito
                    </button>
                </div>
            `).join('');
        }

        cargarCarta();
    </script>
</body>
</html>
```

---

## Soporte

Para más información consulta:
- **Documentación del sistema:** `docs/API_REFERENCE.md`
- **CHANGELOG:** `CHANGELOG.md`
