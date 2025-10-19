-- Agregar campo de duración de reserva configurable por empresa
ALTER TABLE empresas ADD COLUMN duracion_reserva_horas INT NOT NULL DEFAULT 2;

-- Comentarios para documentar el campo
COMMENT ON COLUMN empresas.duracion_reserva_horas IS 'Duración en horas de cada reserva de mesa. Por defecto 2 horas.';
