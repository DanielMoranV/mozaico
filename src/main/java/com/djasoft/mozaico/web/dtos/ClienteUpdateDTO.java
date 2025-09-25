package com.djasoft.mozaico.web.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteUpdateDTO {

    private String nombre;

    private String apellido;

    @Email(message = "El formato del email no es válido.")
    private String email;

    private String telefono;

    @Past(message = "La fecha de nacimiento debe ser en el pasado.")
    private LocalDate fechaNacimiento;

    private String direccion;

    private String preferenciasAlimentarias;
    
    private Integer puntosFidelidad;

    private Boolean activo;
}
