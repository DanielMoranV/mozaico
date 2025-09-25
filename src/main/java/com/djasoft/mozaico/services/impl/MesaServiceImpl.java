package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Mesa;
import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import com.djasoft.mozaico.domain.repositories.MesaRepository;
import com.djasoft.mozaico.services.MesaService;
import com.djasoft.mozaico.web.dtos.MesaRequestDTO;
import com.djasoft.mozaico.web.dtos.MesaResponseDTO;
import com.djasoft.mozaico.web.dtos.MesaUpdateDTO;
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
public class MesaServiceImpl implements MesaService {

    private final MesaRepository mesaRepository;

    @Override
    @Transactional
    public MesaResponseDTO crearMesa(MesaRequestDTO mesaRequestDTO) {
        Mesa nuevaMesa = Mesa.builder()
                .numeroMesa(mesaRequestDTO.getNumeroMesa())
                .capacidad(mesaRequestDTO.getCapacidad())
                .ubicacion(mesaRequestDTO.getUbicacion())
                .estado(mesaRequestDTO.getEstado() != null ? mesaRequestDTO.getEstado() : EstadoMesa.DISPONIBLE)
                .observaciones(mesaRequestDTO.getObservaciones())
                .build();

        Mesa mesaGuardada = mesaRepository.save(nuevaMesa);
        return mapToResponseDTO(mesaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaResponseDTO> obtenerTodasLasMesas() {
        return mesaRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MesaResponseDTO obtenerMesaPorId(Integer id) {
        return mesaRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con el id: " + id));
    }

    @Override
    @Transactional
    public MesaResponseDTO actualizarMesa(Integer id, MesaUpdateDTO mesaUpdateDTO) {
        Mesa mesaExistente = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con el id: " + id));

        if (mesaUpdateDTO.getNumeroMesa() != null) {
            mesaExistente.setNumeroMesa(mesaUpdateDTO.getNumeroMesa());
        }
        if (mesaUpdateDTO.getCapacidad() != null) {
            mesaExistente.setCapacidad(mesaUpdateDTO.getCapacidad());
        }
        if (mesaUpdateDTO.getUbicacion() != null) {
            mesaExistente.setUbicacion(mesaUpdateDTO.getUbicacion());
        }
        if (mesaUpdateDTO.getEstado() != null) {
            mesaExistente.setEstado(mesaUpdateDTO.getEstado());
        }
        if (mesaUpdateDTO.getObservaciones() != null) {
            mesaExistente.setObservaciones(mesaUpdateDTO.getObservaciones());
        }

        Mesa mesaActualizada = mesaRepository.save(mesaExistente);
        return mapToResponseDTO(mesaActualizada);
    }

    @Override
    @Transactional
    public void eliminarMesa(Integer id) {
        if (!mesaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mesa no encontrada con el id: " + id);
        }
        mesaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MesaResponseDTO cambiarEstadoMesa(Integer id, EstadoMesa nuevoEstado) {
        Mesa mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con el id: " + id));
        mesa.setEstado(nuevoEstado);
        Mesa mesaActualizada = mesaRepository.save(mesa);
        return mapToResponseDTO(mesaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MesaResponseDTO> buscarMesas(Integer numeroMesa, Integer capacidad, String ubicacion, EstadoMesa estado, String searchTerm, String logic) {
        Specification<Mesa> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (numeroMesa != null) {
                predicates.add(criteriaBuilder.equal(root.get("numeroMesa"), numeroMesa));
            }
            if (capacidad != null) {
                predicates.add(criteriaBuilder.equal(root.get("capacidad"), capacidad));
            }
            if (ubicacion != null && !ubicacion.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("ubicacion")), "%" + ubicacion.toLowerCase() + "%"));
            }
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate globalSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("ubicacion")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("observaciones")), lowerSearchTerm)
                );
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

        return mesaRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private MesaResponseDTO mapToResponseDTO(Mesa mesa) {
        return MesaResponseDTO.builder()
                .idMesa(mesa.getIdMesa())
                .numeroMesa(mesa.getNumeroMesa())
                .capacidad(mesa.getCapacidad())
                .ubicacion(mesa.getUbicacion())
                .estado(mesa.getEstado())
                .observaciones(mesa.getObservaciones())
                .fechaCreacion(mesa.getFechaCreacion())
                .build();
    }
}
