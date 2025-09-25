package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.ProveedorRequestDTO;
import com.djasoft.mozaico.web.dtos.ProveedorResponseDTO;
import com.djasoft.mozaico.web.dtos.ProveedorUpdateDTO;

import java.util.List;

public interface ProveedorService {

    ProveedorResponseDTO crearProveedor(ProveedorRequestDTO proveedorRequestDTO);

    List<ProveedorResponseDTO> obtenerTodosLosProveedores();

    ProveedorResponseDTO obtenerProveedorPorId(Integer id);

    ProveedorResponseDTO actualizarProveedor(Integer id, ProveedorUpdateDTO proveedorUpdateDTO);

    void eliminarProveedor(Integer id);

    ProveedorResponseDTO cambiarEstadoProveedor(Integer id, boolean activo);

    List<ProveedorResponseDTO> buscarProveedores(
            String nombre,
            String contacto,
            String telefono,
            String email,
            Boolean activo,
            String searchTerm,
            String logic
    );
}
