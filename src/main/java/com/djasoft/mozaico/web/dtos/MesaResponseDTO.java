package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesaResponseDTO {
    private Integer idMesa;
    private Integer numeroMesa;
    private Integer capacidad;
    private String ubicacion;
    private EstadoMesa estado;
    private String observaciones;
    private LocalDateTime fechaCreacion;
}
