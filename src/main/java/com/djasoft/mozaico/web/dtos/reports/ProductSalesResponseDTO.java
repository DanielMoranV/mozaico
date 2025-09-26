package com.djasoft.mozaico.web.dtos.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesResponseDTO {
    private Long idProducto;
    private String nombreProducto;
    private String nombreCategoria;
    private Long cantidadVendida;
    private BigDecimal totalVentas;
}
