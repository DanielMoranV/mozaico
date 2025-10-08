package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.empresa.TipoOperacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta con información de la empresa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponseDTO {

    private Long idEmpresa;
    private String nombre;
    private String slug;
    private String descripcion;
    private String direccion;
    private String telefono;
    private String email;
    private String logoUrl;
    private String paginaWeb;
    private Boolean activa;
    private TipoOperacion tipoOperacion;
    private Boolean aplicaIgv;
    private BigDecimal porcentajeIgv;
    private String moneda;
    private String prefijoTicket;
    private Long correlativoTicket;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
