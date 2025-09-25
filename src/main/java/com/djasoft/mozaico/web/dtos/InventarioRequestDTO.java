package com.djasoft.mozaico.web.dtos;

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
public class InventarioRequestDTO {

    @NotNull(message = "El ID del producto no puede ser nulo.")
    private Long idProducto;

    @NotNull(message = "El stock actual no puede ser nulo.")
    @Min(value = 0, message = "El stock actual debe ser al menos 0.")
    private Integer stockActual;

    @Min(value = 0, message = "El stock mínimo debe ser al menos 0.")
    private Integer stockMinimo;

    @Min(value = 0, message = "El stock máximo debe ser al menos 0.")
    private Integer stockMaximo;

    @Min(value = 0, message = "El costo unitario debe ser al menos 0.")
    private BigDecimal costoUnitario;
}
