package com.djasoft.mozaico.web.dtos;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorUpdateDTO {

    private String nombre;

    private String contacto;

    private String telefono;

    @Email(message = "El formato del email no es válido.")
    private String email;

    private String direccion;

    private Boolean activo;
}
