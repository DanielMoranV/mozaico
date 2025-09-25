package com.djasoft.mozaico.web.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPagoRequestDTO {

    @NotBlank(message = "El nombre del método de pago no puede estar vacío.")
    private String nombre;

    private Boolean activo;
}
