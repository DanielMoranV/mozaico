package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.InventarioRequestDTO;
import com.djasoft.mozaico.web.dtos.InventarioResponseDTO;
import com.djasoft.mozaico.web.dtos.InventarioUpdateDTO;

import java.util.List;

public interface InventarioService {

    InventarioResponseDTO crearInventario(InventarioRequestDTO inventarioRequestDTO);

    List<InventarioResponseDTO> obtenerTodosLosInventarios();

    InventarioResponseDTO obtenerInventarioPorId(Integer id);

    InventarioResponseDTO actualizarInventario(Integer id, InventarioUpdateDTO inventarioUpdateDTO);

    void eliminarInventario(Integer id);

    InventarioResponseDTO actualizarStockPorVenta(Long idProducto, Integer cantidadVendida);

    InventarioResponseDTO actualizarStockPorCompra(Long idProducto, Integer cantidadComprada);

    List<InventarioResponseDTO> buscarInventarios(
            Long idProducto,
            Integer stockActualMin,
            Integer stockActualMax,
            Integer stockMinimo,
            Integer stockMaximo,
            String searchTerm,
            String logic
    );
}
