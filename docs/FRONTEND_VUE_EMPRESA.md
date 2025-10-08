# 🎨 Frontend Vue.js + TypeScript - Sistema de Empresa

## 📖 Guía para Desarrolladores Frontend

Esta documentación está específicamente diseñada para desarrolladores frontend que trabajen con **Vue.js 3 + TypeScript** para integrar el nuevo sistema de empresa y validación de IGV.

---

## 🎯 **Resumen de Cambios Backend**

### **Nuevas Funcionalidades Disponibles:**
- ✅ **Validación automática de configuración empresarial**
- ✅ **Cálculo dinámico de IGV según empresa**
- ✅ **Mensajes personalizados para clientes**
- ✅ **Capacidades de emisión de comprobantes**
- ✅ **API REST para consultas en tiempo real**

### **Configuración Actual del Sistema:**
```yaml
Empresa: "Restaurante Mozaico"
Tipo: TICKET_SIMPLE (Negocio informal)
IGV: No aplica (aplicaIgv: false)
Comprobantes: Solo tickets internos
```

---

## 🔧 **Configuración Inicial Vue + TS**

### **1. Instalación de Dependencias**
```bash
npm install axios
# o
yarn add axios
```

### **2. Configuración de Axios**
```typescript
// src/services/api.ts
import axios from 'axios'

const API_BASE_URL = process.env.VUE_APP_API_URL || 'http://localhost:8080'

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 10000
})

// Interceptor para manejo de errores
apiClient.interceptors.response.use(
  response => response,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)
```

---

## 📋 **Tipos TypeScript**

### **Interfaces para Validación de Empresa**
```typescript
// src/types/empresa.ts

export enum TipoOperacion {
  TICKET_SIMPLE = 'TICKET_SIMPLE',
  BOLETA_MANUAL = 'BOLETA_MANUAL', 
  FACTURACION_ELECTRONICA = 'FACTURACION_ELECTRONICA',
  MIXTO = 'MIXTO'
}

export interface ValidacionIgvResponse {
  // Configuración de IGV
  aplicaIgv: boolean
  porcentajeIgv: number
  moneda: string
  
  // Capacidades de emisión
  tipoOperacion: TipoOperacion
  puedeEmitirFacturas: boolean
  puedeEmitirBoletas: boolean
  puedeEmitirTickets: boolean
  facturacionElectronicaActiva: boolean
  
  // Información para cliente
  mensajeCliente: string
  tipoComprobanteDisponible: string
  comprobantesPermitidos: string[]
  
  // Datos de empresa
  nombreEmpresa: string
  ruc: string | null
  tieneRuc: boolean
  
  // Configuración de cálculos
  incluyeIgvEnPrecio: boolean
  formatoNumeracion: string
  prefijoComprobante: string
  
  // Estado y validaciones
  empresaActiva: boolean
  configuracionValida: boolean
  advertencias: string[]
  limitaciones: string[]
}

export interface CalculoTotales {
  subtotal: number
  igv: number
  descuento: number
  total: number
  aplicaIgv: boolean
  porcentajeIgv: number
}

export interface ProductoCarrito {
  id: number
  nombre: string
  precio: number
  cantidad: number
  subtotal: number
}
```

---

## 🔧 **Servicios de API**

### **Servicio de Empresa**
```typescript
// src/services/empresaService.ts
import { apiClient } from './api'
import type { ValidacionIgvResponse } from '@/types/empresa'

export class EmpresaService {
  private static readonly BASE_URL = '/api/v1/empresa/validacion'

  /**
   * Obtiene la validación completa de IGV y capacidades
   */
  static async getValidacionCompleta(): Promise<ValidacionIgvResponse> {
    try {
      const response = await apiClient.get<ValidacionIgvResponse>(`${this.BASE_URL}/igv`)
      return response.data
    } catch (error) {
      console.error('Error al obtener validación de empresa:', error)
      throw new Error('No se pudo cargar la configuración de la empresa')
    }
  }

  /**
   * Verificación rápida si aplica IGV
   */
  static async verificarAplicaIgv(): Promise<boolean> {
    try {
      const response = await apiClient.get<boolean>(`${this.BASE_URL}/aplica-igv`)
      return response.data
    } catch (error) {
      console.error('Error al verificar IGV:', error)
      return false
    }
  }

  /**
   * Obtiene el porcentaje de IGV configurado
   */
  static async getPorcentajeIgv(): Promise<number> {
    try {
      const response = await apiClient.get<number>(`${this.BASE_URL}/porcentaje-igv`)
      return response.data
    } catch (error) {
      console.error('Error al obtener porcentaje IGV:', error)
      return 0
    }
  }

  /**
   * Obtiene mensaje personalizado para mostrar al cliente
   */
  static async getMensajeCliente(): Promise<string> {
    try {
      const response = await apiClient.get(`${this.BASE_URL}/mensaje-cliente`, {
        responseType: 'text'
      })
      return response.data
    } catch (error) {
      console.error('Error al obtener mensaje cliente:', error)
      return 'No se pudo cargar información de la empresa'
    }
  }
}
```

---

## 🧮 **Composable para Cálculos**

### **Calculadora de Precios Reactiva**
```typescript
// src/composables/useCalculadoraPrecios.ts
import { ref, reactive, computed, onMounted } from 'vue'
import { EmpresaService } from '@/services/empresaService'
import type { ValidacionIgvResponse, CalculoTotales, ProductoCarrito } from '@/types/empresa'

export function useCalculadoraPrecios() {
  // Estado reactivo
  const validacion = ref<ValidacionIgvResponse | null>(null)
  const loading = ref(true)
  const error = ref<string | null>(null)

  // Configuración calculadora
  const config = reactive({
    aplicaIgv: false,
    porcentajeIgv: 18.0,
    inicializada: false
  })

  /**
   * Inicializa la calculadora obteniendo configuración de empresa
   */
  const inicializar = async (): Promise<void> => {
    try {
      loading.value = true
      error.value = null
      
      const response = await EmpresaService.getValidacionCompleta()
      validacion.value = response
      
      config.aplicaIgv = response.aplicaIgv
      config.porcentajeIgv = response.porcentajeIgv
      config.inicializada = true
      
      console.log('✅ Calculadora inicializada:', {
        aplicaIgv: config.aplicaIgv,
        porcentajeIgv: config.porcentajeIgv,
        tipoOperacion: response.tipoOperacion
      })
    } catch (err) {
      error.value = 'Error al inicializar calculadora de precios'
      console.error('❌ Error en calculadora:', err)
      
      // Configuración por defecto en caso de error
      config.aplicaIgv = false
      config.porcentajeIgv = 18.0
      config.inicializada = true
    } finally {
      loading.value = false
    }
  }

  /**
   * Calcula totales para una lista de productos
   */
  const calcularTotales = (productos: ProductoCarrito[]): CalculoTotales => {
    if (!config.inicializada) {
      throw new Error('Calculadora no inicializada. Llame a inicializar() primero.')
    }

    const subtotal = productos.reduce((sum, producto) => {
      return sum + (producto.precio * producto.cantidad)
    }, 0)

    let igv = 0
    if (config.aplicaIgv) {
      igv = subtotal * (config.porcentajeIgv / 100)
    }

    const descuento = 0 // Por ahora sin descuentos
    const total = subtotal + igv - descuento

    return {
      subtotal: Number(subtotal.toFixed(2)),
      igv: Number(igv.toFixed(2)),
      descuento: Number(descuento.toFixed(2)),
      total: Number(total.toFixed(2)),
      aplicaIgv: config.aplicaIgv,
      porcentajeIgv: config.porcentajeIgv
    }
  }

  /**
   * Formatea un monto en soles peruanos
   */
  const formatearMoneda = (amount: number): string => {
    return new Intl.NumberFormat('es-PE', {
      style: 'currency',
      currency: 'PEN'
    }).format(amount)
  }

  // Computed properties
  const mensajeEmpresa = computed(() => 
    validacion.value?.mensajeCliente || ''
  )

  const capacidades = computed(() => ({
    puedeEmitirFacturas: validacion.value?.puedeEmitirFacturas || false,
    puedeEmitirBoletas: validacion.value?.puedeEmitirBoletas || false,
    puedeEmitirTickets: validacion.value?.puedeEmitirTickets || true
  }))

  const limitaciones = computed(() => 
    validacion.value?.limitaciones || []
  )

  // Auto-inicializar en mounted
  onMounted(() => {
    inicializar()
  })

  return {
    // Estado
    validacion: readonly(validacion),
    loading: readonly(loading),
    error: readonly(error),
    config: readonly(config),
    
    // Métodos
    inicializar,
    calcularTotales,
    formatearMoneda,
    
    // Computed
    mensajeEmpresa,
    capacidades,
    limitaciones
  }
}
```

---

## 🎨 **Componentes Vue**

### **1. Componente de Información de Empresa**
```vue
<!-- src/components/EmpresaInfo.vue -->
<template>
  <div class="empresa-info">
    <!-- Loading State -->
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Cargando configuración de empresa...</p>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="error-message">
      <i class="fas fa-exclamation-triangle"></i>
      <p>{{ error }}</p>
      <button @click="recargar" class="btn-retry">Reintentar</button>
    </div>

    <!-- Success State -->
    <div v-else-if="validacion" class="empresa-content">
      <!-- Nombre de Empresa -->
      <h2 class="empresa-nombre">{{ validacion.nombreEmpresa }}</h2>
      
      <!-- Mensaje para Cliente -->
      <div class="mensaje-cliente" :class="mensajeClass">
        <i :class="mensajeIcon"></i>
        <p>{{ validacion.mensajeCliente }}</p>
      </div>

      <!-- Capacidades -->
      <div class="capacidades">
        <h3>Comprobantes Disponibles:</h3>
        <ul class="comprobantes-lista">
          <li 
            v-for="comprobante in validacion.comprobantesPermitidos" 
            :key="comprobante"
            class="comprobante-item"
          >
            <i class="fas fa-check-circle"></i>
            {{ comprobante }}
          </li>
        </ul>
      </div>

      <!-- Configuración IGV -->
      <div class="igv-config">
        <div class="igv-status" :class="{ 'igv-activo': validacion.aplicaIgv }">
          <span class="igv-label">IGV:</span>
          <span class="igv-value">
            {{ validacion.aplicaIgv ? `${validacion.porcentajeIgv}%` : 'No aplica' }}
          </span>
        </div>
      </div>

      <!-- Limitaciones (si existen) -->
      <div v-if="validacion.limitaciones.length > 0" class="limitaciones">
        <h4>Limitaciones:</h4>
        <ul class="limitaciones-lista">
          <li 
            v-for="limitacion in validacion.limitaciones" 
            :key="limitacion"
            class="limitacion-item"
          >
            <i class="fas fa-info-circle"></i>
            {{ limitacion }}
          </li>
        </ul>
      </div>

      <!-- Advertencias (si existen) -->
      <div v-if="validacion.advertencias.length > 0" class="advertencias">
        <h4>Advertencias:</h4>
        <ul class="advertencias-lista">
          <li 
            v-for="advertencia in validacion.advertencias" 
            :key="advertencia"
            class="advertencia-item"
          >
            <i class="fas fa-exclamation-triangle"></i>
            {{ advertencia }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useCalculadoraPrecios } from '@/composables/useCalculadoraPrecios'

// Composable
const { validacion, loading, error, inicializar } = useCalculadoraPrecios()

// Computed para clases dinámicas
const mensajeClass = computed(() => {
  if (!validacion.value) return ''
  
  switch (validacion.value.tipoOperacion) {
    case 'TICKET_SIMPLE':
      return 'mensaje-informal'
    case 'BOLETA_MANUAL':
      return 'mensaje-formal'
    case 'FACTURACION_ELECTRONICA':
      return 'mensaje-electronica'
    default:
      return 'mensaje-default'
  }
})

const mensajeIcon = computed(() => {
  if (!validacion.value) return 'fas fa-info-circle'
  
  switch (validacion.value.tipoOperacion) {
    case 'TICKET_SIMPLE':
      return 'fas fa-ticket-alt'
    case 'BOLETA_MANUAL':
      return 'fas fa-file-alt'
    case 'FACTURACION_ELECTRONICA':
      return 'fas fa-certificate'
    default:
      return 'fas fa-info-circle'
  }
})

// Métodos
const recargar = () => {
  inicializar()
}
</script>

<style scoped>
.empresa-info {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.loading {
  text-align: center;
  padding: 40px;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  text-align: center;
  padding: 20px;
  color: #dc3545;
}

.btn-retry {
  background: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 10px;
}

.empresa-nombre {
  color: #333;
  margin-bottom: 15px;
  font-size: 1.5em;
}

.mensaje-cliente {
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.mensaje-informal {
  background: #e7f3ff;
  border-left: 4px solid #007bff;
  color: #004085;
}

.mensaje-formal {
  background: #d4edda;
  border-left: 4px solid #28a745;
  color: #155724;
}

.mensaje-electronica {
  background: #d1ecf1;
  border-left: 4px solid #17a2b8;
  color: #0c5460;
}

.capacidades {
  margin-bottom: 20px;
}

.comprobantes-lista {
  list-style: none;
  padding: 0;
}

.comprobante-item {
  padding: 8px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #28a745;
}

.igv-config {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  margin-bottom: 20px;
}

.igv-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.igv-label {
  font-weight: bold;
}

.igv-value {
  font-size: 1.1em;
  padding: 4px 8px;
  border-radius: 4px;
  background: #e9ecef;
}

.igv-activo .igv-value {
  background: #d4edda;
  color: #155724;
}

.limitaciones, .advertencias {
  margin-top: 15px;
}

.limitaciones-lista, .advertencias-lista {
  list-style: none;
  padding: 0;
}

.limitacion-item {
  padding: 5px 0;
  color: #6c757d;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.advertencia-item {
  padding: 5px 0;
  color: #856404;
  display: flex;
  align-items: flex-start;
  gap: 8px;
}
</style>
```

### **2. Componente Calculadora de Carrito**
```vue
<!-- src/components/CarritoCalculadora.vue -->
<template>
  <div class="carrito-calculadora">
    <h3>Resumen del Pedido</h3>
    
    <!-- Lista de Productos -->
    <div class="productos-carrito">
      <div 
        v-for="producto in productos" 
        :key="producto.id"
        class="producto-item"
      >
        <div class="producto-info">
          <span class="producto-nombre">{{ producto.nombre }}</span>
          <span class="producto-cantidad">x{{ producto.cantidad }}</span>
        </div>
        <div class="producto-precio">
          {{ formatearMoneda(producto.subtotal) }}
        </div>
      </div>
    </div>

    <!-- Cálculos -->
    <div class="totales-section">
      <div class="total-line">
        <span>Subtotal:</span>
        <span>{{ formatearMoneda(totales.subtotal) }}</span>
      </div>

      <!-- IGV solo si aplica -->
      <div v-if="totales.aplicaIgv" class="total-line igv-line">
        <span>IGV ({{ totales.porcentajeIgv }}%):</span>
        <span>{{ formatearMoneda(totales.igv) }}</span>
      </div>

      <!-- Descuento si existe -->
      <div v-if="totales.descuento > 0" class="total-line descuento-line">
        <span>Descuento:</span>
        <span>-{{ formatearMoneda(totales.descuento) }}</span>
      </div>

      <hr class="divider">

      <div class="total-line total-final">
        <span>Total:</span>
        <span>{{ formatearMoneda(totales.total) }}</span>
      </div>
    </div>

    <!-- Información adicional -->
    <div class="info-adicional">
      <div v-if="!totales.aplicaIgv" class="sin-igv-notice">
        <i class="fas fa-info-circle"></i>
        <small>Los precios NO incluyen IGV (empresa informal)</small>
      </div>
      <div v-else class="con-igv-notice">
        <i class="fas fa-check-circle"></i>
        <small>Los precios incluyen IGV ({{ totales.porcentajeIgv }}%)</small>
      </div>
    </div>

    <!-- Botones de Acción -->
    <div class="acciones">
      <button 
        @click="$emit('procesar-pedido')"
        :disabled="productos.length === 0"
        class="btn-procesar"
      >
        <i class="fas fa-shopping-cart"></i>
        Procesar Pedido
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useCalculadoraPrecios } from '@/composables/useCalculadoraPrecios'
import type { ProductoCarrito } from '@/types/empresa'

// Props
interface Props {
  productos: ProductoCarrito[]
}

const props = defineProps<Props>()

// Emits
defineEmits<{
  'procesar-pedido': []
}>()

// Composable
const { calcularTotales, formatearMoneda, config } = useCalculadoraPrecios()

// Computed
const totales = computed(() => {
  if (!config.inicializada || props.productos.length === 0) {
    return {
      subtotal: 0,
      igv: 0,
      descuento: 0,
      total: 0,
      aplicaIgv: false,
      porcentajeIgv: 0
    }
  }
  
  return calcularTotales(props.productos)
})
</script>

<style scoped>
.carrito-calculadora {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.productos-carrito {
  margin-bottom: 20px;
}

.producto-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.producto-info {
  display: flex;
  flex-direction: column;
}

.producto-nombre {
  font-weight: 500;
}

.producto-cantidad {
  font-size: 0.9em;
  color: #6c757d;
}

.totales-section {
  margin-bottom: 15px;
}

.total-line {
  display: flex;
  justify-content: space-between;
  padding: 5px 0;
}

.igv-line {
  color: #28a745;
  font-weight: 500;
}

.descuento-line {
  color: #dc3545;
}

.total-final {
  font-weight: bold;
  font-size: 1.1em;
  color: #333;
}

.divider {
  margin: 10px 0;
  border: none;
  border-top: 2px solid #dee2e6;
}

.info-adicional {
  margin-bottom: 20px;
}

.sin-igv-notice {
  color: #007bff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.con-igv-notice {
  color: #28a745;
  display: flex;
  align-items: center;
  gap: 8px;
}

.acciones {
  text-align: center;
}

.btn-procesar {
  background: #28a745;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 1em;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 auto;
}

.btn-procesar:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.btn-procesar:not(:disabled):hover {
  background: #218838;
}
</style>
```

### **3. Store Pinia para Estado Global**
```typescript
// src/stores/empresaStore.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { EmpresaService } from '@/services/empresaService'
import type { ValidacionIgvResponse } from '@/types/empresa'

export const useEmpresaStore = defineStore('empresa', () => {
  // Estado
  const validacion = ref<ValidacionIgvResponse | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const ultimaActualizacion = ref<Date | null>(null)

  // Getters
  const aplicaIgv = computed(() => validacion.value?.aplicaIgv ?? false)
  const porcentajeIgv = computed(() => validacion.value?.porcentajeIgv ?? 0)
  const nombreEmpresa = computed(() => validacion.value?.nombreEmpresa ?? '')
  const tipoOperacion = computed(() => validacion.value?.tipoOperacion)
  const mensajeCliente = computed(() => validacion.value?.mensajeCliente ?? '')
  
  const capacidades = computed(() => ({
    puedeEmitirFacturas: validacion.value?.puedeEmitirFacturas ?? false,
    puedeEmitirBoletas: validacion.value?.puedeEmitirBoletas ?? false,
    puedeEmitirTickets: validacion.value?.puedeEmitirTickets ?? true
  }))

  const esEmpresaInformal = computed(() => 
    tipoOperacion.value === 'TICKET_SIMPLE'
  )

  const necesitaActualizacion = computed(() => {
    if (!ultimaActualizacion.value) return true
    const ahora = new Date()
    const diferencia = ahora.getTime() - ultimaActualizacion.value.getTime()
    return diferencia > 5 * 60 * 1000 // 5 minutos
  })

  // Actions
  const cargarValidacion = async (forzar = false): Promise<void> => {
    if (!forzar && validacion.value && !necesitaActualizacion.value) {
      return // Ya tenemos datos recientes
    }

    try {
      loading.value = true
      error.value = null

      const response = await EmpresaService.getValidacionCompleta()
      validacion.value = response
      ultimaActualizacion.value = new Date()

      console.log('✅ Validación de empresa actualizada:', response)
    } catch (err) {
      error.value = 'Error al cargar configuración de empresa'
      console.error('❌ Error al cargar validación:', err)
      throw err
    } finally {
      loading.value = false
    }
  }

  const verificarSoloIgv = async (): Promise<boolean> => {
    try {
      return await EmpresaService.verificarAplicaIgv()
    } catch (err) {
      console.error('Error al verificar IGV:', err)
      return aplicaIgv.value
    }
  }

  const obtenerMensajeCliente = async (): Promise<string> => {
    try {
      return await EmpresaService.getMensajeCliente()
    } catch (err) {
      console.error('Error al obtener mensaje:', err)
      return mensajeCliente.value
    }
  }

  const limpiarCache = (): void => {
    validacion.value = null
    ultimaActualizacion.value = null
    error.value = null
  }

  return {
    // Estado
    validacion: readonly(validacion),
    loading: readonly(loading),
    error: readonly(error),
    ultimaActualizacion: readonly(ultimaActualizacion),

    // Getters
    aplicaIgv,
    porcentajeIgv,
    nombreEmpresa,
    tipoOperacion,
    mensajeCliente,
    capacidades,
    esEmpresaInformal,
    necesitaActualizacion,

    // Actions
    cargarValidacion,
    verificarSoloIgv,
    obtenerMensajeCliente,
    limpiarCache
  }
})
```

---

## 🎮 **Ejemplos de Uso**

### **1. Página Principal con Información de Empresa**
```vue
<!-- src/views/HomePage.vue -->
<template>
  <div class="home-page">
    <!-- Información de Empresa -->
    <EmpresaInfo />
    
    <!-- Carrito de Compras -->
    <div class="carrito-section">
      <CarritoCalculadora 
        :productos="productosCarrito"
        @procesar-pedido="procesarPedido"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import EmpresaInfo from '@/components/EmpresaInfo.vue'
import CarritoCalculadora from '@/components/CarritoCalculadora.vue'
import { useEmpresaStore } from '@/stores/empresaStore'
import type { ProductoCarrito } from '@/types/empresa'

// Store
const empresaStore = useEmpresaStore()

// Estado local
const productosCarrito = ref<ProductoCarrito[]>([
  {
    id: 1,
    nombre: 'Hamburguesa Clásica',
    precio: 12.00,
    cantidad: 1,
    subtotal: 12.00
  },
  {
    id: 2,
    nombre: 'Coca-Cola',
    precio: 2.50,
    cantidad: 2,
    subtotal: 5.00
  }
])

// Métodos
const procesarPedido = async () => {
  try {
    // Verificar capacidades antes de procesar
    await empresaStore.cargarValidacion()
    
    if (empresaStore.esEmpresaInformal) {
      console.log('🎟️ Procesando como ticket interno')
      // Lógica para ticket interno
    } else if (empresaStore.capacidades.puedeEmitirFacturas) {
      console.log('📄 Procesando con facturación')
      // Lógica para factura/boleta
    }
    
    // Continuar con procesamiento...
  } catch (error) {
    console.error('Error al procesar pedido:', error)
  }
}

// Lifecycle
onMounted(async () => {
  await empresaStore.cargarValidacion()
})
</script>
```

### **2. Plugin Global para Calculadora**
```typescript
// src/plugins/calculadora.ts
import type { App } from 'vue'
import { useCalculadoraPrecios } from '@/composables/useCalculadoraPrecios'

export default {
  install(app: App) {
    app.config.globalProperties.$calculadora = useCalculadoraPrecios()
    
    app.provide('calculadora', useCalculadoraPrecios())
  }
}
```

### **3. Guard de Ruta para Verificar Configuración**
```typescript
// src/router/guards.ts
import type { NavigationGuard } from 'vue-router'
import { useEmpresaStore } from '@/stores/empresaStore'

export const verificarEmpresaGuard: NavigationGuard = async (to, from, next) => {
  const empresaStore = useEmpresaStore()
  
  try {
    // Cargar configuración si no existe
    if (!empresaStore.validacion) {
      await empresaStore.cargarValidacion()
    }
    
    // Verificar si la empresa está activa
    if (!empresaStore.validacion?.empresaActiva) {
      next({ name: 'ConfiguracionEmpresa' })
      return
    }
    
    next()
  } catch (error) {
    console.error('Error en guard de empresa:', error)
    next({ name: 'Error', params: { error: 'empresa-no-disponible' } })
  }
}
```

---

## 🧪 **Testing**

### **Test del Composable**
```typescript
// src/composables/__tests__/useCalculadoraPrecios.test.ts
import { describe, it, expect, beforeEach, vi } from 'vitest'
import { useCalculadoraPrecios } from '../useCalculadoraPrecios'
import { EmpresaService } from '@/services/empresaService'
import type { ProductoCarrito } from '@/types/empresa'

// Mock del servicio
vi.mock('@/services/empresaService')

describe('useCalculadoraPrecios', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  it('debería inicializar correctamente para empresa informal', async () => {
    // Mock respuesta
    const mockValidacion = {
      aplicaIgv: false,
      porcentajeIgv: 18.0,
      tipoOperacion: 'TICKET_SIMPLE',
      mensajeCliente: 'Empresa informal'
    }

    vi.mocked(EmpresaService.getValidacionCompleta).mockResolvedValue(mockValidacion)

    const { inicializar, config } = useCalculadoraPrecios()
    
    await inicializar()

    expect(config.aplicaIgv).toBe(false)
    expect(config.porcentajeIgv).toBe(18.0)
    expect(config.inicializada).toBe(true)
  })

  it('debería calcular totales sin IGV para empresa informal', async () => {
    // Setup
    const mockValidacion = {
      aplicaIgv: false,
      porcentajeIgv: 18.0
    }

    vi.mocked(EmpresaService.getValidacionCompleta).mockResolvedValue(mockValidacion)

    const { inicializar, calcularTotales } = useCalculadoraPrecios()
    await inicializar()

    // Productos de prueba
    const productos: ProductoCarrito[] = [
      { id: 1, nombre: 'Test', precio: 10.0, cantidad: 2, subtotal: 20.0 }
    ]

    // Test
    const resultado = calcularTotales(productos)

    expect(resultado.subtotal).toBe(20.0)
    expect(resultado.igv).toBe(0.0)
    expect(resultado.total).toBe(20.0)
    expect(resultado.aplicaIgv).toBe(false)
  })

  it('debería calcular totales con IGV para empresa formal', async () => {
    // Setup
    const mockValidacion = {
      aplicaIgv: true,
      porcentajeIgv: 18.0
    }

    vi.mocked(EmpresaService.getValidacionCompleta).mockResolvedValue(mockValidacion)

    const { inicializar, calcularTotales } = useCalculadoraPrecios()
    await inicializar()

    // Productos de prueba
    const productos: ProductoCarrito[] = [
      { id: 1, nombre: 'Test', precio: 100.0, cantidad: 1, subtotal: 100.0 }
    ]

    // Test
    const resultado = calcularTotales(productos)

    expect(resultado.subtotal).toBe(100.0)
    expect(resultado.igv).toBe(18.0)
    expect(resultado.total).toBe(118.0)
    expect(resultado.aplicaIgv).toBe(true)
  })
})
```

---

## 📋 **Checklist de Implementación**

### **✅ Configuración Inicial**
- [ ] Instalar dependencias (`axios`)
- [ ] Configurar cliente API
- [ ] Crear tipos TypeScript
- [ ] Configurar variables de entorno

### **✅ Servicios**
- [ ] Implementar `EmpresaService`
- [ ] Crear composable `useCalculadoraPrecios`
- [ ] Configurar store Pinia (opcional)

### **✅ Componentes**
- [ ] Componente `EmpresaInfo`
- [ ] Componente `CarritoCalculadora`
- [ ] Integrar en páginas principales

### **✅ Funcionalidades**
- [ ] Carga automática de configuración
- [ ] Cálculo dinámico de IGV
- [ ] Mensajes informativos al cliente
- [ ] Manejo de estados de error

### **✅ Testing**
- [ ] Tests unitarios de composables
- [ ] Tests de componentes
- [ ] Tests de integración API

---

## 🚀 **Próximos Pasos**

1. **Implementar notificaciones**: Toast/alerts para cambios de configuración
2. **Cache inteligente**: Reducir llamadas API con cache temporal
3. **Offline support**: Configuración por defecto cuando no hay conexión
4. **Dashboard admin**: Interfaz para cambiar configuración de empresa
5. **Integración PWA**: Notificaciones push para cambios importantes

Esta documentación te proporciona todo lo necesario para integrar el sistema de empresa flexible en tu frontend Vue.js + TypeScript, con ejemplos prácticos y código listo para usar.