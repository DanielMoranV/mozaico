package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Categoria;
import com.djasoft.mozaico.domain.entities.Producto;
import com.djasoft.mozaico.domain.enums.producto.EstadoProducto;
import com.djasoft.mozaico.domain.repositories.CategoriaRepository;
import com.djasoft.mozaico.domain.repositories.ProductoRepository;
import com.djasoft.mozaico.services.ProductoService;
import com.djasoft.mozaico.web.dtos.CategoriaResponseDTO;
import com.djasoft.mozaico.web.dtos.ProductoRequestDTO;
import com.djasoft.mozaico.web.dtos.ProductoResponseDTO;
import com.djasoft.mozaico.web.dtos.ProductoUpdateDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    @Transactional
    public ProductoResponseDTO crearProducto(ProductoRequestDTO requestDTO) {
        Categoria categoria = categoriaRepository.findById(requestDTO.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con el id: " + requestDTO.getIdCategoria()));

        Producto nuevoProducto = Producto.builder()
                .nombre(requestDTO.getNombre())
                .descripcion(requestDTO.getDescripcion())
                .precio(requestDTO.getPrecio())
                .categoria(categoria)
                .tiempoPreparacion(requestDTO.getTiempoPreparacion())
                .disponible(requestDTO.getDisponible() != null ? requestDTO.getDisponible() : true)
                .imagenUrl(requestDTO.getImagenUrl())
                .ingredientes(requestDTO.getIngredientes())
                .calorias(requestDTO.getCalorias())
                .codigoBarras(requestDTO.getCodigoBarras())
                .marca(requestDTO.getMarca())
                .presentacion(requestDTO.getPresentacion())
                .requierePreparacion(
                        requestDTO.getRequierePreparacion() != null ? requestDTO.getRequierePreparacion() : false)
                .esAlcoholico(requestDTO.getEsAlcoholico() != null ? requestDTO.getEsAlcoholico() : false)
                .estado(requestDTO.getEstado() != null ? requestDTO.getEstado() : EstadoProducto.ACTIVO)
                .build();

        Producto productoGuardado = productoRepository.save(nuevoProducto);
        return mapToResponseDTO(productoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> obtenerTodosLosProductos() {
        return productoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponseDTO obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el id: " + id));
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizarProducto(Long id, ProductoUpdateDTO requestDTO) {
        Producto productoExistente = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el id: " + id));

        Categoria categoria = categoriaRepository.findById(requestDTO.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Categoría no encontrada con el id: " + requestDTO.getIdCategoria()));

        productoExistente.setNombre(requestDTO.getNombre());
        productoExistente.setDescripcion(requestDTO.getDescripcion());
        productoExistente.setPrecio(requestDTO.getPrecio());
        productoExistente.setCategoria(categoria);
        productoExistente.setTiempoPreparacion(requestDTO.getTiempoPreparacion());
        productoExistente.setDisponible(
                requestDTO.getDisponible() != null ? requestDTO.getDisponible() : productoExistente.getDisponible());
        productoExistente.setImagenUrl(requestDTO.getImagenUrl());
        productoExistente.setIngredientes(requestDTO.getIngredientes());
        productoExistente.setCalorias(requestDTO.getCalorias());
        productoExistente.setCodigoBarras(requestDTO.getCodigoBarras());
        productoExistente.setMarca(requestDTO.getMarca());
        productoExistente.setPresentacion(requestDTO.getPresentacion());
        productoExistente.setRequierePreparacion(
                requestDTO.getRequierePreparacion() != null ? requestDTO.getRequierePreparacion()
                        : productoExistente.getRequierePreparacion());
        productoExistente.setEsAlcoholico(requestDTO.getEsAlcoholico() != null ? requestDTO.getEsAlcoholico()
                : productoExistente.getEsAlcoholico());
        productoExistente
                .setEstado(requestDTO.getEstado() != null ? requestDTO.getEstado() : productoExistente.getEstado());

        Producto productoActualizado = productoRepository.save(productoExistente);
        return mapToResponseDTO(productoActualizado);
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con el id: " + id);
        }
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductoResponseDTO activarProducto(Long id) {
        return cambiarEstadoProducto(id, EstadoProducto.ACTIVO);
    }

    @Override
    @Transactional
    public ProductoResponseDTO desactivarProducto(Long id) {
        return cambiarEstadoProducto(id, EstadoProducto.INACTIVO);
    }

    private ProductoResponseDTO cambiarEstadoProducto(Long id, EstadoProducto estado) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el id: " + id));
        producto.setEstado(estado);
        productoRepository.save(producto);
        return mapToResponseDTO(producto);
    }

    private ProductoResponseDTO mapToResponseDTO(Producto producto) {
        return ProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .categoria(mapCategoriaToResponseDTO(producto.getCategoria()))
                .tiempoPreparacion(producto.getTiempoPreparacion())
                .disponible(producto.getDisponible())
                .imagenUrl(producto.getImagenUrl())
                .ingredientes(producto.getIngredientes())
                .calorias(producto.getCalorias())
                .codigoBarras(producto.getCodigoBarras())
                .marca(producto.getMarca())
                .presentacion(producto.getPresentacion())
                .requierePreparacion(producto.getRequierePreparacion())
                .esAlcoholico(producto.getEsAlcoholico())
                .estado(producto.getEstado())
                .fechaCreacion(producto.getFechaCreacion())
                .fechaActualizacion(producto.getFechaActualizacion())
                .build();
    }

    private CategoriaResponseDTO mapCategoriaToResponseDTO(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .fechaCreacion(categoria.getFechaCreacion())
                .fechaActualizacion(categoria.getFechaActualizacion())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponseDTO> buscarProductos(
            String nombre,
            String descripcion,
            Long idCategoria,
            Boolean disponible,
            Boolean requierePreparacion,
            Boolean esAlcoholico,
            EstadoProducto estado,
            String searchTerm,
            String logic) {
        Specification<Producto> spec = (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (nombre != null && !nombre.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase() + "%"));
            }
            if (descripcion != null && !descripcion.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")),
                        "%" + descripcion.toLowerCase() + "%"));
            }
            if (idCategoria != null) {
                predicates.add(criteriaBuilder.equal(root.get("categoria").get("idCategoria"), idCategoria));
            }
            if (disponible != null) {
                predicates.add(criteriaBuilder.equal(root.get("disponible"), disponible));
            }
            if (requierePreparacion != null) {
                predicates.add(criteriaBuilder.equal(root.get("requierePreparacion"), requierePreparacion));
            }
            if (esAlcoholico != null) {
                predicates.add(criteriaBuilder.equal(root.get("esAlcoholico"), esAlcoholico));
            }
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                jakarta.persistence.criteria.Predicate globalSearchPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("descripcion")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("ingredientes")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("marca")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("presentacion")), lowerSearchTerm));
                predicates.add(globalSearchPredicate);
            }

            if (predicates.isEmpty()) {
                return null;
            }

            if ("OR".equalsIgnoreCase(logic)) {
                return criteriaBuilder.or(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
            } else { // Default a AND
                return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
            }
        };

        return productoRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
}
