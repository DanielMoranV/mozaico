package com.djasoft.mozaico.services;

import com.djasoft.mozaico.domain.enums.usuario.EstadoUsuario;
import com.djasoft.mozaico.domain.enums.usuario.TipoDocumentoIdentidad;
import com.djasoft.mozaico.domain.enums.usuario.TipoUsuario;
import com.djasoft.mozaico.web.dtos.UsuarioRequestDTO;
import com.djasoft.mozaico.web.dtos.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {

    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO usuarioRequestDTO);

    List<UsuarioResponseDTO> obtenerTodosLosUsuarios();

    UsuarioResponseDTO obtenerUsuarioPorId(Long id);

    // Nuevos métodos
    UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO);

    void eliminarUsuario(Long id);

    List<UsuarioResponseDTO> buscarUsuarios(
            String nombre,
            String username,
            String email,
            TipoUsuario tipoUsuario,
            EstadoUsuario estado,
            TipoDocumentoIdentidad tipoDocumentoIdentidad,
            String numeroDocumento
    );
}
