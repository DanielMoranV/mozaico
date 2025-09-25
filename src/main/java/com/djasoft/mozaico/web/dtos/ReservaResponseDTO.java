package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.reserva.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    private Integer idReserva;
    private ClienteResponseDTO cliente;
    private MesaResponseDTO mesa;
    private LocalDateTime fechaHoraReserva;
    private Integer numeroPersonas;
    private EstadoReserva estado;
    private String observaciones;
    private LocalDateTime fechaCreacion;
}
