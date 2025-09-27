package com.djasoft.mozaico.services;

import com.djasoft.mozaico.domain.enums.pedido.EstadoPedido;
import com.djasoft.mozaico.domain.enums.pedido.TipoServicio;
import com.djasoft.mozaico.web.dtos.PedidoRequestDTO;
import com.djasoft.mozaico.web.dtos.PedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.PedidoUpdateDTO;
import com.djasoft.mozaico.web.dtos.PedidoCompletoRequestDTO;
import com.djasoft.mozaico.web.dtos.AgregarProductoRequestDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoService {

    PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoRequestDTO);

    List<PedidoResponseDTO> obtenerTodosLosPedidos();

    PedidoResponseDTO obtenerPedidoPorId(Integer id);

    PedidoResponseDTO actualizarPedido(Integer id, PedidoUpdateDTO pedidoUpdateDTO);

    void eliminarPedido(Integer id);

    PedidoResponseDTO cambiarEstadoPedido(Integer id, EstadoPedido nuevoEstado);

    List<PedidoResponseDTO> buscarPedidos(
            Integer idCliente,
            Integer idMesa,
            Long idEmpleado,
            LocalDateTime fechaPedidoDesde,
            LocalDateTime fechaPedidoHasta,
            EstadoPedido estado,
            TipoServicio tipoServicio,
            String searchTerm,
            String logic
    );

    PedidoResponseDTO recalcularTotalesPedido(Integer idPedido);

    PedidoResponseDTO crearPedidoCompleto(PedidoCompletoRequestDTO requestDTO);

    DetallePedidoResponseDTO agregarProductoAPedido(Integer idPedido, AgregarProductoRequestDTO requestDTO);
}
