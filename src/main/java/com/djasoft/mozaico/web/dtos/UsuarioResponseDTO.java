package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.usuario.EstadoUsuario;
import com.djasoft.mozaico.domain.enums.usuario.TipoDocumentoIdentidad;
import com.djasoft.mozaico.domain.enums.usuario.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Long idUsuario;
    private String nombre;
    private String username;
    private String numeroDocumentoIdentidad;
    private TipoDocumentoIdentidad tipoDocumentoIdentidad;
    private String email;
    private TipoUsuario tipoUsuario;
    private EstadoUsuario estado;
    private LocalDateTime fechaCreacion;
}
