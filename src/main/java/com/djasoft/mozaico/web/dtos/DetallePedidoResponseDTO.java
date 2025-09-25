package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoResponseDTO {
    private Integer idDetalle;
    private PedidoResponseDTO pedido;
    private ProductoResponseDTO producto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String observaciones;
    private EstadoDetallePedido estado;
}
