package com.djasoft.mozaico.domain.enums.reserva;

public enum EstadoReserva {
    PENDIENTE,      // Reserva creada, esperando confirmación
    CONFIRMADA,     // Reserva confirmada por el restaurante/cliente
    EN_CURSO,       // Cliente llegó y está ocupando la mesa
    COMPLETADA,     // Reserva finalizada exitosamente
    CANCELADA,      // Reserva cancelada por cliente o restaurante
    NO_PRESENTADO   // Cliente no se presentó (no-show)
}
