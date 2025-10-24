package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Mesa;
import com.djasoft.mozaico.domain.entities.Pedido;
import com.djasoft.mozaico.domain.entities.Reserva;
import com.djasoft.mozaico.domain.entities.DetallePedido;
import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import com.djasoft.mozaico.domain.repositories.MesaRepository;
import com.djasoft.mozaico.domain.repositories.PedidoRepository;
import com.djasoft.mozaico.domain.repositories.ReservaRepository;
import com.djasoft.mozaico.domain.repositories.DetallePedidoRepository;
import com.djasoft.mozaico.services.MesaService;
import com.djasoft.mozaico.web.dtos.MesaRequestDTO;
import com.djasoft.mozaico.web.dtos.MesaResponseDTO;
import com.djasoft.mozaico.web.dtos.MesaUpdateDTO;
import com.djasoft.mozaico.web.dtos.MesaEstadoDetalladoResponseDTO;
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
    private final PedidoRepository pedidoRepository;
    private final ReservaRepository reservaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

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

            // Si no hay predicates, retornar criterio que sea siempre verdadero (traer todos)
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction(); // Equivalente a WHERE 1=1
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

    @Override
    @Transactional(readOnly = true)
    public List<MesaEstadoDetalladoResponseDTO> obtenerMesasConEstadoDetallado() {
        List<Mesa> mesas = mesaRepository.findAllByOrderByNumeroMesaAsc();

        return mesas.stream()
                .map(this::mapToMesaEstadoDetalladoResponseDTO)
                .collect(Collectors.toList());
    }

    private MesaEstadoDetalladoResponseDTO mapToMesaEstadoDetalladoResponseDTO(Mesa mesa) {
        MesaEstadoDetalladoResponseDTO.MesaEstadoDetalladoResponseDTOBuilder builder =
                MesaEstadoDetalladoResponseDTO.builder()
                .idMesa(mesa.getIdMesa())
                .numeroMesa(mesa.getNumeroMesa())
                .capacidad(mesa.getCapacidad())
                .ubicacion(mesa.getUbicacion())
                .estado(mesa.getEstado())
                .observaciones(mesa.getObservaciones())
                .fechaCreacion(mesa.getFechaCreacion());

        // Si la mesa está ocupada, buscar el último pedido
        if (mesa.getEstado() == EstadoMesa.OCUPADA) {
            Pedido ultimoPedido = pedidoRepository.findFirstByMesaOrderByFechaPedidoDesc(mesa);
            if (ultimoPedido != null) {
                builder.ultimoPedido(mapToPedidoBasicoResponseDTO(ultimoPedido));
            }
        }

        // Si la mesa está reservada, buscar la última reserva
        if (mesa.getEstado() == EstadoMesa.RESERVADA) {
            Reserva ultimaReserva = reservaRepository.findFirstByMesaOrderByFechaHoraReservaDesc(mesa);
            if (ultimaReserva != null) {
                builder.ultimaReserva(mapToReservaBasicaResponseDTO(ultimaReserva));
            }
        }

        return builder.build();
    }

    private MesaEstadoDetalladoResponseDTO.PedidoBasicoResponseDTO mapToPedidoBasicoResponseDTO(Pedido pedido) {
        // Obtener detalles del pedido
        List<DetallePedido> detalles = detallePedidoRepository.findByPedido(pedido);
        List<MesaEstadoDetalladoResponseDTO.DetallePedidoBasicoResponseDTO> detallesDTO =
                detalles.stream()
                    .map(this::mapToDetallePedidoBasicoResponseDTO)
                    .collect(Collectors.toList());

        // Calcular el total dinámicamente si es null o 0
        Double totalCalculado = pedido.getTotal() != null ? pedido.getTotal().doubleValue() : 0.0;
        if (totalCalculado == 0.0 && !detalles.isEmpty()) {
            totalCalculado = detalles.stream()
                    .filter(detalle -> detalle.getSubtotal() != null)
                    .mapToDouble(detalle -> detalle.getSubtotal().doubleValue())
                    .sum();
        }

        return MesaEstadoDetalladoResponseDTO.PedidoBasicoResponseDTO.builder()
                .idPedido(pedido.getIdPedido())
                .fechaPedido(pedido.getFechaPedido())
                .estado(pedido.getEstado().toString())
                .tipoServicio(pedido.getTipoServicio().toString())
                .cliente(pedido.getCliente() != null ?
                    pedido.getCliente().getNombre() + " " + pedido.getCliente().getApellido() :
                    "Cliente anónimo")
                .empleado(pedido.getEmpleado() != null ? pedido.getEmpleado().getNombre() : "Sin asignar")
                .total(totalCalculado)
                .detalles(detallesDTO)
                .build();
    }

    private MesaEstadoDetalladoResponseDTO.DetallePedidoBasicoResponseDTO mapToDetallePedidoBasicoResponseDTO(DetallePedido detalle) {
        return MesaEstadoDetalladoResponseDTO.DetallePedidoBasicoResponseDTO.builder()
                .idDetalle(detalle.getIdDetalle())
                .producto(detalle.getProducto() != null ? detalle.getProducto().getNombre() : "Producto sin nombre")
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario().doubleValue() : 0.0)
                .estado(detalle.getEstado() != null ? detalle.getEstado().toString() : "PENDIENTE")
                .build();
    }

    private MesaEstadoDetalladoResponseDTO.ReservaBasicaResponseDTO mapToReservaBasicaResponseDTO(Reserva reserva) {
        return MesaEstadoDetalladoResponseDTO.ReservaBasicaResponseDTO.builder()
                .idReserva(reserva.getIdReserva())
                .fechaHoraReserva(reserva.getFechaHoraReserva())
                .numeroPersonas(reserva.getNumeroPersonas())
                .estado(reserva.getEstado().toString())
                .cliente(reserva.getCliente() != null ?
                    reserva.getCliente().getNombre() + " " + reserva.getCliente().getApellido() :
                    "Cliente sin nombre")
                .observaciones(reserva.getObservaciones())
                .fechaCreacion(reserva.getFechaCreacion())
                .build();
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
