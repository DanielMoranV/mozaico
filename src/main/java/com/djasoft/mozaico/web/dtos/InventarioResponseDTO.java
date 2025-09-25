package com.djasoft.mozaico.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventarioResponseDTO {
    private Integer idInventario;
    private ProductoResponseDTO producto;
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private BigDecimal costoUnitario;
    private LocalDateTime fechaActualizacion;
}
