package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.DetallePedidoRequestDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoUpdateDTO;

import java.util.List;

public interface DetallePedidoService {

    DetallePedidoResponseDTO crearDetallePedido(DetallePedidoRequestDTO requestDTO);

    List<DetallePedidoResponseDTO> obtenerTodosLosDetallesPorPedido(Integer idPedido);

    DetallePedidoResponseDTO obtenerDetallePedidoPorId(Integer id);

    DetallePedidoResponseDTO actualizarDetallePedido(Integer id, DetallePedidoUpdateDTO updateDTO);

    void eliminarDetallePedido(Integer id);

    List<DetallePedidoResponseDTO> obtenerDetallesPorEstado(com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido estado);

    DetallePedidoResponseDTO cambiarEstadoDetalle(Integer idDetalle, com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido nuevoEstado);
}