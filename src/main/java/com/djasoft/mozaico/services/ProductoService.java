package com.djasoft.mozaico.services;

import com.djasoft.mozaico.domain.enums.producto.EstadoProducto;
import com.djasoft.mozaico.web.dtos.ProductoRequestDTO;
import com.djasoft.mozaico.web.dtos.ProductoResponseDTO;
import com.djasoft.mozaico.web.dtos.ProductoUpdateDTO;

import java.util.List;

public interface ProductoService {

    ProductoResponseDTO crearProducto(ProductoRequestDTO requestDTO);

    List<ProductoResponseDTO> obtenerTodosLosProductos();

    ProductoResponseDTO obtenerProductoPorId(Long id);

    ProductoResponseDTO actualizarProducto(Long id, ProductoUpdateDTO requestDTO);

    void eliminarProducto(Long id);

    List<ProductoResponseDTO> buscarProductos(
            String nombre,
            String descripcion,
            Long idCategoria,
            Boolean disponible,
            Boolean requierePreparacion,
            Boolean esAlcoholico,
            EstadoProducto estado,
            String searchTerm,
            String logic
    );

    ProductoResponseDTO activarProducto(Long id);

    ProductoResponseDTO desactivarProducto(Long id);

    ProductoResponseDTO updateProductImage(Long id, org.springframework.web.multipart.MultipartFile file);

    /**
     * Buscar productos de una empresa específica por ID (multitenant)
     * @param idEmpresa ID de la empresa
     * @param idCategoria ID de categoría (opcional)
     * @param disponible Si el producto está disponible
     * @param estado Estado del producto
     * @return Lista de productos que cumplen los criterios
     */
    List<ProductoResponseDTO> buscarProductosPorEmpresa(
            Integer idEmpresa,
            Long idCategoria,
            Boolean disponible,
            EstadoProducto estado
    );

    /**
     * Buscar productos de una empresa por su slug único (para carta pública)
     * @param slug Slug único de la empresa
     * @param idCategoria ID de categoría (opcional)
     * @param disponible Si el producto está disponible
     * @param estado Estado del producto
     * @return Lista de productos que cumplen los criterios
     */
    List<ProductoResponseDTO> buscarProductosPorSlugEmpresa(
            String slug,
            Long idCategoria,
            Boolean disponible,
            EstadoProducto estado
    );
}
