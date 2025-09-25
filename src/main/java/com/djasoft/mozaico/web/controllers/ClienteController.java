package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.ClienteService;
import com.djasoft.mozaico.web.dtos.ClienteRequestDTO;
import com.djasoft.mozaico.web.dtos.ClienteResponseDTO;
import com.djasoft.mozaico.web.dtos.ClienteUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @PostMapping
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> crearCliente(@Valid @RequestBody ClienteRequestDTO requestDTO) {
        ClienteResponseDTO nuevoCliente = clienteService.crearCliente(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevoCliente, "Cliente creado exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClienteResponseDTO>>> obtenerTodosLosClientes() {
        List<ClienteResponseDTO> clientes = clienteService.obtenerTodosLosClientes();
        return ResponseEntity.ok(ApiResponse.success(clientes, "Clientes obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> obtenerClientePorId(@PathVariable Integer id) {
        ClienteResponseDTO cliente = clienteService.obtenerClientePorId(id);
        return ResponseEntity.ok(ApiResponse.success(cliente, "Cliente encontrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> actualizarCliente(
            @PathVariable Integer id,
            @Valid @RequestBody ClienteUpdateDTO requestDTO) {
        ClienteResponseDTO clienteActualizado = clienteService.actualizarCliente(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(clienteActualizado, "Cliente actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarCliente(@PathVariable Integer id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Cliente eliminado exitosamente"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> activarCliente(@PathVariable Integer id) {
        ClienteResponseDTO cliente = clienteService.cambiarEstadoCliente(id, true);
        return ResponseEntity.ok(ApiResponse.success(cliente, "Cliente activado exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<ClienteResponseDTO>> desactivarCliente(@PathVariable Integer id) {
        ClienteResponseDTO cliente = clienteService.cambiarEstadoCliente(id, false);
        return ResponseEntity.ok(ApiResponse.success(cliente, "Cliente desactivado exitosamente"));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<ClienteResponseDTO>>> buscarClientes(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "AND") String logic
    ) {
        List<ClienteResponseDTO> clientes = clienteService.buscarClientes(nombre, apellido, email, telefono, activo, searchTerm, logic);
        return ResponseEntity.ok(ApiResponse.success(clientes, "Búsqueda de clientes exitosa"));
    }
}
