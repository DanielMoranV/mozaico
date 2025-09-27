package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.DetallePedidoService;
import com.djasoft.mozaico.web.dtos.DetallePedidoRequestDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/detalles-pedido")
@RequiredArgsConstructor
public class DetallePedidoController {

    private final DetallePedidoService detallePedidoService;

    @PostMapping
    public ResponseEntity<ApiResponse<DetallePedidoResponseDTO>> crearDetallePedido(@Valid @RequestBody DetallePedidoRequestDTO requestDTO) {
        DetallePedidoResponseDTO responseDTO = detallePedidoService.crearDetallePedido(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(responseDTO, "Detalle de pedido creado exitosamente"), HttpStatus.CREATED);
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<ApiResponse<List<DetallePedidoResponseDTO>>> obtenerTodosLosDetallesPorPedido(@PathVariable Integer idPedido) {
        List<DetallePedidoResponseDTO> detalles = detallePedidoService.obtenerTodosLosDetallesPorPedido(idPedido);
        return ResponseEntity.ok(ApiResponse.success(detalles, "Detalles de pedido obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DetallePedidoResponseDTO>> obtenerDetallePedidoPorId(@PathVariable Integer id) {
        DetallePedidoResponseDTO responseDTO = detallePedidoService.obtenerDetallePedidoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(responseDTO, "Detalle de pedido encontrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DetallePedidoResponseDTO>> actualizarDetallePedido(@PathVariable Integer id, @Valid @RequestBody DetallePedidoUpdateDTO updateDTO) {
        DetallePedidoResponseDTO responseDTO = detallePedidoService.actualizarDetallePedido(id, updateDTO);
        return ResponseEntity.ok(ApiResponse.success(responseDTO, "Detalle de pedido actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarDetallePedido(@PathVariable Integer id) {
        detallePedidoService.eliminarDetallePedido(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Detalle de pedido eliminado exitosamente"));
    }
}