package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesaRequestDTO {

    @NotNull(message = "El número de mesa no puede ser nulo.")
    private Integer numeroMesa;

    @NotNull(message = "La capacidad no puede ser nula.")
    @Min(value = 1, message = "La capacidad debe ser al menos 1.")
    private Integer capacidad;

    private String ubicacion;

    private String observaciones;

    private EstadoMesa estado;
}
