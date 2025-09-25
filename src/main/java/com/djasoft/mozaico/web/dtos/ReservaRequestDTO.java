package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.reserva.EstadoReserva;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "El ID del cliente no puede ser nulo.")
    private Integer idCliente;

    @NotNull(message = "El ID de la mesa no puede ser nulo.")
    private Integer idMesa;

    @NotNull(message = "La fecha y hora de la reserva no pueden ser nulas.")
    @FutureOrPresent(message = "La fecha y hora de la reserva deben ser en el presente o futuro.")
    private LocalDateTime fechaHoraReserva;

    @NotNull(message = "El número de personas no puede ser nulo.")
    @Min(value = 1, message = "El número de personas debe ser al menos 1.")
    private Integer numeroPersonas;

    private String observaciones;

    private EstadoReserva estado;
}
