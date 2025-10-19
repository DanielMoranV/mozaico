# API de Gestión de Reservas de Mesas - Guía para Frontend Vue + TypeScript

## Índice
1. [Descripción General](#descripción-general)
2. [Tipos TypeScript](#tipos-typescript)
3. [Endpoints](#endpoints)
4. [Ejemplos de Uso](#ejemplos-de-uso)
5. [Casos de Uso Comunes](#casos-de-uso-comunes)
6. [Manejo de Errores](#manejo-de-errores)

---

## Descripción General

La API de gestión de reservas permite:
- Crear, actualizar, eliminar y consultar reservas de mesas
- Consultar disponibilidad de mesas para una fecha/hora específica
- Buscar reservas con múltiples filtros
- Gestionar el ciclo de vida completo de las reservas (PENDIENTE → CONFIRMADA → EN_CURSO → COMPLETADA)

**Base URL**: `/api/v1/reservas`

**Autenticación**: Requiere token JWT en el header `Authorization: Bearer {token}`

**Multitenant**: Todas las operaciones están filtradas automáticamente por la empresa del usuario autenticado.

---

## Tipos TypeScript

### Enums

```typescript
// Estados de reserva
export enum EstadoReserva {
  PENDIENTE = 'PENDIENTE',        // Reserva creada, esperando confirmación
  CONFIRMADA = 'CONFIRMADA',      // Reserva confirmada
  EN_CURSO = 'EN_CURSO',          // Cliente llegó y está ocupando la mesa
  COMPLETADA = 'COMPLETADA',      // Reserva finalizada exitosamente
  CANCELADA = 'CANCELADA',        // Reserva cancelada
  NO_PRESENTADO = 'NO_PRESENTADO' // Cliente no se presentó (no-show)
}
```

### DTOs de Reserva

```typescript
// Request para crear reserva
export interface ReservaRequestDTO {
  idCliente: number;              // ID del cliente (requerido)
  idMesa: number;                 // ID de la mesa (requerido)
  fechaHoraReserva: string;       // ISO 8601 format: "2025-01-20T19:00:00" (requerido, futuro o presente)
  numeroPersonas: number;         // Cantidad de personas (requerido, mínimo 1)
  observaciones?: string;         // Notas adicionales (opcional)
  estado?: EstadoReserva;         // Por defecto: PENDIENTE (opcional)
}

// Request para actualizar reserva
export interface ReservaUpdateDTO {
  idCliente?: number;
  idMesa?: number;
  fechaHoraReserva?: string;      // ISO 8601 format
  numeroPersonas?: number;
  observaciones?: string;
  estado?: EstadoReserva;
}

// Response de reserva
export interface ReservaResponseDTO {
  idReserva: number;
  cliente: ClienteBasicoDTO;
  mesa: MesaBasicaDTO;
  fechaHoraReserva: string;       // ISO 8601 format
  numeroPersonas: number;
  estado: EstadoReserva;
  observaciones?: string;
  fechaCreacion: string;          // ISO 8601 format
}

export interface ClienteBasicoDTO {
  idCliente: number;
  nombre: string;
  apellido?: string;
}

export interface MesaBasicaDTO {
  idMesa: number;
  numeroMesa: number;
}
```

### DTOs de Disponibilidad

```typescript
// Request para consultar disponibilidad
export interface DisponibilidadRequestDTO {
  fechaHora: string;              // ISO 8601 format (requerido, futuro o presente)
  numeroPersonas: number;         // Cantidad de personas (requerido, mínimo 1)
  ubicacion?: string;             // Filtro opcional por ubicación de mesa
}

// Response de disponibilidad
export interface DisponibilidadResponseDTO {
  mesasDisponibles: MesaDisponibleDTO[];
  totalDisponibles: number;
}

export interface MesaDisponibleDTO {
  idMesa: number;
  numeroMesa: number;
  capacidad: number;
  ubicacion?: string;
  observaciones?: string;
}
```

### Response Wrapper

```typescript
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
```

---

## Endpoints

### 1. Crear Reserva

**POST** `/api/v1/reservas`

**Request Body**:
```typescript
{
  idCliente: number;
  idMesa: number;
  fechaHoraReserva: string;
  numeroPersonas: number;
  observaciones?: string;
  estado?: EstadoReserva;
}
```

**Response**: `ApiResponse<ReservaResponseDTO>`

**Status Codes**:
- `201 Created`: Reserva creada exitosamente
- `400 Bad Request`: Validación fallida (fechaHoraReserva en el pasado, numeroPersonas < 1, etc.)
- `404 Not Found`: Cliente o mesa no encontrados
- `409 Conflict`: Mesa no disponible (ya reservada) o capacidad insuficiente

**Validaciones**:
- `fechaHoraReserva` debe ser presente o futura
- `numeroPersonas` no debe exceder la capacidad de la mesa
- La mesa no debe tener otra reserva en el rango de tiempo (según duración configurada)

---

### 2. Obtener Todas las Reservas

**GET** `/api/v1/reservas`

**Response**: `ApiResponse<ReservaResponseDTO[]>`

**Status Codes**:
- `200 OK`: Lista de reservas obtenida exitosamente

**Nota**: Solo retorna reservas de la empresa del usuario autenticado.

---

### 3. Obtener Reserva por ID

**GET** `/api/v1/reservas/{id}`

**Path Parameters**:
- `id`: ID de la reserva

**Response**: `ApiResponse<ReservaResponseDTO>`

**Status Codes**:
- `200 OK`: Reserva encontrada
- `404 Not Found`: Reserva no existe o no pertenece a la empresa

---

### 4. Actualizar Reserva

**PUT** `/api/v1/reservas/{id}`

**Path Parameters**:
- `id`: ID de la reserva

**Request Body**: `ReservaUpdateDTO`

**Response**: `ApiResponse<ReservaResponseDTO>`

**Status Codes**:
- `200 OK`: Reserva actualizada exitosamente
- `400 Bad Request`: Validación fallida
- `404 Not Found`: Reserva, cliente o mesa no encontrados
- `409 Conflict`: Nueva mesa no disponible o capacidad insuficiente

---

### 5. Eliminar Reserva

**DELETE** `/api/v1/reservas/{id}`

**Path Parameters**:
- `id`: ID de la reserva

**Response**: `ApiResponse<null>`

**Status Codes**:
- `200 OK`: Reserva eliminada exitosamente
- `404 Not Found`: Reserva no existe o no pertenece a la empresa

---

### 6. Cambiar Estado de Reserva

**PATCH** `/api/v1/reservas/{id}/estado`

**Path Parameters**:
- `id`: ID de la reserva

**Query Parameters**:
- `nuevoEstado`: Nuevo estado de la reserva (EstadoReserva)

**Response**: `ApiResponse<ReservaResponseDTO>`

**Status Codes**:
- `200 OK`: Estado actualizado exitosamente
- `404 Not Found`: Reserva no encontrada

**Ejemplo de URL**: `/api/v1/reservas/123/estado?nuevoEstado=CONFIRMADA`

---

### 7. Buscar Reservas

**GET** `/api/v1/reservas/buscar`

**Query Parameters** (todos opcionales):
- `idCliente`: Filtrar por ID de cliente
- `idMesa`: Filtrar por ID de mesa
- `fechaHoraReservaDesde`: Fecha/hora desde (ISO 8601)
- `fechaHoraReservaHasta`: Fecha/hora hasta (ISO 8601)
- `estado`: Filtrar por estado (EstadoReserva)
- `numeroPersonas`: Filtrar por número de personas exacto
- `searchTerm`: Búsqueda en observaciones
- `logic`: Lógica de combinación de filtros ("AND" | "OR", default: "AND")

**Response**: `ApiResponse<ReservaResponseDTO[]>`

**Status Codes**:
- `200 OK`: Búsqueda exitosa

**Nota**: El filtro por empresa siempre se aplica con AND, independientemente del parámetro `logic`.

**Ejemplo de URLs**:
```
/api/v1/reservas/buscar?estado=PENDIENTE
/api/v1/reservas/buscar?fechaHoraReservaDesde=2025-01-20T00:00:00&fechaHoraReservaHasta=2025-01-21T23:59:59
/api/v1/reservas/buscar?idCliente=5&estado=CONFIRMADA&logic=AND
```

---

### 8. Consultar Disponibilidad de Mesas

**POST** `/api/v1/reservas/disponibilidad`

**Request Body**:
```typescript
{
  fechaHora: string;           // ISO 8601 format
  numeroPersonas: number;
  ubicacion?: string;
}
```

**Response**: `ApiResponse<DisponibilidadResponseDTO>`

**Status Codes**:
- `200 OK`: Consulta exitosa
- `400 Bad Request`: Validación fallida

**Funcionalidad**:
- Retorna todas las mesas de la empresa que:
  - Tengan capacidad suficiente (>= numeroPersonas)
  - Estén en estado DISPONIBLE
  - No tengan reservas conflictivas en el rango de tiempo
  - Coincidan con la ubicación (si se especifica)

---

## Ejemplos de Uso

### Configuración del Cliente HTTP (Axios)

```typescript
// src/api/client.ts
import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar token JWT
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para manejo de errores
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Redirigir a login
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
```

### Servicio de Reservas

```typescript
// src/services/reserva.service.ts
import apiClient from '@/api/client';
import type {
  ReservaRequestDTO,
  ReservaUpdateDTO,
  ReservaResponseDTO,
  DisponibilidadRequestDTO,
  DisponibilidadResponseDTO,
  ApiResponse,
  EstadoReserva,
} from '@/types/reserva.types';

export class ReservaService {
  private readonly basePath = '/api/v1/reservas';

  /**
   * Crear una nueva reserva
   */
  async crear(data: ReservaRequestDTO): Promise<ReservaResponseDTO> {
    const response = await apiClient.post<ApiResponse<ReservaResponseDTO>>(
      this.basePath,
      data
    );
    return response.data.data;
  }

  /**
   * Obtener todas las reservas de la empresa
   */
  async obtenerTodas(): Promise<ReservaResponseDTO[]> {
    const response = await apiClient.get<ApiResponse<ReservaResponseDTO[]>>(
      this.basePath
    );
    return response.data.data;
  }

  /**
   * Obtener una reserva por ID
   */
  async obtenerPorId(id: number): Promise<ReservaResponseDTO> {
    const response = await apiClient.get<ApiResponse<ReservaResponseDTO>>(
      `${this.basePath}/${id}`
    );
    return response.data.data;
  }

  /**
   * Actualizar una reserva existente
   */
  async actualizar(
    id: number,
    data: ReservaUpdateDTO
  ): Promise<ReservaResponseDTO> {
    const response = await apiClient.put<ApiResponse<ReservaResponseDTO>>(
      `${this.basePath}/${id}`,
      data
    );
    return response.data.data;
  }

  /**
   * Eliminar una reserva
   */
  async eliminar(id: number): Promise<void> {
    await apiClient.delete(`${this.basePath}/${id}`);
  }

  /**
   * Cambiar el estado de una reserva
   */
  async cambiarEstado(
    id: number,
    nuevoEstado: EstadoReserva
  ): Promise<ReservaResponseDTO> {
    const response = await apiClient.patch<ApiResponse<ReservaResponseDTO>>(
      `${this.basePath}/${id}/estado`,
      null,
      {
        params: { nuevoEstado },
      }
    );
    return response.data.data;
  }

  /**
   * Buscar reservas con filtros
   */
  async buscar(params: {
    idCliente?: number;
    idMesa?: number;
    fechaHoraReservaDesde?: string;
    fechaHoraReservaHasta?: string;
    estado?: EstadoReserva;
    numeroPersonas?: number;
    searchTerm?: string;
    logic?: 'AND' | 'OR';
  }): Promise<ReservaResponseDTO[]> {
    const response = await apiClient.get<ApiResponse<ReservaResponseDTO[]>>(
      `${this.basePath}/buscar`,
      { params }
    );
    return response.data.data;
  }

  /**
   * Consultar disponibilidad de mesas
   */
  async consultarDisponibilidad(
    data: DisponibilidadRequestDTO
  ): Promise<DisponibilidadResponseDTO> {
    const response = await apiClient.post<
      ApiResponse<DisponibilidadResponseDTO>
    >(`${this.basePath}/disponibilidad`, data);
    return response.data.data;
  }
}

export const reservaService = new ReservaService();
```

---

## Casos de Uso Comunes

### 1. Crear una Reserva Completa

```typescript
// components/CrearReservaForm.vue
<script setup lang="ts">
import { ref } from 'vue';
import { reservaService } from '@/services/reserva.service';
import type { ReservaRequestDTO } from '@/types/reserva.types';

const form = ref<ReservaRequestDTO>({
  idCliente: 0,
  idMesa: 0,
  fechaHoraReserva: '',
  numeroPersonas: 2,
  observaciones: '',
});

const isSubmitting = ref(false);
const error = ref<string | null>(null);

async function crearReserva() {
  try {
    isSubmitting.value = true;
    error.value = null;

    const reserva = await reservaService.crear(form.value);

    console.log('Reserva creada:', reserva);
    // Mostrar notificación de éxito
    // Redirigir o actualizar lista
  } catch (err: any) {
    if (err.response?.status === 409) {
      error.value = 'La mesa no está disponible para esa fecha/hora';
    } else if (err.response?.status === 400) {
      error.value = err.response.data.message || 'Datos inválidos';
    } else {
      error.value = 'Error al crear la reserva';
    }
    console.error('Error:', err);
  } finally {
    isSubmitting.value = false;
  }
}
</script>
```

### 2. Consultar Disponibilidad antes de Reservar

```typescript
// components/SelectorMesasDisponibles.vue
<script setup lang="ts">
import { ref, computed } from 'vue';
import { reservaService } from '@/services/reserva.service';
import type { DisponibilidadResponseDTO } from '@/types/reserva.types';

const fechaHora = ref('');
const numeroPersonas = ref(2);
const ubicacion = ref('');

const disponibilidad = ref<DisponibilidadResponseDTO | null>(null);
const isLoading = ref(false);

const hasMesasDisponibles = computed(
  () => disponibilidad.value && disponibilidad.value.totalDisponibles > 0
);

async function buscarDisponibilidad() {
  try {
    isLoading.value = true;

    disponibilidad.value = await reservaService.consultarDisponibilidad({
      fechaHora: fechaHora.value,
      numeroPersonas: numeroPersonas.value,
      ubicacion: ubicacion.value || undefined,
    });

    if (!hasMesasDisponibles.value) {
      // Mostrar mensaje: "No hay mesas disponibles para esa fecha/hora"
    }
  } catch (err) {
    console.error('Error al consultar disponibilidad:', err);
  } finally {
    isLoading.value = false;
  }
}

function seleccionarMesa(idMesa: number) {
  // Pasar a formulario de reserva con la mesa seleccionada
  emit('mesaSeleccionada', { idMesa, fechaHora: fechaHora.value });
}
</script>

<template>
  <div>
    <!-- Formulario de búsqueda -->
    <button @click="buscarDisponibilidad" :disabled="isLoading">
      Buscar Disponibilidad
    </button>

    <!-- Lista de mesas disponibles -->
    <div v-if="disponibilidad">
      <p>{{ disponibilidad.totalDisponibles }} mesas disponibles</p>
      <div
        v-for="mesa in disponibilidad.mesasDisponibles"
        :key="mesa.idMesa"
        @click="seleccionarMesa(mesa.idMesa)"
      >
        Mesa {{ mesa.numeroMesa }} - Capacidad: {{ mesa.capacidad }}
      </div>
    </div>
  </div>
</template>
```

### 3. Dashboard de Reservas del Día

```typescript
// components/DashboardReservas.vue
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { reservaService } from '@/services/reserva.service';
import { EstadoReserva } from '@/types/reserva.types';
import type { ReservaResponseDTO } from '@/types/reserva.types';

const reservasDelDia = ref<ReservaResponseDTO[]>([]);
const isLoading = ref(false);

onMounted(async () => {
  await cargarReservasDelDia();
});

async function cargarReservasDelDia() {
  try {
    isLoading.value = true;

    const hoy = new Date();
    const inicioDia = new Date(hoy.setHours(0, 0, 0, 0)).toISOString();
    const finDia = new Date(hoy.setHours(23, 59, 59, 999)).toISOString();

    reservasDelDia.value = await reservaService.buscar({
      fechaHoraReservaDesde: inicioDia,
      fechaHoraReservaHasta: finDia,
    });
  } catch (err) {
    console.error('Error al cargar reservas:', err);
  } finally {
    isLoading.value = false;
  }
}

async function confirmarReserva(id: number) {
  try {
    await reservaService.cambiarEstado(id, EstadoReserva.CONFIRMADA);
    await cargarReservasDelDia(); // Recargar lista
  } catch (err) {
    console.error('Error al confirmar reserva:', err);
  }
}

async function marcarEnCurso(id: number) {
  try {
    // Cliente llegó y se sentó en la mesa
    await reservaService.cambiarEstado(id, EstadoReserva.EN_CURSO);
    await cargarReservasDelDia();
  } catch (err) {
    console.error('Error al actualizar reserva:', err);
  }
}

async function completarReserva(id: number) {
  try {
    await reservaService.cambiarEstado(id, EstadoReserva.COMPLETADA);
    await cargarReservasDelDia();
  } catch (err) {
    console.error('Error al completar reserva:', err);
  }
}

function obtenerColorEstado(estado: EstadoReserva): string {
  const colores = {
    [EstadoReserva.PENDIENTE]: 'warning',
    [EstadoReserva.CONFIRMADA]: 'info',
    [EstadoReserva.EN_CURSO]: 'primary',
    [EstadoReserva.COMPLETADA]: 'success',
    [EstadoReserva.CANCELADA]: 'error',
    [EstadoReserva.NO_PRESENTADO]: 'error',
  };
  return colores[estado];
}
</script>

<template>
  <div>
    <h2>Reservas del Día</h2>
    <div v-if="isLoading">Cargando...</div>
    <div v-else>
      <div v-for="reserva in reservasDelDia" :key="reserva.idReserva">
        <div :class="obtenerColorEstado(reserva.estado)">
          <!-- Información de la reserva -->
          <p>Mesa {{ reserva.mesa.numeroMesa }}</p>
          <p>{{ reserva.cliente.nombre }} {{ reserva.cliente.apellido }}</p>
          <p>{{ new Date(reserva.fechaHoraReserva).toLocaleTimeString() }}</p>
          <p>{{ reserva.numeroPersonas }} personas</p>
          <p>Estado: {{ reserva.estado }}</p>

          <!-- Botones de acción según el estado -->
          <button
            v-if="reserva.estado === EstadoReserva.PENDIENTE"
            @click="confirmarReserva(reserva.idReserva)"
          >
            Confirmar
          </button>
          <button
            v-if="reserva.estado === EstadoReserva.CONFIRMADA"
            @click="marcarEnCurso(reserva.idReserva)"
          >
            Cliente Llegó
          </button>
          <button
            v-if="reserva.estado === EstadoReserva.EN_CURSO"
            @click="completarReserva(reserva.idReserva)"
          >
            Finalizar
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
```

### 4. Filtrar Reservas por Cliente

```typescript
// components/HistorialReservasCliente.vue
<script setup lang="ts">
import { ref, watch } from 'vue';
import { reservaService } from '@/services/reserva.service';
import type { ReservaResponseDTO } from '@/types/reserva.types';

const props = defineProps<{
  idCliente: number;
}>();

const reservas = ref<ReservaResponseDTO[]>([]);

watch(
  () => props.idCliente,
  async (nuevoId) => {
    if (nuevoId) {
      reservas.value = await reservaService.buscar({ idCliente: nuevoId });
    }
  },
  { immediate: true }
);
</script>
```

---

## Manejo de Errores

### Códigos de Error Comunes

| Código | Significado | Acción Recomendada |
|--------|-------------|-------------------|
| 400 | Validación fallida | Revisar campos del formulario |
| 401 | No autenticado | Redirigir a login |
| 404 | Recurso no encontrado | Mostrar mensaje y volver al listado |
| 409 | Conflicto (mesa ocupada, capacidad excedida) | Sugerir mesas alternativas |
| 500 | Error del servidor | Mostrar mensaje genérico y reintentar |

### Wrapper de Manejo de Errores

```typescript
// utils/error-handler.ts
import { AxiosError } from 'axios';

export interface ErrorResponse {
  message: string;
  statusCode: number;
}

export function handleApiError(error: unknown): ErrorResponse {
  if (error instanceof AxiosError) {
    const status = error.response?.status || 500;
    const message = error.response?.data?.message || 'Error desconocido';

    return { message, statusCode: status };
  }

  return {
    message: 'Error inesperado',
    statusCode: 500,
  };
}

// Uso en componentes
try {
  await reservaService.crear(form.value);
} catch (error) {
  const { message, statusCode } = handleApiError(error);

  if (statusCode === 409) {
    // Mesa no disponible - mostrar mesas alternativas
    showAlternativasModal.value = true;
  } else {
    errorMessage.value = message;
  }
}
```

---

## Notas Finales

### Configuración de Duración de Reserva

La duración de cada reserva es configurable por empresa. Por defecto son 2 horas, pero puede variar. Esta configuración se usa para:
- Validar disponibilidad de mesas
- Detectar conflictos entre reservas
- Calcular la hora de fin estimada

### Buenas Prácticas

1. **Validación del lado del cliente**: Validar fechas futuras y capacidades antes de enviar al servidor
2. **Debounce en búsquedas**: Implementar debounce en filtros de búsqueda para reducir llamadas API
3. **Cache local**: Considerar cache de mesas disponibles por corto tiempo
4. **Actualización en tiempo real**: Usar WebSockets o polling para dashboard de reservas activas
5. **Formateo de fechas**: Usar librerías como `date-fns` o `dayjs` para manejo consistente de fechas

### Próximas Funcionalidades

- Notificaciones automáticas (email/SMS)
- Lista de espera
- Reservas recurrentes
- Integración con sistema de pedidos
- Widget público de reservas

---

**Fecha de última actualización**: 2025-01-16
**Versión del API**: v1
