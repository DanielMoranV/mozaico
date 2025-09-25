package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.reserva.EstadoReserva;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaUpdateDTO {

    private Integer idCliente;
    private Integer idMesa;

    @FutureOrPresent(message = "La fecha y hora de la reserva deben ser en el presente o futuro.")
    private LocalDateTime fechaHoraReserva;

    @Min(value = 1, message = "El número de personas debe ser al menos 1.")
    private Integer numeroPersonas;

    private String observaciones;

    private EstadoReserva estado;
}
