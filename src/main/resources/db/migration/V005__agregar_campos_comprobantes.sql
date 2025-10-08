-- Migración para agregar campos de auditoría y control de impresiones a comprobantes

-- Agregar campos de control de impresiones
ALTER TABLE comprobantes
    ADD COLUMN IF NOT EXISTS contador_impresiones INTEGER DEFAULT 0,
    ADD COLUMN IF NOT EXISTS fecha_primera_impresion TIMESTAMP,
    ADD COLUMN IF NOT EXISTS fecha_anulacion TIMESTAMP,
    ADD COLUMN IF NOT EXISTS usuario_anulacion VARCHAR(255),
    ADD COLUMN IF NOT EXISTS fecha_envio_digital TIMESTAMP,
    ADD COLUMN IF NOT EXISTS email_envio VARCHAR(255);

-- Actualizar comprobantes existentes con contador en 0 si es null
UPDATE comprobantes
SET contador_impresiones = 0
WHERE contador_impresiones IS NULL;

-- Comentarios para documentación
COMMENT ON COLUMN comprobantes.contador_impresiones IS 'Número de veces que se ha impreso/descargado el comprobante';
COMMENT ON COLUMN comprobantes.fecha_primera_impresion IS 'Fecha y hora de la primera impresión';
COMMENT ON COLUMN comprobantes.fecha_anulacion IS 'Fecha y hora en que se anuló el comprobante';
COMMENT ON COLUMN comprobantes.usuario_anulacion IS 'Usuario que anuló el comprobante';
COMMENT ON COLUMN comprobantes.fecha_envio_digital IS 'Fecha y hora del envío por email/WhatsApp';
COMMENT ON COLUMN comprobantes.email_envio IS 'Email al que se envió el comprobante';
