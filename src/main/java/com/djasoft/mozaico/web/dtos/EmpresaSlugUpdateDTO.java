package com.djasoft.mozaico.web.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar el slug de una empresa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaSlugUpdateDTO {

    @NotBlank(message = "El slug no puede estar vacío")
    @Pattern(
        regexp = "^[a-z0-9]+(-[a-z0-9]+)*$",
        message = "El slug solo puede contener letras minúsculas, números y guiones. " +
                  "No puede empezar ni terminar con guión, ni tener guiones consecutivos."
    )
    private String slug;
}
