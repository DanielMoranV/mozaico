package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.UsuarioRequestDTO;
import com.djasoft.mozaico.web.dtos.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {

    UsuarioResponseDTO crearUsuario(UsuarioRequestDTO usuarioRequestDTO);

    List<UsuarioResponseDTO> obtenerTodosLosUsuarios();

    UsuarioResponseDTO obtenerUsuarioPorId(Long id);

    // Here you can add methods for update and delete in the future
    // UsuarioResponseDTO actualizarUsuario(Long id, UsuarioRequestDTO
    // usuarioRequestDTO);
    // void eliminarUsuario(Long id);
}
