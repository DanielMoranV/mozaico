package com.djasoft.mozaico.domain.enums.detallepedido;

public enum EstadoDetallePedido {
    PEDIDO,           // Cliente pidió el producto
    EN_PREPARACION,   // Cocina preparando
    LISTO,            // Cocina terminó, listo para servir
    SERVIDO,          // Producto entregado al cliente
    CANCELADO         // Producto cancelado
}
