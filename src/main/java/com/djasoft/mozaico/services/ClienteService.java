package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.ClienteRequestDTO;
import com.djasoft.mozaico.web.dtos.ClienteResponseDTO;
import com.djasoft.mozaico.web.dtos.ClienteUpdateDTO;

import java.util.List;

public interface ClienteService {

    ClienteResponseDTO crearCliente(ClienteRequestDTO clienteRequestDTO);

    List<ClienteResponseDTO> obtenerTodosLosClientes();

    ClienteResponseDTO obtenerClientePorId(Integer id);

    ClienteResponseDTO actualizarCliente(Integer id, ClienteUpdateDTO clienteUpdateDTO);

    void eliminarCliente(Integer id);

    ClienteResponseDTO cambiarEstadoCliente(Integer id, boolean estado);

    List<ClienteResponseDTO> buscarClientes(
            String nombre,
            String apellido,
            String email,
            String telefono,
            Boolean activo,
            String searchTerm,
            String logic
    );
}
