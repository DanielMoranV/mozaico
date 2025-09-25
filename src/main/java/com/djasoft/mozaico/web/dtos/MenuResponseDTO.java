package com.djasoft.mozaico.web.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class MenuResponseDTO {
    private Integer idMenu;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private boolean disponible;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Set<ProductoResponseDTO> productos;
}
