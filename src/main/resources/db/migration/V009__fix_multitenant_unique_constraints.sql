-- ====================================================================
-- V009 - Corregir constraints UNIQUE para arquitectura multitenant
-- ====================================================================
--
-- PROBLEMA: Los constraints UNIQUE actuales no consideran el scope de empresa
-- causando conflictos cuando múltiples empresas intentan usar los mismos valores.
--
-- SOLUCIÓN: Reemplazar constraints UNIQUE simples por índices UNIQUE compuestos
-- que incluyan id_empresa, permitiendo que cada empresa tenga sus propios datos.
--
-- ====================================================================

-- 1. TABLA: categorias
-- Permitir que diferentes empresas tengan categorías con el mismo nombre
ALTER TABLE categorias DROP CONSTRAINT IF EXISTS uk_categorias_nombre;
DROP INDEX IF EXISTS uk_categorias_nombre;

CREATE UNIQUE INDEX uk_categorias_nombre_empresa
ON categorias(nombre, id_empresa);

-- 2. TABLA: mesas
-- Permitir que diferentes empresas tengan mesas con el mismo número
ALTER TABLE mesas DROP CONSTRAINT IF EXISTS uk_mesas_numero_mesa;
DROP INDEX IF EXISTS uk_mesas_numero_mesa;

CREATE UNIQUE INDEX uk_mesas_numero_empresa
ON mesas(numero_mesa, id_empresa);

-- 3. TABLA: clientes
-- Permitir que diferentes empresas tengan clientes con el mismo email
ALTER TABLE clientes DROP CONSTRAINT IF EXISTS uk_clientes_email;
DROP INDEX IF EXISTS uk_clientes_email;

CREATE UNIQUE INDEX uk_clientes_email_empresa
ON clientes(email, id_empresa) WHERE email IS NOT NULL;

-- 4. TABLA: usuarios
-- Permitir que diferentes empresas tengan usuarios con mismo username/email/documento
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS uk_usuarios_username;
DROP INDEX IF EXISTS uk_usuarios_username;

ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS uk_usuarios_email;
DROP INDEX IF EXISTS uk_usuarios_email;

ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS uk_usuarios_numero_documento_identidad;
DROP INDEX IF EXISTS uk_usuarios_numero_documento_identidad;

CREATE UNIQUE INDEX uk_usuarios_username_empresa
ON usuarios(username, id_empresa);

CREATE UNIQUE INDEX uk_usuarios_email_empresa
ON usuarios(email, id_empresa);

CREATE UNIQUE INDEX uk_usuarios_documento_empresa
ON usuarios(numero_documento_identidad, id_empresa);

-- 5. TABLA: comprobantes
-- El número de comprobante puede repetirse entre empresas
ALTER TABLE comprobantes DROP CONSTRAINT IF EXISTS uk_comprobantes_numero;
DROP INDEX IF EXISTS uk_comprobantes_numero;

-- Los comprobantes no tienen id_empresa directamente, lo obtienen a través del pago
-- Por ahora no agregamos constraint, se validará a nivel de aplicación

-- ====================================================================
-- VERIFICACIÓN
-- ====================================================================
-- Para verificar los nuevos índices creados, ejecutar:
-- SELECT indexname, indexdef FROM pg_indexes WHERE tablename IN
-- ('categorias', 'mesas', 'clientes', 'usuarios', 'comprobantes')
-- AND indexname LIKE 'uk_%';
