package com.djasoft.mozaico.domain.enums.pedido;

public enum EstadoPedido {
    ABIERTO,      // Mesa ocupada, pueden seguir pidiendo productos
    ATENDIDO,     // Cliente terminó de pedir, esperando finalizar consumo
    PAGADO,       // Mesa pagó, se libera
    CANCELADO     // Mesa cancelada, se libera
}
