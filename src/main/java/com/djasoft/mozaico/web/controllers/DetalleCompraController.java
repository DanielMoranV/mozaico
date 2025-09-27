package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.DetalleCompraService;
import com.djasoft.mozaico.web.dtos.DetalleCompraRequestDTO;
import com.djasoft.mozaico.web.dtos.DetalleCompraResponseDTO;
import com.djasoft.mozaico.web.dtos.DetalleCompraUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/detalles-compra")
@RequiredArgsConstructor
public class DetalleCompraController {

    private final DetalleCompraService detalleCompraService;

    @PostMapping
    public ResponseEntity<ApiResponse<DetalleCompraResponseDTO>> crearDetalleCompra(@Valid @RequestBody DetalleCompraRequestDTO requestDTO) {
        DetalleCompraResponseDTO responseDTO = detalleCompraService.crearDetalleCompra(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(responseDTO, "Detalle de compra creado exitosamente"), HttpStatus.CREATED);
    }

    @GetMapping("/compra/{idCompra}")
    public ResponseEntity<ApiResponse<List<DetalleCompraResponseDTO>>> obtenerTodosLosDetallesPorCompra(@PathVariable Integer idCompra) {
        List<DetalleCompraResponseDTO> detalles = detalleCompraService.obtenerTodosLosDetallesPorCompra(idCompra);
        return ResponseEntity.ok(ApiResponse.success(detalles, "Detalles de compra obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleCompraResponseDTO>> obtenerDetalleCompraPorId(@PathVariable Integer id) {
        DetalleCompraResponseDTO responseDTO = detalleCompraService.obtenerDetalleCompraPorId(id);
        return ResponseEntity.ok(ApiResponse.success(responseDTO, "Detalle de compra encontrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DetalleCompraResponseDTO>> actualizarDetalleCompra(@PathVariable Integer id, @Valid @RequestBody DetalleCompraUpdateDTO updateDTO) {
        DetalleCompraResponseDTO responseDTO = detalleCompraService.actualizarDetalleCompra(id, updateDTO);
        return ResponseEntity.ok(ApiResponse.success(responseDTO, "Detalle de compra actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDetalleCompra(@PathVariable Integer id) {
        detalleCompraService.eliminarDetalleCompra(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Detalle de compra eliminado exitosamente"));
    }
}
