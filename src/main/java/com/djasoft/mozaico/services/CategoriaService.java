package com.djasoft.mozaico.services;

import com.djasoft.mozaico.web.dtos.CategoriaRequestDTO;
import com.djasoft.mozaico.web.dtos.CategoriaResponseDTO;
import com.djasoft.mozaico.web.dtos.CategoriaUpdateDTO;

import java.util.List;

public interface CategoriaService {

    CategoriaResponseDTO crearCategoria(CategoriaRequestDTO requestDTO);

    List<CategoriaResponseDTO> obtenerTodasLasCategorias();

    CategoriaResponseDTO obtenerCategoriaPorId(Long id);

    CategoriaResponseDTO actualizarCategoria(Long id, CategoriaUpdateDTO requestDTO);

    void eliminarCategoria(Long id);
}
