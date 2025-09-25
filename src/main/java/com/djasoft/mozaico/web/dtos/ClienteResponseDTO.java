package com.djasoft.mozaico.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {
    private Integer idCliente;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    private String direccion;
    private String preferenciasAlimentarias;
    private Integer puntosFidelidad;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
}
