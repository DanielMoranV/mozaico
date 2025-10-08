package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.domain.enums.pago.EstadoPago;
import com.djasoft.mozaico.services.PagoService;
import com.djasoft.mozaico.web.dtos.PagoRequestDTO;
import com.djasoft.mozaico.web.dtos.PagoResponseDTO;
import com.djasoft.mozaico.web.dtos.PagoCompletoResponseDTO;
import com.djasoft.mozaico.web.dtos.PagoUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponseDTO>> crearPago(@Valid @RequestBody PagoRequestDTO requestDTO) {
        PagoResponseDTO nuevoPago = pagoService.crearPago(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevoPago, "Pago creado exitosamente"),
                HttpStatus.CREATED);
    }

    @PostMapping("/completo")
    public ResponseEntity<ApiResponse<PagoCompletoResponseDTO>> crearPagoCompleto(@Valid @RequestBody PagoRequestDTO requestDTO) {
        PagoCompletoResponseDTO pagoCompleto = pagoService.crearPagoCompleto(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(pagoCompleto, "Pago y comprobante creados exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PagoResponseDTO>>> obtenerTodosLosPagos() {
        List<PagoResponseDTO> pagos = pagoService.obtenerTodosLosPagos();
        return ResponseEntity.ok(ApiResponse.success(pagos, "Pagos obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> obtenerPagoPorId(@PathVariable Integer id) {
        PagoResponseDTO pago = pagoService.obtenerPagoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(pago, "Pago encontrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> actualizarPago(
            @PathVariable Integer id,
            @Valid @RequestBody PagoUpdateDTO requestDTO) {
        PagoResponseDTO pagoActualizado = pagoService.actualizarPago(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(pagoActualizado, "Pago actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarPago(@PathVariable Integer id) {
        pagoService.eliminarPago(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Pago eliminado exitosamente"));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> cambiarEstadoPago(
            @PathVariable Integer id,
            @RequestParam EstadoPago nuevoEstado) {
        PagoResponseDTO pago = pagoService.cambiarEstadoPago(id, nuevoEstado);
        return ResponseEntity.ok(ApiResponse.success(pago, "Estado del pago actualizado exitosamente"));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<PagoResponseDTO>>> buscarPagos(
            @RequestParam(required = false) Integer idPedido,
            @RequestParam(required = false) Integer idMetodo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaPagoDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaPagoHasta,
            @RequestParam(required = false) EstadoPago estado,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "AND") String logic
    ) {
        List<PagoResponseDTO> pagos = pagoService.buscarPagos(idPedido, idMetodo, fechaPagoDesde, fechaPagoHasta, estado, searchTerm, logic);
        return ResponseEntity.ok(ApiResponse.success(pagos, "Búsqueda de pagos exitosa"));
    }
}
