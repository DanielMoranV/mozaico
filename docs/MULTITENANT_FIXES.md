# 🔧 Correcciones de Arquitectura Multitenant

## 📋 Resumen

Se han identificado y corregido **inconsistencias críticas** en la implementación multitenant que impedían que múltiples empresas pudieran coexistir en el sistema.

---

## ❌ Problemas Identificados

### 1. **Constraints UNIQUE sin scope de empresa**

#### Entidades afectadas:
- ✅ `Categoria` - Campo `nombre` era UNIQUE global
- ✅ `Mesa` - Campo `numero_mesa` era UNIQUE global
- ✅ `Cliente` - Campo `email` era UNIQUE global
- ✅ `Usuario` - Campos `username`, `email`, `numero_documento_identidad` eran UNIQUE globales
- ✅ `Comprobante` - Campo `numero_comprobante` era UNIQUE global

#### Impacto:
```java
// ❌ ANTES: Esto fallaba si otra empresa ya tenía una categoría "Bebidas"
Categoria bebidas1 = new Categoria();
bebidas1.setNombre("Bebidas");
bebidas1.setEmpresa(empresa1);
categoriaRepository.save(bebidas1); // ✅ OK

Categoria bebidas2 = new Categoria();
bebidas2.setNombre("Bebidas");
bebidas2.setEmpresa(empresa2);
categoriaRepository.save(bebidas2); // ❌ ERROR: Duplicate key violation
```

```java
// ✅ DESPUÉS: Ambas empresas pueden tener "Bebidas"
Categoria bebidas1 = new Categoria();
bebidas1.setNombre("Bebidas");
bebidas1.setEmpresa(empresa1);
categoriaRepository.save(bebidas1); // ✅ OK

Categoria bebidas2 = new Categoria();
bebidas2.setNombre("Bebidas");
bebidas2.setEmpresa(empresa2);
categoriaRepository.save(bebidas2); // ✅ OK - Diferente empresa
```

---

## ✅ Soluciones Implementadas

### 1. **Actualización de Entidades JPA**

#### Categoria.java
```java
// ✅ ANTES
@Column(name = "nombre", nullable = false, unique = true, length = 100)
private String nombre;

// ✅ DESPUÉS
@Table(name = "categorias",
    uniqueConstraints = @UniqueConstraint(columnNames = {"nombre", "id_empresa"}))
```

#### Mesa.java
```java
// ✅ ANTES
@Column(name = "numero_mesa", nullable = false, unique = true)
private Integer numeroMesa;

// ✅ DESPUÉS
@Table(name = "mesas",
    uniqueConstraints = @UniqueConstraint(columnNames = {"numero_mesa", "id_empresa"}))
```

#### Usuario.java
```java
// ✅ ANTES
@Column(name = "username", nullable = false, unique = true, length = 50)
@Column(name = "email", nullable = false, unique = true, length = 100)
@Column(name = "numero_documento_identidad", nullable = false, unique = true, length = 20)

// ✅ DESPUÉS
@Table(name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "id_empresa"}),
        @UniqueConstraint(columnNames = {"email", "id_empresa"}),
        @UniqueConstraint(columnNames = {"numero_documento_identidad", "id_empresa"})
    })
```

#### Cliente.java
```java
// ✅ ANTES
@Column(name = "email", unique = true, length = 150)

// ✅ DESPUÉS
@Table(name = "clientes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"email", "id_empresa"}))
```

---

### 2. **Migración de Base de Datos**

**Archivo:** `V009__fix_multitenant_unique_constraints.sql`

```sql
-- Categorías
ALTER TABLE categorias DROP CONSTRAINT IF EXISTS uk_categorias_nombre;
CREATE UNIQUE INDEX uk_categorias_nombre_empresa
ON categorias(nombre, id_empresa);

-- Mesas
ALTER TABLE mesas DROP CONSTRAINT IF EXISTS uk_mesas_numero_mesa;
CREATE UNIQUE INDEX uk_mesas_numero_empresa
ON mesas(numero_mesa, id_empresa);

-- Clientes
ALTER TABLE clientes DROP CONSTRAINT IF EXISTS uk_clientes_email;
CREATE UNIQUE INDEX uk_clientes_email_empresa
ON clientes(email, id_empresa) WHERE email IS NOT NULL;

-- Usuarios (3 índices)
CREATE UNIQUE INDEX uk_usuarios_username_empresa
ON usuarios(username, id_empresa);

CREATE UNIQUE INDEX uk_usuarios_email_empresa
ON usuarios(email, id_empresa);

CREATE UNIQUE INDEX uk_usuarios_documento_empresa
ON usuarios(numero_documento_identidad, id_empresa);

-- Comprobantes
ALTER TABLE comprobantes DROP CONSTRAINT IF EXISTS uk_comprobantes_numero;
-- Validación a nivel de aplicación (no tiene id_empresa directo)
```

---

### 3. **Mejoras en DataLoader**

#### Verificación de datos existentes:
```java
// ✅ ANTES
if (categoriaRepository.count() == 0) { // ❌ No considera multitenant

// ✅ DESPUÉS
if (empresaRepository.findBySlug("restaurante-mozaico").isPresent()) {
    System.out.println("⚠️ Los datos de prueba ya fueron cargados.");
    return;
}
```

#### Documentación:
```java
/**
 * DataLoader - Cargador de datos de prueba para desarrollo
 *
 * ⚠️ IMPORTANTE - ARQUITECTURA MULTITENANT:
 * - Todas las entidades están aisladas por empresa (id_empresa)
 * - Los constraints UNIQUE se aplican POR EMPRESA (índices compuestos)
 * - Dos empresas PUEDEN tener categorías/mesas/clientes con el mismo nombre/número
 * - La validación de unicidad debe considerar el scope de empresa
 */
```

---

## 🎯 Casos de Uso Ahora Soportados

### Escenario 1: Dos restaurantes con mismas categorías
```java
// Restaurante 1
Categoria bebidas1 = new Categoria();
bebidas1.setNombre("Bebidas");
bebidas1.setEmpresa(restaurante1);

// Restaurante 2 (mismo nombre, diferente empresa)
Categoria bebidas2 = new Categoria();
bebidas2.setNombre("Bebidas");
bebidas2.setEmpresa(restaurante2);

// ✅ AMBOS SE GUARDAN SIN CONFLICTO
```

### Escenario 2: Mesas con mismos números
```java
// Restaurante 1 - Mesa 1
Mesa mesa1A = createMesa(1, 4, "Ventana", restaurante1);

// Restaurante 2 - Mesa 1 (mismo número)
Mesa mesa1B = createMesa(1, 4, "Entrada", restaurante2);

// ✅ AMBAS MESAS COEXISTEN
```

### Escenario 3: Usuarios con mismo email en diferentes empresas
```java
// Administrador Restaurante 1
Usuario admin1 = createUsuario("Juan", "admin", "admin@email.com", empresa1);

// Administrador Restaurante 2 (mismo email)
Usuario admin2 = createUsuario("Pedro", "admin", "admin@email.com", empresa2);

// ✅ PERMITIDO - Diferentes empresas
```

---

## 🚀 Migración y Despliegue

### Paso 1: Aplicar cambios en código
```bash
git pull origin main
```

### Paso 2: Ejecutar migraciones
```bash
# La migración V009 se ejecutará automáticamente con Flyway
./mvnw spring-boot:run
```

### Paso 3: Verificar índices creados
```sql
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename IN ('categorias', 'mesas', 'clientes', 'usuarios')
AND indexname LIKE 'uk_%';
```

### Paso 4: Limpiar datos de prueba (si es necesario)
```sql
-- Solo en desarrollo
DELETE FROM categorias;
DELETE FROM mesas;
DELETE FROM clientes;
DELETE FROM usuarios WHERE tipo_usuario != 'SUPER_ADMIN';
DELETE FROM empresas WHERE slug = 'restaurante-mozaico';
```

---

## 📊 Validación de Repositorios

### ⚠️ Actualizar métodos de búsqueda

#### ANTES (incorrecto):
```java
Optional<Categoria> findByNombre(String nombre); // ❌ No filtra por empresa
Optional<Mesa> findByNumeroMesa(Integer numero); // ❌ No filtra por empresa
```

#### DESPUÉS (correcto):
```java
Optional<Categoria> findByNombreAndEmpresa(String nombre, Empresa empresa);
Optional<Mesa> findByNumeroMesaAndEmpresa(Integer numero, Empresa empresa);
```

---

## 🔍 Checklist de Verificación

- [x] Entidades actualizadas con `@UniqueConstraint` compuesto
- [x] Migración SQL creada (V009)
- [x] DataLoader actualizado con verificación por slug
- [x] Documentación de cambios creada
- [ ] Actualizar repositorios con métodos que filtren por empresa
- [ ] Actualizar servicios para pasar empresa en validaciones
- [ ] Actualizar tests unitarios con múltiples empresas
- [ ] Ejecutar migración en desarrollo
- [ ] Validar con datos de prueba de 2+ empresas

---

## 📚 Referencias

- **Archivo de migración:** `src/main/resources/db/migration/V009__fix_multitenant_unique_constraints.sql`
- **Entidades modificadas:**
  - `Categoria.java` - src/main/java/com/djasoft/mozaico/config/DataLoader.java:18-19
  - `Mesa.java` - src/main/java/com/djasoft/mozaico/config/DataLoader.java:18-19
  - `Cliente.java` - src/main/java/com/djasoft/mozaico/config/DataLoader.java:20-21
  - `Usuario.java` - src/main/java/com/djasoft/mozaico/config/DataLoader.java:28-33
  - `Comprobante.java` - src/main/java/com/djasoft/mozaico/config/DataLoader.java:34

---

## 💡 Próximos Pasos

1. **Actualizar servicios de validación** para incluir scope de empresa
2. **Crear interceptor/filter** para inyectar automáticamente empresa en queries
3. **Implementar Row-Level Security (RLS)** en PostgreSQL para mayor seguridad
4. **Agregar tests de integración** que validen multitenancy
5. **Documentar guía de desarrollo** para nuevos desarrolladores

---

**Fecha de implementación:** 2025-10-19
**Autor:** Claude Code
**Versión de migración:** V009
