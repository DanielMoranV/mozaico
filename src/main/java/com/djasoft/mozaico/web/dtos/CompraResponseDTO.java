package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.compra.EstadoCompra;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraResponseDTO {
    private Integer idCompra;
    private ProveedorResponseDTO proveedor;
    private LocalDate fechaCompra;
    private BigDecimal total;
    private EstadoCompra estado;
    private String observaciones;
}
