package com.djasoft.mozaico.web.dtos;

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
public class InventarioUpdateDTO {

    private Long idProducto;

    @Min(value = 0, message = "El stock actual debe ser al menos 0.")
    private Integer stockActual;

    @Min(value = 0, message = "El stock mínimo debe ser al menos 0.")
    private Integer stockMinimo;

    @Min(value = 0, message = "El stock máximo debe ser al menos 0.")
    private Integer stockMaximo;

    @Min(value = 0, message = "El costo unitario debe ser al menos 0.")
    private BigDecimal costoUnitario;
}
