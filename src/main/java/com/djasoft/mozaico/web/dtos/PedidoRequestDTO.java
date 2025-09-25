package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.pedido.EstadoPedido;
import com.djasoft.mozaico.domain.enums.pedido.TipoServicio;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRequestDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo.")
    private Integer idCliente;

    @NotNull(message = "El ID de la mesa no puede ser nulo.")
    private Integer idMesa;

    @NotNull(message = "El ID del empleado no puede ser nulo.")
    private Long idEmpleado;

    private EstadoPedido estado;

    private TipoServicio tipoServicio;

    private String observaciones;

    private String direccionDelivery;

    // Los campos subtotal, impuestos, descuento y total se calcularán en el backend
    // o se actualizarán a través de la gestión de detalle_pedidos.
    // No se incluyen en el request inicial.
}
