package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.services.ProveedorService;
import com.djasoft.mozaico.web.dtos.ProveedorRequestDTO;
import com.djasoft.mozaico.web.dtos.ProveedorResponseDTO;
import com.djasoft.mozaico.web.dtos.ProveedorUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> crearProveedor(@Valid @RequestBody ProveedorRequestDTO requestDTO) {
        ProveedorResponseDTO nuevoProveedor = proveedorService.crearProveedor(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevoProveedor, "Proveedor creado exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProveedorResponseDTO>>> obtenerTodosLosProveedores() {
        List<ProveedorResponseDTO> proveedores = proveedorService.obtenerTodosLosProveedores();
        return ResponseEntity.ok(ApiResponse.success(proveedores, "Proveedores obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> obtenerProveedorPorId(@PathVariable Integer id) {
        ProveedorResponseDTO proveedor = proveedorService.obtenerProveedorPorId(id);
        return ResponseEntity.ok(ApiResponse.success(proveedor, "Proveedor encontrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> actualizarProveedor(
            @PathVariable Integer id,
            @Valid @RequestBody ProveedorUpdateDTO requestDTO) {
        ProveedorResponseDTO proveedorActualizado = proveedorService.actualizarProveedor(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(proveedorActualizado, "Proveedor actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarProveedor(@PathVariable Integer id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Proveedor eliminado exitosamente"));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<ProveedorResponseDTO>> cambiarEstadoProveedor(
            @PathVariable Integer id,
            @RequestParam boolean activo) {
        ProveedorResponseDTO proveedor = proveedorService.cambiarEstadoProveedor(id, activo);
        return ResponseEntity.ok(ApiResponse.success(proveedor, "Estado del Proveedor actualizado exitosamente"));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<ProveedorResponseDTO>>> buscarProveedores(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String contacto,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "AND") String logic
    ) {
        List<ProveedorResponseDTO> proveedores = proveedorService.buscarProveedores(nombre, contacto, telefono, email, activo, searchTerm, logic);
        return ResponseEntity.ok(ApiResponse.success(proveedores, "Búsqueda de Proveedores exitosa"));
    }
}
