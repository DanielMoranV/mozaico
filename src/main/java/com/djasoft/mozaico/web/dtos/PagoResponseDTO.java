package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.pago.EstadoPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagoResponseDTO {
    private Integer idPago;
    private PedidoResponseDTO pedido;
    private MetodoPagoResponseDTO metodoPago;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String referencia;
    private EstadoPago estado;
}
