package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.DetallePedidoService;
import com.djasoft.mozaico.web.dtos.DetallePedidoRequestDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoUpdateDTO;
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
    public ResponseEntity<DetallePedidoResponseDTO> crearDetallePedido(@Valid @RequestBody DetallePedidoRequestDTO requestDTO) {
        DetallePedidoResponseDTO responseDTO = detallePedidoService.crearDetallePedido(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<DetallePedidoResponseDTO>> obtenerTodosLosDetallesPorPedido(@PathVariable Integer idPedido) {
        List<DetallePedidoResponseDTO> detalles = detallePedidoService.obtenerTodosLosDetallesPorPedido(idPedido);
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoResponseDTO> obtenerDetallePedidoPorId(@PathVariable Integer id) {
        DetallePedidoResponseDTO responseDTO = detallePedidoService.obtenerDetallePedidoPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedidoResponseDTO> actualizarDetallePedido(@PathVariable Integer id, @Valid @RequestBody DetallePedidoUpdateDTO updateDTO) {
        DetallePedidoResponseDTO responseDTO = detallePedidoService.actualizarDetallePedido(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetallePedido(@PathVariable Integer id) {
        detallePedidoService.eliminarDetallePedido(id);
        return ResponseEntity.noContent().build();
    }
}