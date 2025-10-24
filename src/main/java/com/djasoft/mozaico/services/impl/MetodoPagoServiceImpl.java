package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.MetodoPago;
import com.djasoft.mozaico.domain.repositories.MetodoPagoRepository;
import com.djasoft.mozaico.services.MetodoPagoService;
import com.djasoft.mozaico.web.dtos.MetodoPagoRequestDTO;
import com.djasoft.mozaico.web.dtos.MetodoPagoResponseDTO;
import com.djasoft.mozaico.web.dtos.MetodoPagoUpdateDTO;
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
public class MetodoPagoServiceImpl implements MetodoPagoService {

    private final MetodoPagoRepository metodoPagoRepository;

    @Override
    @Transactional
    public MetodoPagoResponseDTO crearMetodoPago(MetodoPagoRequestDTO metodoPagoRequestDTO) {
        MetodoPago nuevoMetodoPago = MetodoPago.builder()
                .nombre(metodoPagoRequestDTO.getNombre())
                .activo(metodoPagoRequestDTO.getActivo() != null ? metodoPagoRequestDTO.getActivo() : true)
                .build();

        MetodoPago metodoPagoGuardado = metodoPagoRepository.save(nuevoMetodoPago);
        return mapToResponseDTO(metodoPagoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPagoResponseDTO> obtenerTodosLosMetodosPago() {
        return metodoPagoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MetodoPagoResponseDTO obtenerMetodoPagoPorId(Integer id) {
        return metodoPagoRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Método de Pago no encontrado con el id: " + id));
    }

    @Override
    @Transactional
    public MetodoPagoResponseDTO actualizarMetodoPago(Integer id, MetodoPagoUpdateDTO metodoPagoUpdateDTO) {
        MetodoPago metodoPagoExistente = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Método de Pago no encontrado con el id: " + id));

        if (metodoPagoUpdateDTO.getNombre() != null) {
            metodoPagoExistente.setNombre(metodoPagoUpdateDTO.getNombre());
        }
        if (metodoPagoUpdateDTO.getActivo() != null) {
            metodoPagoExistente.setActivo(metodoPagoUpdateDTO.getActivo());
        }

        MetodoPago metodoPagoActualizado = metodoPagoRepository.save(metodoPagoExistente);
        return mapToResponseDTO(metodoPagoActualizado);
    }

    @Override
    @Transactional
    public void eliminarMetodoPago(Integer id) {
        if (!metodoPagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Método de Pago no encontrado con el id: " + id);
        }
        metodoPagoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MetodoPagoResponseDTO cambiarEstadoMetodoPago(Integer id, boolean activo) {
        MetodoPago metodoPago = metodoPagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Método de Pago no encontrado con el id: " + id));
        metodoPago.setActivo(activo);
        MetodoPago metodoPagoActualizado = metodoPagoRepository.save(metodoPago);
        return mapToResponseDTO(metodoPagoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MetodoPagoResponseDTO> buscarMetodosPago(String nombre, Boolean activo, String searchTerm, String logic) {
        Specification<MetodoPago> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nombre != null && !nombre.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%"));
            }
            if (activo != null) {
                predicates.add(criteriaBuilder.equal(root.get("activo"), activo));
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate globalSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), lowerSearchTerm)
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

        return metodoPagoRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private MetodoPagoResponseDTO mapToResponseDTO(MetodoPago metodoPago) {
        return MetodoPagoResponseDTO.builder()
                .idMetodo(metodoPago.getIdMetodo())
                .nombre(metodoPago.getNombre())
                .activo(metodoPago.getActivo())
                .build();
    }
}
