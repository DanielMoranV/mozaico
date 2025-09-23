package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.domain.enums.producto.EstadoProducto;
import com.djasoft.mozaico.services.ProductoService;
import com.djasoft.mozaico.web.dtos.ProductoRequestDTO;
import com.djasoft.mozaico.web.dtos.ProductoResponseDTO;
import com.djasoft.mozaico.web.dtos.ProductoUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> crearProducto(@Valid @RequestBody ProductoRequestDTO requestDTO) {
        ProductoResponseDTO nuevoProducto = productoService.crearProducto(requestDTO);
        return new ResponseEntity<>(ApiResponse.created(nuevoProducto, "Producto creado exitosamente"),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> obtenerTodosLosProductos() {
        List<ProductoResponseDTO> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(ApiResponse.success(productos, "Productos obtenidos exitosamente"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> obtenerProductoPorId(@PathVariable Long id) {
        ProductoResponseDTO producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(ApiResponse.success(producto, "Producto encontrado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody ProductoUpdateDTO requestDTO) {
        ProductoResponseDTO productoActualizado = productoService.actualizarProducto(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.success(productoActualizado, "Producto actualizado exitosamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminarProducto(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Producto eliminado exitosamente"));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<List<ProductoResponseDTO>>> buscarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Long idCategoria,
            @RequestParam(required = false) Boolean disponible,
            @RequestParam(required = false) Boolean requierePreparacion,
            @RequestParam(required = false) Boolean esAlcoholico,
            @RequestParam(required = false) EstadoProducto estado,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false, defaultValue = "AND") String logic
    ) {
        List<ProductoResponseDTO> productos = productoService.buscarProductos(
                nombre, descripcion, idCategoria, disponible, requierePreparacion, esAlcoholico, estado, searchTerm, logic
        );
        return ResponseEntity.ok(ApiResponse.success(productos, "Búsqueda de productos exitosa"));
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> activarProducto(@PathVariable Long id) {
        ProductoResponseDTO producto = productoService.activarProducto(id);
        return ResponseEntity.ok(ApiResponse.success(producto, "Producto activado exitosamente"));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> desactivarProducto(@PathVariable Long id) {
        ProductoResponseDTO producto = productoService.desactivarProducto(id);
        return ResponseEntity.ok(ApiResponse.success(producto, "Producto desactivado exitosamente"));
    }
}
