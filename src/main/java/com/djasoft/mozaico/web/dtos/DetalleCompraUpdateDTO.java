package com.djasoft.mozaico.web.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DetalleCompraUpdateDTO {

    @NotNull(message = "La cantidad no puede ser nula.")
    @Positive(message = "La cantidad debe ser un número positivo.")
    private Integer cantidad;
}
