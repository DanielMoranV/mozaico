package com.djasoft.mozaico.web.dtos.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockItemResponseDTO {
    private Long idProducto;
    private String nombreProducto;
    private Integer stockActual;
    private Integer stockMinimo;
    // private String nombreProveedor; // Optional
}
