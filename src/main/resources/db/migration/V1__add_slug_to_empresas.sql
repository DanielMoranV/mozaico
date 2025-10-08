-- Agregar columna slug a la tabla empresas
-- Esta columna almacenará el identificador único para URLs públicas

-- Paso 1: Agregar la columna como nullable primero
ALTER TABLE empresas ADD COLUMN IF NOT EXISTS slug VARCHAR(100);

-- Paso 2: Actualizar empresas existentes con slug generado del nombre
-- Convierte "Restaurante Mozaico" -> "restaurante-mozaico"
UPDATE empresas
SET slug = LOWER(
    REGEXP_REPLACE(
        REGEXP_REPLACE(
            TRIM(nombre),
            '[^a-zA-Z0-9\s-]', '', 'g'  -- Eliminar caracteres especiales
        ),
        '\s+', '-', 'g'  -- Reemplazar espacios con guiones
    )
)
WHERE slug IS NULL;

-- Paso 3: Agregar constraint de unique y not null
ALTER TABLE empresas ALTER COLUMN slug SET NOT NULL;
ALTER TABLE empresas ADD CONSTRAINT uk_empresas_slug UNIQUE (slug);

-- Paso 4: Crear índice para búsquedas rápidas
CREATE INDEX IF NOT EXISTS idx_empresas_slug ON empresas(slug);
