package com.djasoft.mozaico.web.dtos;

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
public class DisponibilidadRequestDTO {

    @NotNull(message = "La fecha y hora no pueden ser nulas.")
    @FutureOrPresent(message = "La fecha y hora deben ser en el presente o futuro.")
    private LocalDateTime fechaHora;

    @NotNull(message = "El número de personas no puede ser nulo.")
    @Min(value = 1, message = "El número de personas debe ser al menos 1.")
    private Integer numeroPersonas;

    private String ubicacion; // Filtro opcional por ubicación
}
