package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.pago.EstadoPago;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoRequestDTO {

    @NotNull(message = "El ID del pedido no puede ser nulo.")
    private Integer idPedido;

    @NotNull(message = "El ID del método de pago no puede ser nulo.")
    private Integer idMetodo;

    @NotNull(message = "El monto no puede ser nulo.")
    @Min(value = 0, message = "El monto debe ser al menos 0.")
    private BigDecimal monto;

    private String referencia;

    private EstadoPago estado;
}
