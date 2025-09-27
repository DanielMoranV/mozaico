package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido;
import com.djasoft.mozaico.services.DetallePedidoService;
import com.djasoft.mozaico.web.dtos.DetallePedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kds")
@RequiredArgsConstructor
public class KdsController {

    private final DetallePedidoService detallePedidoService;

    @GetMapping("/detalles")
    public ResponseEntity<ApiResponse<List<DetallePedidoResponseDTO>>> obtenerDetallesPorEstado(@RequestParam("estado") String estado) {
        EstadoDetallePedido estadoEnum = EstadoDetallePedido.valueOf(estado.toUpperCase());
        List<DetallePedidoResponseDTO> detalles = detallePedidoService.obtenerDetallesPorEstado(estadoEnum);
        return ResponseEntity.ok(ApiResponse.success(detalles, "Detalles KDS obtenidos exitosamente"));
    }

    @PutMapping("/detalles/{id}/estado")
    public ResponseEntity<ApiResponse<DetallePedidoResponseDTO>> cambiarEstadoDetalle(
            @PathVariable("id") Integer idDetalle,
            @RequestParam("estado") String nuevoEstado) {
        EstadoDetallePedido estadoEnum = EstadoDetallePedido.valueOf(nuevoEstado.toUpperCase());
        DetallePedidoResponseDTO detalleActualizado = detallePedidoService.cambiarEstadoDetalle(idDetalle, estadoEnum);
        return ResponseEntity.ok(ApiResponse.success(detalleActualizado, "Estado del detalle actualizado exitosamente"));
    }
}
