package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.domain.entities.Empresa;
import com.djasoft.mozaico.domain.enums.producto.EstadoProducto;
import com.djasoft.mozaico.domain.repositories.EmpresaRepository;
import com.djasoft.mozaico.services.ProductoService;
import com.djasoft.mozaico.web.dtos.CartaPublicaResponseDTO;
import com.djasoft.mozaico.web.dtos.EmpresaPublicaDTO;
import com.djasoft.mozaico.web.dtos.ProductoRequestDTO;
import com.djasoft.mozaico.web.dtos.ProductoResponseDTO;
import com.djasoft.mozaico.web.dtos.ProductoUpdateDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;
    private final EmpresaRepository empresaRepository;

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

    @PostMapping("/{id}/image")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        ProductoResponseDTO productoActualizado = productoService.updateProductImage(id, file);
        return ResponseEntity.ok(ApiResponse.success(productoActualizado, "Imagen del producto actualizada exitosamente"));
    }

    /**
     * Endpoint público para obtener la carta/menú de productos disponibles de una empresa
     * GET /api/v1/productos/public/{slug}/carta
     *
     * Este endpoint NO requiere autenticación y muestra solo productos:
     * - De la empresa especificada por slug único (multitenant seguro)
     * - Disponibles (disponible = true)
     * - Activos (estado = ACTIVO)
     *
     * Incluye información de la empresa (nombre, logo, dirección, etc.) junto con los productos
     * Ideal para mostrar la carta a clientes en aplicaciones web/móviles y códigos QR
     *
     * @param slug Slug único de la empresa (ej: "restaurante-mozaico")
     * @param idCategoria ID de categoría para filtrar (opcional)
     * @return Información de la empresa y lista de productos disponibles
     */
    @GetMapping("/public/{slug}/carta")
    public ResponseEntity<ApiResponse<CartaPublicaResponseDTO>> obtenerCartaPublica(
            @PathVariable String slug,
            @RequestParam(required = false) Long idCategoria) {

        // Buscar la empresa por slug
        Empresa empresa = empresaRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con slug: " + slug));

        // Obtener los productos de la empresa
        List<ProductoResponseDTO> productos = productoService.buscarProductosPorSlugEmpresa(
                slug,
                idCategoria,  // idCategoria (opcional para filtrar por categoría)
                true,  // disponible = true (solo productos disponibles)
                EstadoProducto.ACTIVO  // estado = ACTIVO
        );

        // Mapear información pública de la empresa
        EmpresaPublicaDTO empresaPublica = EmpresaPublicaDTO.builder()
                .nombre(empresa.getNombre())
                .slug(empresa.getSlug())
                .descripcion(empresa.getDescripcion())
                .direccion(empresa.getDireccion())
                .telefono(empresa.getTelefono())
                .email(empresa.getEmail())
                .logoUrl(empresa.getLogoUrl())
                .paginaWeb(empresa.getPaginaWeb())
                .moneda(empresa.getMoneda())
                .build();

        // Construir respuesta completa
        CartaPublicaResponseDTO cartaPublica = CartaPublicaResponseDTO.builder()
                .empresa(empresaPublica)
                .productos(productos)
                .build();

        return ResponseEntity.ok(ApiResponse.success(cartaPublica, "Carta de productos obtenida exitosamente"));
    }

    /**
     * Endpoint público para obtener productos por categoría de una empresa
     * GET /api/v1/productos/public/{slug}/carta/por-categoria
     *
     * Este endpoint NO requiere autenticación
     * Agrupa los productos por categoría para una mejor visualización
     * Incluye información de la empresa junto con los productos
     *
     * @param slug Slug único de la empresa (ej: "restaurante-mozaico")
     * @return Información de la empresa y lista de productos agrupados por categoría
     */
    @GetMapping("/public/{slug}/carta/por-categoria")
    public ResponseEntity<ApiResponse<CartaPublicaResponseDTO>> obtenerCartaPorCategoria(
            @PathVariable String slug) {

        // Buscar la empresa por slug
        Empresa empresa = empresaRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con slug: " + slug));

        // Obtener los productos de la empresa
        List<ProductoResponseDTO> productos = productoService.buscarProductosPorSlugEmpresa(
                slug,
                null,  // todas las categorías
                true,  // solo disponibles
                EstadoProducto.ACTIVO  // solo activos
        );

        // Mapear información pública de la empresa
        EmpresaPublicaDTO empresaPublica = EmpresaPublicaDTO.builder()
                .nombre(empresa.getNombre())
                .slug(empresa.getSlug())
                .descripcion(empresa.getDescripcion())
                .direccion(empresa.getDireccion())
                .telefono(empresa.getTelefono())
                .email(empresa.getEmail())
                .logoUrl(empresa.getLogoUrl())
                .paginaWeb(empresa.getPaginaWeb())
                .moneda(empresa.getMoneda())
                .build();

        // Construir respuesta completa
        CartaPublicaResponseDTO cartaPublica = CartaPublicaResponseDTO.builder()
                .empresa(empresaPublica)
                .productos(productos)
                .build();

        return ResponseEntity.ok(ApiResponse.success(cartaPublica, "Carta de productos por categoría obtenida exitosamente"));
    }
}
