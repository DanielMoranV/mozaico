package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.CategoriaService;
import com.djasoft.mozaico.web.dtos.CategoriaRequestDTO;
import com.djasoft.mozaico.web.dtos.CategoriaResponseDTO;
import com.djasoft.mozaico.web.dtos.CategoriaUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> crearCategoria(@Valid @RequestBody CategoriaRequestDTO requestDTO) {
        CategoriaResponseDTO nuevaCategoria = categoriaService.crearCategoria(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevaCategoria, "Categoría creada exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoriaResponseDTO>>> obtenerTodasLasCategorias() {
        List<CategoriaResponseDTO> categorias = categoriaService.obtenerTodasLasCategorias();
        return ResponseEntity.ok(ApiResponse.success(categorias, "Categorías obtenidas exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> obtenerCategoriaPorId(@PathVariable Long id) {
        CategoriaResponseDTO categoria = categoriaService.obtenerCategoriaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(categoria, "Categoría encontrada exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoriaResponseDTO>> actualizarCategoria(
            @PathVariable Long id,
            @Valid @RequestBody CategoriaUpdateDTO requestDTO) {
        CategoriaResponseDTO categoriaActualizada = categoriaService.actualizarCategoria(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(categoriaActualizada, "Categoría actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCategoria(@PathVariable Long id) {
        categoriaService.eliminarCategoria(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Categoría eliminada exitosamente"));
    }
}
