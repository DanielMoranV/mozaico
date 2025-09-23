package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.usuario.TipoDocumentoIdentidad;
import com.djasoft.mozaico.domain.enums.usuario.TipoUsuario;
import com.djasoft.mozaico.web.validators.ValidDocument;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidDocument
public class UsuarioUpdateDTO implements DocumentInfo {

    @NotBlank(message = "El nombre no puede estar vacío.")
    private String nombre;

    @NotBlank(message = "El nombre de usuario no puede estar vacío.")
    private String username;

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El formato del email no es válido.")
    private String email;

    @NotNull(message = "El tipo de usuario no puede ser nulo.")
    private TipoUsuario tipoUsuario;

    @NotNull(message = "El tipo de documento no puede ser nulo.")
    private TipoDocumentoIdentidad tipoDocumentoIdentidad;

    private String numeroDocumento;

    @Override
    public TipoDocumentoIdentidad getTipoDocumentoIdentidad() {
        return tipoDocumentoIdentidad;
    }

    @Override
    public String getNumeroDocumento() {
        return numeroDocumento;
    }
}
