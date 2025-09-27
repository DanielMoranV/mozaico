package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.MetodoPago;
import com.djasoft.mozaico.domain.entities.Pago;
import com.djasoft.mozaico.domain.entities.Pedido;
import com.djasoft.mozaico.domain.enums.pedido.EstadoPedido;
import com.djasoft.mozaico.domain.enums.pago.EstadoPago;
import com.djasoft.mozaico.domain.repositories.MetodoPagoRepository;
import com.djasoft.mozaico.domain.repositories.PagoRepository;
import com.djasoft.mozaico.domain.repositories.PedidoRepository;
import com.djasoft.mozaico.services.PagoService;
import com.djasoft.mozaico.services.PedidoService;
import com.djasoft.mozaico.web.dtos.MetodoPagoResponseDTO;
import com.djasoft.mozaico.web.dtos.PagoRequestDTO;
import com.djasoft.mozaico.web.dtos.PagoResponseDTO;
import com.djasoft.mozaico.web.dtos.PagoUpdateDTO;
import com.djasoft.mozaico.web.dtos.PedidoResponseDTO;
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
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final PedidoService pedidoService;

    @Override
    @Transactional
    public PagoResponseDTO crearPago(PagoRequestDTO pagoRequestDTO) {
        Pedido pedido = pedidoRepository.findById(pagoRequestDTO.getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el id: " + pagoRequestDTO.getIdPedido()));

        MetodoPago metodoPago = metodoPagoRepository.findById(pagoRequestDTO.getIdMetodo())
                .orElseThrow(() -> new ResourceNotFoundException("Método de Pago no encontrado con el id: " + pagoRequestDTO.getIdMetodo()));

        Pago nuevoPago = Pago.builder()
                .pedido(pedido)
                .metodoPago(metodoPago)
                .monto(pagoRequestDTO.getMonto())
                .referencia(pagoRequestDTO.getReferencia())
                .estado(pagoRequestDTO.getEstado() != null ? pagoRequestDTO.getEstado() : EstadoPago.COMPLETADO)
                .build();

        Pago pagoGuardado = pagoRepository.save(nuevoPago);

        // Si el pago es completado, actualizar el estado del pedido
        if (pagoGuardado.getEstado() == EstadoPago.COMPLETADO) {
            pedidoService.cambiarEstadoPedido(pedido.getIdPedido(), EstadoPedido.PAGADO);
        }

        return mapToResponseDTO(pagoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> obtenerTodosLosPagos() {
        return pagoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PagoResponseDTO obtenerPagoPorId(Integer id) {
        return pagoRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con el id: " + id));
    }

    @Override
    @Transactional
    public PagoResponseDTO actualizarPago(Integer id, PagoUpdateDTO pagoUpdateDTO) {
        Pago pagoExistente = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con el id: " + id));

        if (pagoUpdateDTO.getIdPedido() != null) {
            Pedido pedido = pedidoRepository.findById(pagoUpdateDTO.getIdPedido())
                    .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el id: " + pagoUpdateDTO.getIdPedido()));
            pagoExistente.setPedido(pedido);
        }
        if (pagoUpdateDTO.getIdMetodo() != null) {
            MetodoPago metodoPago = metodoPagoRepository.findById(pagoUpdateDTO.getIdMetodo())
                    .orElseThrow(() -> new ResourceNotFoundException("Método de Pago no encontrado con el id: " + pagoUpdateDTO.getIdMetodo()));
            pagoExistente.setMetodoPago(metodoPago);
        }
        if (pagoUpdateDTO.getMonto() != null) {
            pagoExistente.setMonto(pagoUpdateDTO.getMonto());
        }
        if (pagoUpdateDTO.getReferencia() != null) {
            pagoExistente.setReferencia(pagoUpdateDTO.getReferencia());
        }
        if (pagoUpdateDTO.getEstado() != null) {
            pagoExistente.setEstado(pagoUpdateDTO.getEstado());
        }

        Pago pagoActualizado = pagoRepository.save(pagoExistente);

        // Si el pago es completado, actualizar el estado del pedido
        if (pagoActualizado.getEstado() == EstadoPago.COMPLETADO) {
            pedidoService.cambiarEstadoPedido(pagoActualizado.getPedido().getIdPedido(), EstadoPedido.PAGADO);
        }

        return mapToResponseDTO(pagoActualizado);
    }

    @Override
    @Transactional
    public void eliminarPago(Integer id) {
        if (!pagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pago no encontrado con el id: " + id);
        }
        // Antes de eliminar el pago, podríamos querer revertir el estado del pedido si estaba ENTREGADO
        // Por simplicidad, no se implementa aquí, pero sería una consideración.
        pagoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PagoResponseDTO cambiarEstadoPago(Integer id, EstadoPago nuevoEstado) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con el id: " + id));
        pago.setEstado(nuevoEstado);
        Pago pagoActualizado = pagoRepository.save(pago);

        // Si el pago es completado, actualizar el estado del pedido
        if (pagoActualizado.getEstado() == EstadoPago.COMPLETADO) {
            pedidoService.cambiarEstadoPedido(pagoActualizado.getPedido().getIdPedido(), EstadoPedido.PAGADO);
        }

        return mapToResponseDTO(pagoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponseDTO> buscarPagos(Integer idPedido, Integer idMetodo, LocalDateTime fechaPagoDesde, LocalDateTime fechaPagoHasta, EstadoPago estado, String searchTerm, String logic) {
        Specification<Pago> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (idPedido != null) {
                predicates.add(criteriaBuilder.equal(root.get("pedido").get("idPedido"), idPedido));
            }
            if (idMetodo != null) {
                predicates.add(criteriaBuilder.equal(root.get("metodoPago").get("idMetodo"), idMetodo));
            }
            if (fechaPagoDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaPago"), fechaPagoDesde));
            }
            if (fechaPagoHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaPago"), fechaPagoHasta));
            }
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate globalSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("referencia")), lowerSearchTerm)
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

        return pagoRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private PagoResponseDTO mapToResponseDTO(Pago pago) {
        PedidoResponseDTO pedidoDTO = null;
        if (pago.getPedido() != null) {
            pedidoDTO = PedidoResponseDTO.builder()
                    .idPedido(pago.getPedido().getIdPedido())
                    .fechaPedido(pago.getPedido().getFechaPedido())
                    .build();
        }

        MetodoPagoResponseDTO metodoPagoDTO = null;
        if (pago.getMetodoPago() != null) {
            metodoPagoDTO = MetodoPagoResponseDTO.builder()
                    .idMetodo(pago.getMetodoPago().getIdMetodo())
                    .nombre(pago.getMetodoPago().getNombre())
                    .build();
        }

        return PagoResponseDTO.builder()
                .idPago(pago.getIdPago())
                .pedido(pedidoDTO)
                .metodoPago(metodoPagoDTO)
                .monto(pago.getMonto())
                .fechaPago(pago.getFechaPago())
                .referencia(pago.getReferencia())
                .estado(pago.getEstado())
                .build();
    }
}
