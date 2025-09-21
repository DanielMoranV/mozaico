package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {
    private String nombre;
    private String username;
    private String email;
    private String password;
    private TipoUsuario tipoUsuario;
}
