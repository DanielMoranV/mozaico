package com.djasoft.mozaico.web.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DetalleCompraRequestDTO {

    @NotNull(message = "El ID de la compra no puede ser nulo.")
    private Integer idCompra;

    @NotNull(message = "El ID del producto no puede ser nulo.")
    private Long idProducto;

    @NotNull(message = "La cantidad no puede ser nula.")
    @Positive(message = "La cantidad debe ser un número positivo.")
    private Integer cantidad;
}
