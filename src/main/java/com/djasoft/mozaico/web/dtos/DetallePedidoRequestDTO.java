package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoRequestDTO {

    @NotNull(message = "El ID del pedido no puede ser nulo.")
    private Integer idPedido;

    @NotNull(message = "El ID del producto no puede ser nulo.")
    private Long idProducto;

    @NotNull(message = "La cantidad no puede ser nula.")
    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    private Integer cantidad;

    @NotNull(message = "El precio unitario no puede ser nulo.")
    @Min(value = 0, message = "El precio unitario debe ser al menos 0.")
    private BigDecimal precioUnitario;

    private String observaciones;

    private EstadoDetallePedido estado;
}
