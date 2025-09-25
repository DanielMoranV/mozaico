package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.compra.EstadoCompra;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRequestDTO {

    @NotNull(message = "El ID del proveedor no puede ser nulo.")
    private Integer idProveedor;

    @NotNull(message = "La fecha de compra no puede ser nula.")
    private LocalDate fechaCompra;

    @NotNull(message = "El total de la compra no puede ser nulo.")
    @Min(value = 0, message = "El total de la compra debe ser al menos 0.")
    private BigDecimal total;

    private EstadoCompra estado;

    private String observaciones;
}
