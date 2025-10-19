package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.domain.enums.reserva.EstadoReserva;
import com.djasoft.mozaico.services.ReservaService;
import com.djasoft.mozaico.web.dtos.DisponibilidadRequestDTO;
import com.djasoft.mozaico.web.dtos.DisponibilidadResponseDTO;
import com.djasoft.mozaico.web.dtos.ReservaRequestDTO;
import com.djasoft.mozaico.web.dtos.ReservaResponseDTO;
import com.djasoft.mozaico.web.dtos.ReservaUpdateDTO;
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
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> crearReserva(@Valid @RequestBody ReservaRequestDTO requestDTO) {
        ReservaResponseDTO nuevaReserva = reservaService.crearReserva(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevaReserva, "Reserva creada exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReservaResponseDTO>>> obtenerTodasLasReservas() {
        List<ReservaResponseDTO> reservas = reservaService.obtenerTodasLasReservas();
        return ResponseEntity.ok(ApiResponse.success(reservas, "Reservas obtenidas exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> obtenerReservaPorId(@PathVariable Integer id) {
        ReservaResponseDTO reserva = reservaService.obtenerReservaPorId(id);
        return ResponseEntity.ok(ApiResponse.success(reserva, "Reserva encontrada exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> actualizarReserva(
            @PathVariable Integer id,
            @Valid @RequestBody ReservaUpdateDTO requestDTO) {
        ReservaResponseDTO reservaActualizada = reservaService.actualizarReserva(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(reservaActualizada, "Reserva actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarReserva(@PathVariable Integer id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Reserva eliminada exitosamente"));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<ReservaResponseDTO>> cambiarEstadoReserva(
            @PathVariable Integer id,
            @RequestParam EstadoReserva nuevoEstado) {
        ReservaResponseDTO reserva = reservaService.cambiarEstadoReserva(id, nuevoEstado);
        return ResponseEntity.ok(ApiResponse.success(reserva, "Estado de la Reserva actualizado exitosamente"));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<ReservaResponseDTO>>> buscarReservas(
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) Integer idMesa,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHoraReservaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHoraReservaHasta,
            @RequestParam(required = false) EstadoReserva estado,
            @RequestParam(required = false) Integer numeroPersonas,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "AND") String logic
    ) {
        List<ReservaResponseDTO> reservas = reservaService.buscarReservas(idCliente, idMesa, fechaHoraReservaDesde, fechaHoraReservaHasta, estado, numeroPersonas, searchTerm, logic);
        return ResponseEntity.ok(ApiResponse.success(reservas, "Búsqueda de Reservas exitosa"));
    }

    @PostMapping("/disponibilidad")
    public ResponseEntity<ApiResponse<DisponibilidadResponseDTO>> consultarDisponibilidad(
            @Valid @RequestBody DisponibilidadRequestDTO requestDTO) {
        DisponibilidadResponseDTO disponibilidad = reservaService.consultarDisponibilidad(requestDTO);
        return ResponseEntity.ok(ApiResponse.success(disponibilidad, "Consulta de disponibilidad exitosa"));
    }
}
