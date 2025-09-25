package com.djasoft.mozaico.web.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MenuUpdateDTO {
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Boolean disponible;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
