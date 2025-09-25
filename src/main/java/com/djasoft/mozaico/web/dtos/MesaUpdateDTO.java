package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesaUpdateDTO {

    private Integer numeroMesa;

    @Min(value = 1, message = "La capacidad debe ser al menos 1.")
    private Integer capacidad;

    private String ubicacion;

    private EstadoMesa estado;

    private String observaciones;
}
