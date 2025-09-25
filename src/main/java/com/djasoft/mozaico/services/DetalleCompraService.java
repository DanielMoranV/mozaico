package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.DetalleCompraRequestDTO;
import com.djasoft.mozaico.web.dtos.DetalleCompraResponseDTO;
import com.djasoft.mozaico.web.dtos.DetalleCompraUpdateDTO;

import java.util.List;

public interface DetalleCompraService {

    DetalleCompraResponseDTO crearDetalleCompra(DetalleCompraRequestDTO requestDTO);

    List<DetalleCompraResponseDTO> obtenerTodosLosDetallesPorCompra(Integer idCompra);

    DetalleCompraResponseDTO obtenerDetalleCompraPorId(Integer id);

    DetalleCompraResponseDTO actualizarDetalleCompra(Integer id, DetalleCompraUpdateDTO updateDTO);

    void eliminarDetalleCompra(Integer id);
}
