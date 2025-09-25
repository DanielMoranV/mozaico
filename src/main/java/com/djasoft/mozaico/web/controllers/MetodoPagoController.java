package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.MetodoPagoService;
import com.djasoft.mozaico.web.dtos.MetodoPagoRequestDTO;
import com.djasoft.mozaico.web.dtos.MetodoPagoResponseDTO;
import com.djasoft.mozaico.web.dtos.MetodoPagoUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metodos-pago")
@RequiredArgsConstructor
public class MetodoPagoController {

    private final MetodoPagoService metodoPagoService;

    @PostMapping
    public ResponseEntity<ApiResponse<MetodoPagoResponseDTO>> crearMetodoPago(@Valid @RequestBody MetodoPagoRequestDTO requestDTO) {
        MetodoPagoResponseDTO nuevoMetodoPago = metodoPagoService.crearMetodoPago(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevoMetodoPago, "Método de Pago creado exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MetodoPagoResponseDTO>>> obtenerTodosLosMetodosPago() {
        List<MetodoPagoResponseDTO> metodosPago = metodoPagoService.obtenerTodosLosMetodosPago();
        return ResponseEntity.ok(ApiResponse.success(metodosPago, "Métodos de Pago obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MetodoPagoResponseDTO>> obtenerMetodoPagoPorId(@PathVariable Integer id) {
        MetodoPagoResponseDTO metodoPago = metodoPagoService.obtenerMetodoPagoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(metodoPago, "Método de Pago encontrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MetodoPagoResponseDTO>> actualizarMetodoPago(
            @PathVariable Integer id,
            @Valid @RequestBody MetodoPagoUpdateDTO requestDTO) {
        MetodoPagoResponseDTO metodoPagoActualizado = metodoPagoService.actualizarMetodoPago(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(metodoPagoActualizado, "Método de Pago actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarMetodoPago(@PathVariable Integer id) {
        metodoPagoService.eliminarMetodoPago(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Método de Pago eliminado exitosamente"));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<MetodoPagoResponseDTO>> cambiarEstadoMetodoPago(
            @PathVariable Integer id,
            @RequestParam boolean activo) {
        MetodoPagoResponseDTO metodoPago = metodoPagoService.cambiarEstadoMetodoPago(id, activo);
        return ResponseEntity.ok(ApiResponse.success(metodoPago, "Estado del Método de Pago actualizado exitosamente"));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<MetodoPagoResponseDTO>>> buscarMetodosPago(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "AND") String logic
    ) {
        List<MetodoPagoResponseDTO> metodosPago = metodoPagoService.buscarMetodosPago(nombre, activo, searchTerm, logic);
        return ResponseEntity.ok(ApiResponse.success(metodosPago, "Búsqueda de Métodos de Pago exitosa"));
    }
}
