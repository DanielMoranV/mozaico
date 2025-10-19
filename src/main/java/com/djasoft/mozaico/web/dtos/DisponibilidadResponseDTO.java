package com.djasoft.mozaico.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisponibilidadResponseDTO {

    private List<MesaDisponibleDTO> mesasDisponibles;
    private Integer totalDisponibles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MesaDisponibleDTO {
        private Integer idMesa;
        private Integer numeroMesa;
        private Integer capacidad;
        private String ubicacion;
        private String observaciones;
    }
}
