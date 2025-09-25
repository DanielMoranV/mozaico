package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import com.djasoft.mozaico.services.MesaService;
import com.djasoft.mozaico.web.dtos.MesaRequestDTO;
import com.djasoft.mozaico.web.dtos.MesaResponseDTO;
import com.djasoft.mozaico.web.dtos.MesaUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mesas")
@RequiredArgsConstructor
public class MesaController {

    private final MesaService mesaService;

    @PostMapping
    public ResponseEntity<ApiResponse<MesaResponseDTO>> crearMesa(@Valid @RequestBody MesaRequestDTO requestDTO) {
        MesaResponseDTO nuevaMesa = mesaService.crearMesa(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevaMesa, "Mesa creada exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MesaResponseDTO>>> obtenerTodasLasMesas() {
        List<MesaResponseDTO> mesas = mesaService.obtenerTodasLasMesas();
        return ResponseEntity.ok(ApiResponse.success(mesas, "Mesas obtenidas exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MesaResponseDTO>> obtenerMesaPorId(@PathVariable Integer id) {
        MesaResponseDTO mesa = mesaService.obtenerMesaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(mesa, "Mesa encontrada exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MesaResponseDTO>> actualizarMesa(
            @PathVariable Integer id,
            @Valid @RequestBody MesaUpdateDTO requestDTO) {
        MesaResponseDTO mesaActualizada = mesaService.actualizarMesa(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(mesaActualizada, "Mesa actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarMesa(@PathVariable Integer id) {
        mesaService.eliminarMesa(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Mesa eliminada exitosamente"));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<MesaResponseDTO>> cambiarEstadoMesa(
            @PathVariable Integer id,
            @RequestParam EstadoMesa nuevoEstado) {
        MesaResponseDTO mesa = mesaService.cambiarEstadoMesa(id, nuevoEstado);
        return ResponseEntity.ok(ApiResponse.success(mesa, "Estado de mesa actualizado exitosamente"));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<MesaResponseDTO>>> buscarMesas(
            @RequestParam(required = false) Integer numeroMesa,
            @RequestParam(required = false) Integer capacidad,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) EstadoMesa estado,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "AND") String logic
    ) {
        List<MesaResponseDTO> mesas = mesaService.buscarMesas(numeroMesa, capacidad, ubicacion, estado, searchTerm, logic);
        return ResponseEntity.ok(ApiResponse.success(mesas, "Búsqueda de mesas exitosa"));
    }
}
