-- Migración para agregar campos de timestamp a detalle_pedidos
-- Mejora el ordenamiento en KDS para mostrar items correctamente según su estado

-- Agregar campo fecha_creacion (timestamp de cuando se creó el detalle)
ALTER TABLE detalle_pedidos
ADD COLUMN fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Agregar campo fecha_estado_actualizado (timestamp de cuando cambió de estado)
ALTER TABLE detalle_pedidos
ADD COLUMN fecha_estado_actualizado TIMESTAMP NULL;

-- Comentarios para documentar el propósito de los campos
COMMENT ON COLUMN detalle_pedidos.fecha_creacion IS 'Fecha y hora de creación del detalle del pedido';
COMMENT ON COLUMN detalle_pedidos.fecha_estado_actualizado IS 'Fecha y hora del último cambio de estado (para ordenamiento en KDS)';

-- Crear índices para mejorar el rendimiento de las consultas del KDS
CREATE INDEX idx_detalle_pedidos_fecha_creacion ON detalle_pedidos(fecha_creacion);
CREATE INDEX idx_detalle_pedidos_fecha_estado ON detalle_pedidos(fecha_estado_actualizado);
CREATE INDEX idx_detalle_pedidos_estado_fecha ON detalle_pedidos(estado, fecha_creacion);

-- Inicializar fecha_creacion para registros existentes (usar fecha del pedido como aproximación)
UPDATE detalle_pedidos dp
SET fecha_creacion = p.fecha_pedido
FROM pedidos p
WHERE dp.id_pedido = p.id_pedido
AND dp.fecha_creacion IS NULL;

-- Hacer el campo fecha_creacion NOT NULL después de inicializar los datos existentes
ALTER TABLE detalle_pedidos
ALTER COLUMN fecha_creacion SET NOT NULL;
