-- Migración para agregar el estado LISTO al enum EstadoDetallePedido
-- Flujo actualizado: PEDIDO -> EN_PREPARACION -> LISTO -> SERVIDO

-- Para PostgreSQL:
-- ALTER TYPE estado_detalle_pedido ADD VALUE IF NOT EXISTS 'LISTO' BEFORE 'SERVIDO';

-- Para MySQL (modificar la columna con el nuevo enum):
-- Nota: En MySQL/MariaDB, necesitamos recrear el enum con todos los valores en el orden correcto

-- Primero verificamos si la columna ya tiene el valor 'LISTO'
-- Si no existe, agregamos el nuevo estado al enum

-- Para MySQL/MariaDB:
ALTER TABLE detalle_pedidos MODIFY COLUMN estado
    ENUM('PEDIDO', 'EN_PREPARACION', 'LISTO', 'SERVIDO', 'CANCELADO')
    DEFAULT 'PEDIDO';

-- No necesitamos migrar datos existentes porque:
-- 1. Si un detalle está en 'EN_PREPARACION', puede seguir ahí hasta que cocina lo marque como LISTO
-- 2. Si un detalle está en 'SERVIDO', ya fue entregado (no necesita cambiar)
-- 3. El nuevo estado 'LISTO' solo se usará para nuevos cambios de estado

-- Comentario para el equipo:
-- A partir de ahora, el flujo correcto es:
-- PEDIDO (cliente ordena)
--   -> EN_PREPARACION (cocina está preparando)
--   -> LISTO (cocina terminó, mesero debe recoger)
--   -> SERVIDO (mesero entregó al cliente)
