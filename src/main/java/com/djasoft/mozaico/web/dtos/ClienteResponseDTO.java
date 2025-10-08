package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.cliente.TipoDocumentoCliente;
import com.djasoft.mozaico.domain.enums.cliente.TipoPersona;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDTO {
    private Integer idCliente;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;

    // Tipo de persona
    private TipoPersona tipoPersona;

    // Documento
    private TipoDocumentoCliente tipoDocumento;
    private String numeroDocumento;

    // Persona Natural
    private LocalDate fechaNacimiento;
    private String preferenciasAlimentarias;

    // Persona Jurídica
    private String razonSocial;
    private String nombreComercial;
    private String representanteLegal;

    // Sistema
    private Integer puntosFidelidad;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
}
