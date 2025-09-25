package com.djasoft.mozaico.web.dtos;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaUpdateDTO {

    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres.")
    private String nombre;

    private String descripcion;
}
