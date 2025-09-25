package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Categoria;
import com.djasoft.mozaico.domain.repositories.CategoriaRepository;
import com.djasoft.mozaico.services.CategoriaService;
import com.djasoft.mozaico.web.dtos.CategoriaRequestDTO;
import com.djasoft.mozaico.web.dtos.CategoriaResponseDTO;
import com.djasoft.mozaico.web.dtos.CategoriaUpdateDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public CategoriaResponseDTO crearCategoria(CategoriaRequestDTO requestDTO) {
        Categoria nuevaCategoria = new Categoria();
        nuevaCategoria.setNombre(requestDTO.getNombre());
        nuevaCategoria.setDescripcion(requestDTO.getDescripcion());

        Categoria categoriaGuardada = categoriaRepository.save(nuevaCategoria);
        return mapToResponseDTO(categoriaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> obtenerTodasLasCategorias() {
        return categoriaRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaResponseDTO obtenerCategoriaPorId(Long id) {
        return categoriaRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + id));
    }

    @Override
    @Transactional
    public CategoriaResponseDTO actualizarCategoria(Long id, CategoriaUpdateDTO requestDTO) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con el id: " + id));

        categoriaExistente.setNombre(requestDTO.getNombre());
        categoriaExistente.setDescripcion(requestDTO.getDescripcion());

        Categoria categoriaActualizada = categoriaRepository.save(categoriaExistente);
        return mapToResponseDTO(categoriaActualizada);
    }

    @Override
    @Transactional
    public void eliminarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con el id: " + id);
        }
        categoriaRepository.deleteById(id);
    }

    private CategoriaResponseDTO mapToResponseDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .fechaCreacion(categoria.getFechaCreacion())
                .fechaActualizacion(categoria.getFechaActualizacion())
                .build();
    }
}
