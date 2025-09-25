package com.djasoft.mozaico.services;

import com.djasoft.mozaico.domain.enums.compra.EstadoCompra;
import com.djasoft.mozaico.web.dtos.CompraRequestDTO;
import com.djasoft.mozaico.web.dtos.CompraResponseDTO;
import com.djasoft.mozaico.web.dtos.CompraUpdateDTO;

import java.time.LocalDate;
import java.util.List;

public interface CompraService {

    CompraResponseDTO crearCompra(CompraRequestDTO compraRequestDTO);

    List<CompraResponseDTO> obtenerTodasLasCompras();

    CompraResponseDTO obtenerCompraPorId(Integer id);

    CompraResponseDTO actualizarCompra(Integer id, CompraUpdateDTO compraUpdateDTO);

    void eliminarCompra(Integer id);

    CompraResponseDTO cambiarEstadoCompra(Integer id, EstadoCompra nuevoEstado);

    void actualizarInventarioPorCompra(Integer idCompra);

    List<CompraResponseDTO> buscarCompras(
            Integer idProveedor,
            LocalDate fechaCompraDesde,
            LocalDate fechaCompraHasta,
            EstadoCompra estado,
            String searchTerm,
            String logic
    );

    CompraResponseDTO recalcularTotalesCompra(Integer idCompra);
}
