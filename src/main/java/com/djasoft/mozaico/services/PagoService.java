package com.djasoft.mozaico.services;

import com.djasoft.mozaico.domain.enums.pago.EstadoPago;
import com.djasoft.mozaico.web.dtos.PagoRequestDTO;
import com.djasoft.mozaico.web.dtos.PagoResponseDTO;
import com.djasoft.mozaico.web.dtos.PagoUpdateDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface PagoService {

    PagoResponseDTO crearPago(PagoRequestDTO pagoRequestDTO);

    List<PagoResponseDTO> obtenerTodosLosPagos();

    PagoResponseDTO obtenerPagoPorId(Integer id);

    PagoResponseDTO actualizarPago(Integer id, PagoUpdateDTO pagoUpdateDTO);

    void eliminarPago(Integer id);

    PagoResponseDTO cambiarEstadoPago(Integer id, EstadoPago nuevoEstado);

    List<PagoResponseDTO> buscarPagos(
            Integer idPedido,
            Integer idMetodo,
            LocalDateTime fechaPagoDesde,
            LocalDateTime fechaPagoHasta,
            EstadoPago estado,
            String searchTerm,
            String logic
    );
}
