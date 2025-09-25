package com.djasoft.mozaico.web.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class DetalleCompraResponseDTO {

    private Integer idDetalleCompra;
    private Integer idCompra;
    private ProductoResponseDTO producto;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
