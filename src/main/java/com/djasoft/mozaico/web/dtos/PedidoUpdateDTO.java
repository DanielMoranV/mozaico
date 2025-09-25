package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.pedido.EstadoPedido;
import com.djasoft.mozaico.domain.enums.pedido.TipoServicio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoUpdateDTO {

    private Integer idCliente;
    private Integer idMesa;
    private Long idEmpleado;
    private EstadoPedido estado;
    private TipoServicio tipoServicio;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal descuento;
    private BigDecimal total;
    private String observaciones;
    private String direccionDelivery;
}
