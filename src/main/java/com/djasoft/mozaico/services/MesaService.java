package com.djasoft.mozaico.services;

import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import com.djasoft.mozaico.web.dtos.MesaRequestDTO;
import com.djasoft.mozaico.web.dtos.MesaResponseDTO;
import com.djasoft.mozaico.web.dtos.MesaUpdateDTO;
import com.djasoft.mozaico.web.dtos.MesaEstadoDetalladoResponseDTO;

import java.util.List;

public interface MesaService {

    MesaResponseDTO crearMesa(MesaRequestDTO mesaRequestDTO);

    List<MesaResponseDTO> obtenerTodasLasMesas();

    MesaResponseDTO obtenerMesaPorId(Integer id);

    MesaResponseDTO actualizarMesa(Integer id, MesaUpdateDTO mesaUpdateDTO);

    void eliminarMesa(Integer id);

    MesaResponseDTO cambiarEstadoMesa(Integer id, EstadoMesa nuevoEstado);

    List<MesaResponseDTO> buscarMesas(
            Integer numeroMesa,
            Integer capacidad,
            String ubicacion,
            EstadoMesa estado,
            String searchTerm,
            String logic
    );

    List<MesaEstadoDetalladoResponseDTO> obtenerMesasConEstadoDetallado();
}
