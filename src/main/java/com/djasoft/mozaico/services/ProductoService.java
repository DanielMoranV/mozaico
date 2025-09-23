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
}
