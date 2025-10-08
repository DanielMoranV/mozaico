package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.cliente.TipoDocumentoCliente;
import com.djasoft.mozaico.domain.enums.cliente.TipoPersona;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío.")
    private String nombre;

    private String apellido; // Opcional para personas jurídicas

    @Email(message = "El formato del email no es válido.")
    private String email;

    private String telefono;

    private String direccion;

    // === TIPO DE PERSONA ===
    @NotNull(message = "El tipo de persona es obligatorio.")
    @Builder.Default
    private TipoPersona tipoPersona = TipoPersona.NATURAL;

    // === DOCUMENTO (Opcional, pero recomendado para facturación) ===
    private TipoDocumentoCliente tipoDocumento;

    private String numeroDocumento;

    // === PERSONA NATURAL ===
    @Past(message = "La fecha de nacimiento debe ser en el pasado.")
    private LocalDate fechaNacimiento;

    private String preferenciasAlimentarias;

    // === PERSONA JURÍDICA ===
    private String razonSocial; // Obligatorio si tipoPersona = JURIDICA

    private String nombreComercial;

    private String representanteLegal;
}
