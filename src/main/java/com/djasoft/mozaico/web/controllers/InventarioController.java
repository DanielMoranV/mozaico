package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.InventarioService;
import com.djasoft.mozaico.web.dtos.InventarioRequestDTO;
import com.djasoft.mozaico.web.dtos.InventarioResponseDTO;
import com.djasoft.mozaico.web.dtos.InventarioUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @PostMapping
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> crearInventario(@Valid @RequestBody InventarioRequestDTO requestDTO) {
        InventarioResponseDTO nuevoInventario = inventarioService.crearInventario(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevoInventario, "Registro de Inventario creado exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> obtenerTodosLosInventarios() {
        List<InventarioResponseDTO> inventarios = inventarioService.obtenerTodosLosInventarios();
        return ResponseEntity.ok(ApiResponse.success(inventarios, "Registros de Inventario obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> obtenerInventarioPorId(@PathVariable Integer id) {
        InventarioResponseDTO inventario = inventarioService.obtenerInventarioPorId(id);
        return ResponseEntity.ok(ApiResponse.success(inventario, "Registro de Inventario encontrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioResponseDTO>> actualizarInventario(
            @PathVariable Integer id,
            @Valid @RequestBody InventarioUpdateDTO requestDTO) {
        InventarioResponseDTO inventarioActualizado = inventarioService.actualizarInventario(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(inventarioActualizado, "Registro de Inventario actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarInventario(@PathVariable Integer id) {
        inventarioService.eliminarInventario(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Registro de Inventario eliminado exitosamente"));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<InventarioResponseDTO>>> buscarInventarios(
            @RequestParam(required = false) Long idProducto,
            @RequestParam(required = false) Integer stockActualMin,
            @RequestParam(required = false) Integer stockActualMax,
            @RequestParam(required = false) Integer stockMinimo,
            @RequestParam(required = false) Integer stockMaximo,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "AND") String logic
    ) {
        List<InventarioResponseDTO> inventarios = inventarioService.buscarInventarios(idProducto, stockActualMin, stockActualMax, stockMinimo, stockMaximo, searchTerm, logic);
        return ResponseEntity.ok(ApiResponse.success(inventarios, "Búsqueda de Registros de Inventario exitosa"));
    }
}
