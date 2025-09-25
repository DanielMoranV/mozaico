package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.MetodoPagoRequestDTO;
import com.djasoft.mozaico.web.dtos.MetodoPagoResponseDTO;
import com.djasoft.mozaico.web.dtos.MetodoPagoUpdateDTO;

import java.util.List;

public interface MetodoPagoService {

    MetodoPagoResponseDTO crearMetodoPago(MetodoPagoRequestDTO metodoPagoRequestDTO);

    List<MetodoPagoResponseDTO> obtenerTodosLosMetodosPago();

    MetodoPagoResponseDTO obtenerMetodoPagoPorId(Integer id);

    MetodoPagoResponseDTO actualizarMetodoPago(Integer id, MetodoPagoUpdateDTO metodoPagoUpdateDTO);

    void eliminarMetodoPago(Integer id);

    MetodoPagoResponseDTO cambiarEstadoMetodoPago(Integer id, boolean activo);

    List<MetodoPagoResponseDTO> buscarMetodosPago(
            String nombre,
            Boolean activo,
            String searchTerm,
            String logic
    );
}
