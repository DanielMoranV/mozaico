package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.domain.entities.DetalleCompra;
import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.repositories.DetalleCompraRepository;
import com.djasoft.mozaico.domain.entities.Compra;
import com.djasoft.mozaico.domain.entities.Proveedor;
import com.djasoft.mozaico.domain.enums.compra.EstadoCompra;
import com.djasoft.mozaico.domain.repositories.CompraRepository;
import com.djasoft.mozaico.domain.repositories.ProveedorRepository;
import com.djasoft.mozaico.security.annotations.Auditable;
import com.djasoft.mozaico.security.annotations.RequireCompanyContext;
import com.djasoft.mozaico.security.annotations.RequirePermission;
import com.djasoft.mozaico.services.CompraService;
import com.djasoft.mozaico.services.InventarioService;
import com.djasoft.mozaico.web.dtos.CompraRequestDTO;
import com.djasoft.mozaico.web.dtos.CompraResponseDTO;
import com.djasoft.mozaico.web.dtos.CompraUpdateDTO;
import com.djasoft.mozaico.web.dtos.ProveedorResponseDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import com.djasoft.mozaico.web.exceptions.UnauthorizedException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@RequireCompanyContext
public class CompraServiceImpl implements CompraService {

    private final CompraRepository compraRepository;
    private final ProveedorRepository proveedorRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final InventarioService inventarioService;

    @Override
    @Transactional
    @RequirePermission({"MANAGE_PURCHASES", "ALL_PERMISSIONS"})
    @Auditable(action = "CREAR_COMPRA", entity = "Compra")
    public CompraResponseDTO crearCompra(CompraRequestDTO compraRequestDTO) {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Usuario no autenticado");
        }

        Proveedor proveedor = proveedorRepository.findById(compraRequestDTO.getIdProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con el id: " + compraRequestDTO.getIdProveedor()));

        // Validar que el proveedor pertenece a la empresa del usuario
        if (!proveedor.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new UnauthorizedException("No tiene permisos para acceder a este proveedor");
        }

        Compra nuevaCompra = Compra.builder()
                .proveedor(proveedor)
                .fechaCompra(compraRequestDTO.getFechaCompra())
                .total(compraRequestDTO.getTotal())
                .estado(compraRequestDTO.getEstado() != null ? compraRequestDTO.getEstado() : EstadoCompra.PENDIENTE)
                .observaciones(compraRequestDTO.getObservaciones())
                .usuarioCreacion(currentUser)
                .empresa(currentUser.getEmpresa())
                .build();

        Compra compraGuardada = compraRepository.save(nuevaCompra);
        return mapToResponseDTO(compraGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    @RequirePermission({"VIEW_PURCHASES", "MANAGE_PURCHASES", "ALL_PERMISSIONS"})
    public List<CompraResponseDTO> obtenerTodasLasCompras() {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Usuario no autenticado");
        }

        return compraRepository.findAll().stream()
                .filter(compra -> compra.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa()))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @RequirePermission({"VIEW_PURCHASES", "MANAGE_PURCHASES", "ALL_PERMISSIONS"})
    public CompraResponseDTO obtenerCompraPorId(Integer id) {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Usuario no autenticado");
        }

        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con el id: " + id));

        // Validar que la compra pertenece a la empresa del usuario
        if (!compra.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new UnauthorizedException("No tiene permisos para acceder a esta compra");
        }

        return mapToResponseDTO(compra);
    }

    @Override
    @Transactional
    @RequirePermission({"MANAGE_PURCHASES", "ALL_PERMISSIONS"})
    @Auditable(action = "ACTUALIZAR_COMPRA", entity = "Compra")
    public CompraResponseDTO actualizarCompra(Integer id, CompraUpdateDTO compraUpdateDTO) {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthorizedException("Usuario no autenticado");
        }

        Compra compraExistente = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con el id: " + id));

        // Validar que la compra pertenece a la empresa del usuario
        if (!compraExistente.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new UnauthorizedException("No tiene permisos para modificar esta compra");
        }

        if (compraUpdateDTO.getIdProveedor() != null) {
            Proveedor proveedor = proveedorRepository.findById(compraUpdateDTO.getIdProveedor())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con el id: " + compraUpdateDTO.getIdProveedor()));

            // Validar que el proveedor pertenece a la empresa del usuario
            if (!proveedor.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
                throw new UnauthorizedException("No tiene permisos para usar este proveedor");
            }

            compraExistente.setProveedor(proveedor);
        }
        if (compraUpdateDTO.getFechaCompra() != null) {
            compraExistente.setFechaCompra(compraUpdateDTO.getFechaCompra());
        }
        if (compraUpdateDTO.getTotal() != null) {
            compraExistente.setTotal(compraUpdateDTO.getTotal());
        }
        if (compraUpdateDTO.getEstado() != null) {
            compraExistente.setEstado(compraUpdateDTO.getEstado());
        }
        if (compraUpdateDTO.getObservaciones() != null) {
            compraExistente.setObservaciones(compraUpdateDTO.getObservaciones());
        }

        Compra compraActualizada = compraRepository.save(compraExistente);
        return mapToResponseDTO(compraActualizada);
    }

    @Override
    @Transactional
    public void eliminarCompra(Integer id) {
        if (!compraRepository.existsById(id)) {
            throw new ResourceNotFoundException("Compra no encontrada con el id: " + id);
        }
        compraRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CompraResponseDTO cambiarEstadoCompra(Integer id, EstadoCompra nuevoEstado) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con el id: " + id));
        compra.setEstado(nuevoEstado);
        Compra compraActualizada = compraRepository.save(compra);

        if (nuevoEstado == EstadoCompra.RECIBIDA) {
            this.actualizarInventarioPorCompra(id);
        }

        return mapToResponseDTO(compraActualizada);
    }

    @Override
    @Transactional
    public void actualizarInventarioPorCompra(Integer idCompra) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con el id: " + idCompra));

        List<DetalleCompra> detalles = detalleCompraRepository.findByCompra(compra);

        for (DetalleCompra detalle : detalles) {
            inventarioService.actualizarStockPorCompra(
                    detalle.getProducto().getIdProducto(),
                    detalle.getCantidad()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraResponseDTO> buscarCompras(Integer idProveedor, LocalDate fechaCompraDesde, LocalDate fechaCompraHasta, EstadoCompra estado, String searchTerm, String logic) {
        Specification<Compra> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (idProveedor != null) {
                predicates.add(criteriaBuilder.equal(root.get("proveedor").get("idProveedor"), idProveedor));
            }
            if (fechaCompraDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaCompra"), fechaCompraDesde));
            }
            if (fechaCompraHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaCompra"), fechaCompraHasta));
            }
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate globalSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("observaciones")), lowerSearchTerm)
                );
                predicates.add(globalSearch);
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction(); // Equivalente a WHERE 1=1
            }

            if ("OR".equalsIgnoreCase(logic)) {
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            } else {
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        };

        return compraRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompraResponseDTO recalcularTotalesCompra(Integer idCompra) {
        Compra compra = compraRepository.findById(idCompra)
                .orElseThrow(() -> new ResourceNotFoundException("Compra no encontrada con el id: " + idCompra));

        List<DetalleCompra> detalles = detalleCompraRepository.findByCompra(compra);

        BigDecimal totalCalculado = detalles.stream()
                .map(DetalleCompra::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        compra.setTotal(totalCalculado);

        Compra compraActualizada = compraRepository.save(compra);
        return mapToResponseDTO(compraActualizada);
    }

    private CompraResponseDTO mapToResponseDTO(Compra compra) {
        ProveedorResponseDTO proveedorDTO = null;
        if (compra.getProveedor() != null) {
            proveedorDTO = ProveedorResponseDTO.builder()
                    .idProveedor(compra.getProveedor().getIdProveedor())
                    .nombre(compra.getProveedor().getNombre())
                    .build();
        }

        return CompraResponseDTO.builder()
                .idCompra(compra.getIdCompra())
                .proveedor(proveedorDTO)
                .fechaCompra(compra.getFechaCompra())
                .total(compra.getTotal())
                .estado(compra.getEstado())
                .observaciones(compra.getObservaciones())
                .build();
    }
}
