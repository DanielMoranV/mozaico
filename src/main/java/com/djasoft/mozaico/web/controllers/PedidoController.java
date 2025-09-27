package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.domain.enums.pedido.EstadoPedido;
import com.djasoft.mozaico.domain.enums.pedido.TipoServicio;
import com.djasoft.mozaico.services.PedidoService;
import com.djasoft.mozaico.web.dtos.PedidoRequestDTO;
import com.djasoft.mozaico.web.dtos.PedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.PedidoUpdateDTO;
import com.djasoft.mozaico.web.dtos.PedidoCompletoRequestDTO;
import com.djasoft.mozaico.web.dtos.AgregarProductoRequestDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoResponseDTO;
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
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> crearPedido(@Valid @RequestBody PedidoRequestDTO requestDTO) {
        PedidoResponseDTO nuevoPedido = pedidoService.crearPedido(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevoPedido, "Pedido creado exitosamente"),
                HttpStatus.CREATED);
    }

    @PostMapping("/completo")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> crearPedidoCompleto(@Valid @RequestBody PedidoCompletoRequestDTO requestDTO) {
        PedidoResponseDTO nuevoPedido = pedidoService.crearPedidoCompleto(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevoPedido, "Pedido completo creado exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PedidoResponseDTO>>> obtenerTodosLosPedidos() {
        List<PedidoResponseDTO> pedidos = pedidoService.obtenerTodosLosPedidos();
        return ResponseEntity.ok(ApiResponse.success(pedidos, "Pedidos obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> obtenerPedidoPorId(@PathVariable Integer id) {
        PedidoResponseDTO pedido = pedidoService.obtenerPedidoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(pedido, "Pedido encontrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> actualizarPedido(
            @PathVariable Integer id,
            @Valid @RequestBody PedidoUpdateDTO requestDTO) {
        PedidoResponseDTO pedidoActualizado = pedidoService.actualizarPedido(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(pedidoActualizado, "Pedido actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarPedido(@PathVariable Integer id) {
        pedidoService.eliminarPedido(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Pedido eliminado exitosamente"));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<PedidoResponseDTO>> cambiarEstadoPedido(
            @PathVariable Integer id,
            @RequestParam EstadoPedido nuevoEstado) {
        PedidoResponseDTO pedido = pedidoService.cambiarEstadoPedido(id, nuevoEstado);
        return ResponseEntity.ok(ApiResponse.success(pedido, "Estado del pedido actualizado exitosamente"));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<PedidoResponseDTO>>> buscarPedidos(
            @RequestParam(required = false) Integer idCliente,
            @RequestParam(required = false) Integer idMesa,
            @RequestParam(required = false) Long idEmpleado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaPedidoDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaPedidoHasta,
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) TipoServicio tipoServicio,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "AND") String logic
    ) {
        List<PedidoResponseDTO> pedidos = pedidoService.buscarPedidos(idCliente, idMesa, idEmpleado, fechaPedidoDesde, fechaPedidoHasta, estado, tipoServicio, searchTerm, logic);
        return ResponseEntity.ok(ApiResponse.success(pedidos, "Búsqueda de pedidos exitosa"));
    }

    @PostMapping("/{id}/productos")
    public ResponseEntity<ApiResponse<DetallePedidoResponseDTO>> agregarProductoAPedido(
            @PathVariable Integer id,
            @Valid @RequestBody AgregarProductoRequestDTO requestDTO) {
        DetallePedidoResponseDTO detalle = pedidoService.agregarProductoAPedido(id, requestDTO);
        return new ResponseEntity<>(ApiResponse.created(detalle, "Producto agregado al pedido exitosamente"),
                HttpStatus.CREATED);
    }
}
