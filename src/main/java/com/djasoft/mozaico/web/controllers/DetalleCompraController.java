package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.DetalleCompraService;
import com.djasoft.mozaico.web.dtos.DetalleCompraRequestDTO;
import com.djasoft.mozaico.web.dtos.DetalleCompraResponseDTO;
import com.djasoft.mozaico.web.dtos.DetalleCompraUpdateDTO;
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
    public ResponseEntity<DetalleCompraResponseDTO> crearDetalleCompra(@Valid @RequestBody DetalleCompraRequestDTO requestDTO) {
        DetalleCompraResponseDTO responseDTO = detalleCompraService.crearDetalleCompra(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/compra/{idCompra}")
    public ResponseEntity<List<DetalleCompraResponseDTO>> obtenerTodosLosDetallesPorCompra(@PathVariable Integer idCompra) {
        List<DetalleCompraResponseDTO> detalles = detalleCompraService.obtenerTodosLosDetallesPorCompra(idCompra);
        return ResponseEntity.ok(detalles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalleCompraResponseDTO> obtenerDetalleCompraPorId(@PathVariable Integer id) {
        DetalleCompraResponseDTO responseDTO = detalleCompraService.obtenerDetalleCompraPorId(id);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetalleCompraResponseDTO> actualizarDetalleCompra(@PathVariable Integer id, @Valid @RequestBody DetalleCompraUpdateDTO updateDTO) {
        DetalleCompraResponseDTO responseDTO = detalleCompraService.actualizarDetalleCompra(id, updateDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleCompra(@PathVariable Integer id) {
        detalleCompraService.eliminarDetalleCompra(id);
        return ResponseEntity.noContent().build();
    }
}
