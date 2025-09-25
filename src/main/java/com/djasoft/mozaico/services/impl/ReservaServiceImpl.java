package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Cliente;
import com.djasoft.mozaico.domain.entities.Mesa;
import com.djasoft.mozaico.domain.entities.Reserva;
import com.djasoft.mozaico.domain.enums.reserva.EstadoReserva;
import com.djasoft.mozaico.domain.repositories.ClienteRepository;
import com.djasoft.mozaico.domain.repositories.MesaRepository;
import com.djasoft.mozaico.domain.repositories.ReservaRepository;
import com.djasoft.mozaico.services.ReservaService;
import com.djasoft.mozaico.web.dtos.ClienteResponseDTO;
import com.djasoft.mozaico.web.dtos.MesaResponseDTO;
import com.djasoft.mozaico.web.dtos.ReservaRequestDTO;
import com.djasoft.mozaico.web.dtos.ReservaResponseDTO;
import com.djasoft.mozaico.web.dtos.ReservaUpdateDTO;
import com.djasoft.mozaico.web.exceptions.ResourceConflictException;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    private static final long RESERVATION_DURATION_HOURS = 2;

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final MesaRepository mesaRepository;

    @Override
    @Transactional
    public ReservaResponseDTO crearReserva(ReservaRequestDTO reservaRequestDTO) {
        Cliente cliente = clienteRepository.findById(reservaRequestDTO.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con el id: " + reservaRequestDTO.getIdCliente()));

        Mesa mesa = mesaRepository.findById(reservaRequestDTO.getIdMesa())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mesa no encontrada con el id: " + reservaRequestDTO.getIdMesa()));

        // Validar disponibilidad de la mesa
        validarDisponibilidadMesa(mesa, reservaRequestDTO.getFechaHoraReserva(), null);

        Reserva nuevaReserva = Reserva.builder()
                .cliente(cliente)
                .mesa(mesa)
                .fechaHoraReserva(reservaRequestDTO.getFechaHoraReserva())
                .numeroPersonas(reservaRequestDTO.getNumeroPersonas())
                .estado(reservaRequestDTO.getEstado() != null ? reservaRequestDTO.getEstado() : EstadoReserva.PENDIENTE)
                .observaciones(reservaRequestDTO.getObservaciones())
                .build();

        Reserva reservaGuardada = reservaRepository.save(nuevaReserva);
        return mapToResponseDTO(reservaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> obtenerTodasLasReservas() {
        return reservaRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerReservaPorId(Integer id) {
        return reservaRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con el id: " + id));
    }

    @Override
    @Transactional
    public ReservaResponseDTO actualizarReserva(Integer id, ReservaUpdateDTO reservaUpdateDTO) {
        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con el id: " + id));

        if (reservaUpdateDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(reservaUpdateDTO.getIdCliente())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cliente no encontrado con el id: " + reservaUpdateDTO.getIdCliente()));
            reservaExistente.setCliente(cliente);
        }
        if (reservaUpdateDTO.getIdMesa() != null) {
            Mesa mesa = mesaRepository.findById(reservaUpdateDTO.getIdMesa())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Mesa no encontrada con el id: " + reservaUpdateDTO.getIdMesa()));
            // Validar disponibilidad de la mesa si cambia
            if (!mesa.equals(reservaExistente.getMesa())
                    || !reservaUpdateDTO.getFechaHoraReserva().equals(reservaExistente.getFechaHoraReserva())) {
                validarDisponibilidadMesa(mesa, reservaUpdateDTO.getFechaHoraReserva(), id);
            }
            reservaExistente.setMesa(mesa);
        }
        if (reservaUpdateDTO.getFechaHoraReserva() != null) {
            // Validar disponibilidad de la mesa si cambia la fecha/hora
            if (!reservaUpdateDTO.getFechaHoraReserva().equals(reservaExistente.getFechaHoraReserva())) {
                validarDisponibilidadMesa(reservaExistente.getMesa(), reservaUpdateDTO.getFechaHoraReserva(), id);
            }
            reservaExistente.setFechaHoraReserva(reservaUpdateDTO.getFechaHoraReserva());
        }
        if (reservaUpdateDTO.getNumeroPersonas() != null) {
            reservaExistente.setNumeroPersonas(reservaUpdateDTO.getNumeroPersonas());
        }
        if (reservaUpdateDTO.getObservaciones() != null) {
            reservaExistente.setObservaciones(reservaUpdateDTO.getObservaciones());
        }
        if (reservaUpdateDTO.getEstado() != null) {
            reservaExistente.setEstado(reservaUpdateDTO.getEstado());
        }

        Reserva reservaActualizada = reservaRepository.save(reservaExistente);
        return mapToResponseDTO(reservaActualizada);
    }

    @Override
    @Transactional
    public void eliminarReserva(Integer id) {
        if (!reservaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Reserva no encontrada con el id: " + id);
        }
        reservaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ReservaResponseDTO cambiarEstadoReserva(Integer id, EstadoReserva nuevoEstado) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con el id: " + id));
        reserva.setEstado(nuevoEstado);
        Reserva reservaActualizada = reservaRepository.save(reserva);
        return mapToResponseDTO(reservaActualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> buscarReservas(Integer idCliente, Integer idMesa,
            LocalDateTime fechaHoraReservaDesde, LocalDateTime fechaHoraReservaHasta, EstadoReserva estado,
            Integer numeroPersonas, String searchTerm, String logic) {
        Specification<Reserva> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (idCliente != null) {
                predicates.add(criteriaBuilder.equal(root.get("cliente").get("idCliente"), idCliente));
            }
            if (idMesa != null) {
                predicates.add(criteriaBuilder.equal(root.get("mesa").get("idMesa"), idMesa));
            }
            if (fechaHoraReservaDesde != null) {
                predicates
                        .add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaHoraReserva"), fechaHoraReservaDesde));
            }
            if (fechaHoraReservaHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaHoraReserva"), fechaHoraReservaHasta));
            }
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }
            if (numeroPersonas != null) {
                predicates.add(criteriaBuilder.equal(root.get("numeroPersonas"), numeroPersonas));
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate globalSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("observaciones")), lowerSearchTerm));
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

        return reservaRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private void validarDisponibilidadMesa(Mesa mesa, LocalDateTime fechaHoraReserva, Integer idReservaActual) {
        // Rango de tiempo donde la mesa no puede tener otra reserva comenzando.
        // Si una reserva existente comienza en este rango, se solapará con la nueva.
        LocalDateTime inicioConflicto = fechaHoraReserva.minusHours(RESERVATION_DURATION_HOURS);
        LocalDateTime finConflicto = fechaHoraReserva.plusHours(RESERVATION_DURATION_HOURS);

        List<Reserva> reservasConflictivas = reservaRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("mesa"), mesa));
            predicates.add(criteriaBuilder.notEqual(root.get("estado"), EstadoReserva.CANCELADA));

            // Excluir la reserva actual si estamos actualizando
            if (idReservaActual != null) {
                predicates.add(criteriaBuilder.notEqual(root.get("idReserva"), idReservaActual));
            }

            // Verificar si el inicio de una reserva existente cae dentro del rango de
            // conflicto de la nueva reserva.
            // Esto previene solapamientos.
            predicates.add(
                    criteriaBuilder.and(
                            criteriaBuilder.greaterThan(root.get("fechaHoraReserva"), inicioConflicto),
                            criteriaBuilder.lessThan(root.get("fechaHoraReserva"), finConflicto)));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        if (!reservasConflictivas.isEmpty()) {
            throw new ResourceConflictException("La mesa " + mesa.getNumeroMesa()
                    + " ya está reservada o ocupada para la fecha y hora seleccionadas.");
        }
    }

    private ReservaResponseDTO mapToResponseDTO(Reserva reserva) {
        ClienteResponseDTO clienteDTO = null;
        if (reserva.getCliente() != null) {
            clienteDTO = ClienteResponseDTO.builder()
                    .idCliente(reserva.getCliente().getIdCliente())
                    .nombre(reserva.getCliente().getNombre())
                    .apellido(reserva.getCliente().getApellido())
                    .build();
        }

        MesaResponseDTO mesaDTO = null;
        if (reserva.getMesa() != null) {
            mesaDTO = MesaResponseDTO.builder()
                    .idMesa(reserva.getMesa().getIdMesa())
                    .numeroMesa(reserva.getMesa().getNumeroMesa())
                    .build();
        }

        return ReservaResponseDTO.builder()
                .idReserva(reserva.getIdReserva())
                .cliente(clienteDTO)
                .mesa(mesaDTO)
                .fechaHoraReserva(reserva.getFechaHoraReserva())
                .numeroPersonas(reserva.getNumeroPersonas())
                .estado(reserva.getEstado())
                .observaciones(reserva.getObservaciones())
                .fechaCreacion(reserva.getFechaCreacion())
                .build();
    }
}
