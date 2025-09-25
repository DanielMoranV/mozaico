package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoUpdateDTO {

    private Integer idPedido;
    private Long idProducto;

    @Min(value = 1, message = "La cantidad debe ser al menos 1.")
    private Integer cantidad;

    @Min(value = 0, message = "El precio unitario debe ser al menos 0.")
    private BigDecimal precioUnitario;

    private String observaciones;

    private EstadoDetallePedido estado;
}
