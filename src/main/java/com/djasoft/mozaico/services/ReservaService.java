package com.djasoft.mozaico.services;

import com.djasoft.mozaico.domain.enums.reserva.EstadoReserva;
import com.djasoft.mozaico.web.dtos.ReservaRequestDTO;
import com.djasoft.mozaico.web.dtos.ReservaResponseDTO;
import com.djasoft.mozaico.web.dtos.ReservaUpdateDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaService {

    ReservaResponseDTO crearReserva(ReservaRequestDTO reservaRequestDTO);

    List<ReservaResponseDTO> obtenerTodasLasReservas();

    ReservaResponseDTO obtenerReservaPorId(Integer id);

    ReservaResponseDTO actualizarReserva(Integer id, ReservaUpdateDTO reservaUpdateDTO);

    void eliminarReserva(Integer id);

    ReservaResponseDTO cambiarEstadoReserva(Integer id, EstadoReserva nuevoEstado);

    List<ReservaResponseDTO> buscarReservas(
            Integer idCliente,
            Integer idMesa,
            LocalDateTime fechaHoraReservaDesde,
            LocalDateTime fechaHoraReservaHasta,
            EstadoReserva estado,
            Integer numeroPersonas,
            String searchTerm,
            String logic
    );
}
