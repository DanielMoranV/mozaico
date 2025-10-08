package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.empresa.TipoOperacion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para actualizar información de la empresa
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaUpdateDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    private String nombre;

    private String descripcion;

    private String direccion;

    private String telefono;

    @Email(message = "Email inválido")
    private String email;

    private String paginaWeb;

    private String logoUrl;

    @NotNull(message = "El tipo de operación es obligatorio")
    private TipoOperacion tipoOperacion;

    @NotNull(message = "Debe indicar si aplica IGV")
    private Boolean aplicaIgv;

    private BigDecimal porcentajeIgv;

    private String moneda;

    private String prefijoTicket;
}
