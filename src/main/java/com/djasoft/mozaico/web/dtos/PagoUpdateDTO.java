package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.pago.EstadoPago;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoUpdateDTO {

    private Integer idPedido;
    private Integer idMetodo;

    @Min(value = 0, message = "El monto debe ser al menos 0.")
    private BigDecimal monto;

    private String referencia;

    private EstadoPago estado;
}
