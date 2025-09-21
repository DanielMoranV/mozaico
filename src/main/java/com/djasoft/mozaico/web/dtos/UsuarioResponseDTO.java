package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.EstadoUsuario;
import com.djasoft.mozaico.domain.enums.TipoUsuario;
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
    private String email;
    private TipoUsuario tipoUsuario;
    private EstadoUsuario estado;
    private LocalDateTime fechaCreacion;
}
