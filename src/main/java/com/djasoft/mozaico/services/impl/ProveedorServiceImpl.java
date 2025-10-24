package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Proveedor;
import com.djasoft.mozaico.domain.repositories.ProveedorRepository;
import com.djasoft.mozaico.services.ProveedorService;
import com.djasoft.mozaico.web.dtos.ProveedorRequestDTO;
import com.djasoft.mozaico.web.dtos.ProveedorResponseDTO;
import com.djasoft.mozaico.web.dtos.ProveedorUpdateDTO;
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
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    @Transactional
    public ProveedorResponseDTO crearProveedor(ProveedorRequestDTO proveedorRequestDTO) {
        Proveedor nuevoProveedor = Proveedor.builder()
                .nombre(proveedorRequestDTO.getNombre())
                .contacto(proveedorRequestDTO.getContacto())
                .telefono(proveedorRequestDTO.getTelefono())
                .email(proveedorRequestDTO.getEmail())
                .direccion(proveedorRequestDTO.getDireccion())
                .activo(proveedorRequestDTO.getActivo() != null ? proveedorRequestDTO.getActivo() : true)
                .build();

        Proveedor proveedorGuardado = proveedorRepository.save(nuevoProveedor);
        return mapToResponseDTO(proveedorGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponseDTO> obtenerTodosLosProveedores() {
        return proveedorRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponseDTO obtenerProveedorPorId(Integer id) {
        return proveedorRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con el id: " + id));
    }

    @Override
    @Transactional
    public ProveedorResponseDTO actualizarProveedor(Integer id, ProveedorUpdateDTO proveedorUpdateDTO) {
        Proveedor proveedorExistente = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con el id: " + id));

        if (proveedorUpdateDTO.getNombre() != null) {
            proveedorExistente.setNombre(proveedorUpdateDTO.getNombre());
        }
        if (proveedorUpdateDTO.getContacto() != null) {
            proveedorExistente.setContacto(proveedorUpdateDTO.getContacto());
        }
        if (proveedorUpdateDTO.getTelefono() != null) {
            proveedorExistente.setTelefono(proveedorUpdateDTO.getTelefono());
        }
        if (proveedorUpdateDTO.getEmail() != null) {
            proveedorExistente.setEmail(proveedorUpdateDTO.getEmail());
        }
        if (proveedorUpdateDTO.getDireccion() != null) {
            proveedorExistente.setDireccion(proveedorUpdateDTO.getDireccion());
        }
        if (proveedorUpdateDTO.getActivo() != null) {
            proveedorExistente.setActivo(proveedorUpdateDTO.getActivo());
        }

        Proveedor proveedorActualizado = proveedorRepository.save(proveedorExistente);
        return mapToResponseDTO(proveedorActualizado);
    }

    @Override
    @Transactional
    public void eliminarProveedor(Integer id) {
        if (!proveedorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proveedor no encontrado con el id: " + id);
        }
        proveedorRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProveedorResponseDTO cambiarEstadoProveedor(Integer id, boolean activo) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con el id: " + id));
        proveedor.setActivo(activo);
        Proveedor proveedorActualizado = proveedorRepository.save(proveedor);
        return mapToResponseDTO(proveedorActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponseDTO> buscarProveedores(String nombre, String contacto, String telefono, String email, Boolean activo, String searchTerm, String logic) {
        Specification<Proveedor> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombre != null && !nombre.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }
            if (contacto != null && !contacto.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("contacto")), "%" + contacto.toLowerCase() + "%"));
            }
            if (telefono != null && !telefono.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("telefono"), "%" + telefono + "%"));
            }
            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }
            if (activo != null) {
                predicates.add(criteriaBuilder.equal(root.get("activo"), activo));
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate globalSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("contacto")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("telefono")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("direccion")), lowerSearchTerm)
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

        return proveedorRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ProveedorResponseDTO mapToResponseDTO(Proveedor proveedor) {
        return ProveedorResponseDTO.builder()
                .idProveedor(proveedor.getIdProveedor())
                .nombre(proveedor.getNombre())
                .contacto(proveedor.getContacto())
                .telefono(proveedor.getTelefono())
                .email(proveedor.getEmail())
                .direccion(proveedor.getDireccion())
                .activo(proveedor.getActivo())
                .fechaCreacion(proveedor.getFechaCreacion())
                .build();
    }
}
