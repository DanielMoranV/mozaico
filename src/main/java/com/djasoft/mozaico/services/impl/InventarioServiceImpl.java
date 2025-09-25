package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Inventario;
import com.djasoft.mozaico.domain.entities.Producto;
import com.djasoft.mozaico.domain.repositories.InventarioRepository;
import com.djasoft.mozaico.domain.repositories.ProductoRepository;
import com.djasoft.mozaico.services.InventarioService;
import com.djasoft.mozaico.web.dtos.InventarioRequestDTO;
import com.djasoft.mozaico.web.dtos.InventarioResponseDTO;
import com.djasoft.mozaico.web.dtos.InventarioUpdateDTO;
import com.djasoft.mozaico.web.dtos.ProductoResponseDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public InventarioResponseDTO crearInventario(InventarioRequestDTO inventarioRequestDTO) {
        Producto producto = productoRepository.findById(inventarioRequestDTO.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Producto no encontrado con el id: " + inventarioRequestDTO.getIdProducto()));

        // Verificar si ya existe un registro de inventario para este producto
        inventarioRepository.findByProductoIdProducto(inventarioRequestDTO.getIdProducto())
                .ifPresent(inv -> {
                    throw new ResourceNotFoundException("Ya existe un registro de inventario para el producto con id: "
                            + inventarioRequestDTO.getIdProducto());
                });

        Inventario nuevoInventario = Inventario.builder()
                .producto(producto)
                .stockActual(inventarioRequestDTO.getStockActual())
                .stockMinimo(inventarioRequestDTO.getStockMinimo())
                .stockMaximo(inventarioRequestDTO.getStockMaximo())
                .costoUnitario(inventarioRequestDTO.getCostoUnitario())
                .build();

        Inventario inventarioGuardado = inventarioRepository.save(nuevoInventario);
        checkLowStock(inventarioGuardado);
        return mapToResponseDTO(inventarioGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> obtenerTodosLosInventarios() {
        return inventarioRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventarioResponseDTO obtenerInventarioPorId(Integer id) {
        return inventarioRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con el id: " + id));
    }

    @Override
    @Transactional
    public InventarioResponseDTO actualizarInventario(Integer id, InventarioUpdateDTO inventarioUpdateDTO) {
        Inventario inventarioExistente = inventarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventario no encontrado con el id: " + id));

        if (inventarioUpdateDTO.getIdProducto() != null) {
            Producto producto = productoRepository.findById(inventarioUpdateDTO.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con el id: " + inventarioUpdateDTO.getIdProducto()));
            inventarioExistente.setProducto(producto);
        }
        if (inventarioUpdateDTO.getStockActual() != null) {
            inventarioExistente.setStockActual(inventarioUpdateDTO.getStockActual());
        }
        if (inventarioUpdateDTO.getStockMinimo() != null) {
            inventarioExistente.setStockMinimo(inventarioUpdateDTO.getStockMinimo());
        }
        if (inventarioUpdateDTO.getStockMaximo() != null) {
            inventarioExistente.setStockMaximo(inventarioUpdateDTO.getStockMaximo());
        }
        if (inventarioUpdateDTO.getCostoUnitario() != null) {
            inventarioExistente.setCostoUnitario(inventarioUpdateDTO.getCostoUnitario());
        }

        Inventario inventarioActualizado = inventarioRepository.save(inventarioExistente);
        checkLowStock(inventarioActualizado);
        return mapToResponseDTO(inventarioActualizado);
    }

    @Override
    @Transactional
    public void eliminarInventario(Integer id) {
        if (!inventarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inventario no encontrado con el id: " + id);
        }
        inventarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public InventarioResponseDTO actualizarStockPorVenta(Long idProducto, Integer cantidadVendida) {
        Inventario inventario = inventarioRepository.findByProductoIdProducto(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registro de inventario no encontrado para el producto con id: " + idProducto));

        int nuevoStock = inventario.getStockActual() - cantidadVendida;
        if (nuevoStock < 0) {
            throw new ResourceNotFoundException("Stock insuficiente para el producto con id: " + idProducto);
        }
        inventario.setStockActual(nuevoStock);
        Inventario inventarioActualizado = inventarioRepository.save(inventario);
        checkLowStock(inventarioActualizado);
        return mapToResponseDTO(inventarioActualizado);
    }

    @Override
    @Transactional
    public InventarioResponseDTO actualizarStockPorCompra(Long idProducto, Integer cantidadComprada) {
        Inventario inventario = inventarioRepository.findByProductoIdProducto(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registro de inventario no encontrado para el producto con id: " + idProducto));

        int nuevoStock = inventario.getStockActual() + cantidadComprada;
        inventario.setStockActual(nuevoStock);
        Inventario inventarioActualizado = inventarioRepository.save(inventario);
        checkLowStock(inventarioActualizado);
        return mapToResponseDTO(inventarioActualizado);
    }

    private void checkLowStock(Inventario inventario) {
        if (inventario.getStockActual() <= inventario.getStockMinimo()) {
            System.out.println("ALERTA DE STOCK BAJO: El producto " + inventario.getProducto().getNombre() + " (ID: "
                    + inventario.getProducto().getIdProducto() + ") tiene un stock actual de "
                    + inventario.getStockActual() + ", que es igual o inferior al stock mínimo de "
                    + inventario.getStockMinimo() + ".");
            // Aquí se podría integrar un sistema de notificación (email, SMS, etc.)
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventarioResponseDTO> buscarInventarios(Long idProducto, Integer stockActualMin,
            Integer stockActualMax, Integer stockMinimo, Integer stockMaximo, String searchTerm, String logic) {
        Specification<Inventario> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (idProducto != null) {
                predicates.add(criteriaBuilder.equal(root.get("producto").get("idProducto"), idProducto));
            }
            if (stockActualMin != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("stockActual"), stockActualMin));
            }
            if (stockActualMax != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("stockActual"), stockActualMax));
            }
            if (stockMinimo != null) {
                predicates.add(criteriaBuilder.equal(root.get("stockMinimo"), stockMinimo));
            }
            if (stockMaximo != null) {
                predicates.add(criteriaBuilder.equal(root.get("stockMaximo"), stockMaximo));
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate globalSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("producto").get("nombre")),
                                lowerSearchTerm));
                predicates.add(globalSearch);
            }

            if (predicates.isEmpty()) {
                return null;
            }

            if ("OR".equalsIgnoreCase(logic)) {
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            } else {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };

        return inventarioRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private InventarioResponseDTO mapToResponseDTO(Inventario inventario) {
        ProductoResponseDTO productoDTO = null;
        if (inventario.getProducto() != null) {
            productoDTO = ProductoResponseDTO.builder()
                    .idProducto(inventario.getProducto().getIdProducto())
                    .nombre(inventario.getProducto().getNombre())
                    .build();
        }

        return InventarioResponseDTO.builder()
                .idInventario(inventario.getIdInventario())
                .producto(productoDTO)
                .stockActual(inventario.getStockActual())
                .stockMinimo(inventario.getStockMinimo())
                .stockMaximo(inventario.getStockMaximo())
                .costoUnitario(inventario.getCostoUnitario())
                .fechaActualizacion(inventario.getFechaActualizacion())
                .build();
    }
}