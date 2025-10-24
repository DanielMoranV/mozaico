package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.security.annotations.RequirePermission;
import com.djasoft.mozaico.security.annotations.RequireCompanyContext;
import com.djasoft.mozaico.security.annotations.Auditable;
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
import com.djasoft.mozaico.application.services.ComprobanteService;
import com.djasoft.mozaico.domain.entities.Comprobante;
import com.djasoft.mozaico.web.dtos.MetodoPagoResponseDTO;
import com.djasoft.mozaico.web.dtos.PagoRequestDTO;
import com.djasoft.mozaico.web.dtos.PagoResponseDTO;
import com.djasoft.mozaico.web.dtos.PagoCompletoResponseDTO;
import com.djasoft.mozaico.web.dtos.ComprobanteResponseDTO;
import com.djasoft.mozaico.web.dtos.PagoUpdateDTO;
import com.djasoft.mozaico.web.dtos.PedidoResponseDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PagoServiceImpl implements PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final PedidoService pedidoService;
    private final ComprobanteService comprobanteService;

    @Override
    @Transactional
    @RequirePermission({"MANAGE_PAYMENTS", "ALL_PERMISSIONS"})
    @Auditable(action = "CREATE", entity = "Pago", description = "Crear nuevo pago")
    public PagoResponseDTO crearPago(PagoRequestDTO pagoRequestDTO) {
        Pedido pedido = pedidoRepository.findById(pagoRequestDTO.getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el id: " + pagoRequestDTO.getIdPedido()));

        MetodoPago metodoPago = metodoPagoRepository.findById(pagoRequestDTO.getIdMetodo())
                .orElseThrow(() -> new ResourceNotFoundException("Método de Pago no encontrado con el id: " + pagoRequestDTO.getIdMetodo()));

        // Validar que el pedido pertenezca a la empresa del usuario actual
        var currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (!pedido.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new ResourceNotFoundException("Pedido no encontrado en tu empresa");
        }

        Pago nuevoPago = Pago.builder()
                .pedido(pedido)
                .metodoPago(metodoPago)
                .monto(pagoRequestDTO.getMonto())
                .referencia(pagoRequestDTO.getReferencia())
                .estado(pagoRequestDTO.getEstado() != null ? pagoRequestDTO.getEstado() : EstadoPago.COMPLETADO)
                .usuarioCreacion(currentUser)
                .empresa(currentUser.getEmpresa())
                .build();

        Pago pagoGuardado = pagoRepository.save(nuevoPago);

        // Si el pago es completado, actualizar el estado del pedido y generar comprobante
        if (pagoGuardado.getEstado() == EstadoPago.COMPLETADO) {
            pedidoService.cambiarEstadoPedido(pedido.getIdPedido(), EstadoPedido.PAGADO);
            
            // Generar comprobante automáticamente
            try {
                comprobanteService.generarComprobanteAutomatico(pagoGuardado);
                log.info("Comprobante generado automáticamente para pago ID: {}", pagoGuardado.getIdPago());
            } catch (Exception e) {
                log.error("Error al generar comprobante para pago ID {}: {}", 
                         pagoGuardado.getIdPago(), e.getMessage());
                // No fallar el pago por error en comprobante
            }
        }

        return mapToResponseDTO(pagoGuardado);
    }

    @Override
    @Transactional
    public PagoCompletoResponseDTO crearPagoCompleto(PagoRequestDTO pagoRequestDTO) {
        // Usar el método existente para crear el pago
        PagoResponseDTO pagoResponse = crearPago(pagoRequestDTO);
        
        // Obtener el pago recién creado
        Pago pago = pagoRepository.findById(pagoResponse.getIdPago())
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        // Obtener el comprobante si se generó
        ComprobanteResponseDTO comprobanteResponse = null;
        if (pago.getEstado() == EstadoPago.COMPLETADO) {
            try {
                Comprobante comprobante = comprobanteService.obtenerComprobantePorPago(pago.getIdPago());
                comprobanteResponse = mapComprobanteToResponseDTO(comprobante);
            } catch (ResourceNotFoundException e) {
                log.warn("Comprobante no encontrado para pago ID: {}", pago.getIdPago());
            }
        }

        // Construir respuesta completa
        return PagoCompletoResponseDTO.builder()
                .idPago(pagoResponse.getIdPago())
                .pedido(pagoResponse.getPedido())
                .metodoPago(pagoResponse.getMetodoPago())
                .monto(pagoResponse.getMonto())
                .fechaPago(pagoResponse.getFechaPago())
                .referencia(pagoResponse.getReferencia())
                .estado(pagoResponse.getEstado())
                .comprobante(comprobanteResponse)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @RequirePermission({"MANAGE_PAYMENTS", "VIEW_ORDERS", "ALL_PERMISSIONS"})
    public List<PagoResponseDTO> obtenerTodosLosPagos() {
        // Solo mostrar pagos de la empresa del usuario actual
        var currentUser = JwtAuthenticationFilter.getCurrentUser();
        Long empresaId = currentUser.getEmpresa().getIdEmpresa();

        return pagoRepository.findAll().stream()
                .filter(pago -> pago.getEmpresa().getIdEmpresa().equals(empresaId))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @RequirePermission({"MANAGE_PAYMENTS", "VIEW_ORDERS", "ALL_PERMISSIONS"})
    @RequireCompanyContext
    public PagoResponseDTO obtenerPagoPorId(Integer id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con el id: " + id));

        // Validar que el pago pertenezca a la empresa del usuario actual
        var currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (!pago.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new ResourceNotFoundException("Pago no encontrado");
        }

        return mapToResponseDTO(pago);
    }

    @Override
    @Transactional
    @RequirePermission({"MANAGE_PAYMENTS", "ALL_PERMISSIONS"})
    @RequireCompanyContext
    @Auditable(action = "UPDATE", entity = "Pago", description = "Actualizar pago")
    public PagoResponseDTO actualizarPago(Integer id, PagoUpdateDTO pagoUpdateDTO) {
        Pago pagoExistente = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con el id: " + id));

        // Validar que el pago pertenezca a la empresa del usuario actual
        var currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (!pagoExistente.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new ResourceNotFoundException("Pago no encontrado");
        }

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
    @RequirePermission({"MANAGE_PAYMENTS", "ALL_PERMISSIONS"})
    @RequireCompanyContext
    @Auditable(action = "DELETE", entity = "Pago", description = "Eliminar pago")
    public void eliminarPago(Integer id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado con el id: " + id));

        // Validar que el pago pertenezca a la empresa del usuario actual
        var currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (!pago.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new ResourceNotFoundException("Pago no encontrado");
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
                return criteriaBuilder.conjunction(); // Equivalente a WHERE 1=1
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

    private ComprobanteResponseDTO mapComprobanteToResponseDTO(Comprobante comprobante) {
        if (comprobante == null) return null;

        return ComprobanteResponseDTO.builder()
                .idComprobante(comprobante.getIdComprobante())
                .tipoComprobante(comprobante.getTipoComprobante())
                .numeroComprobante(comprobante.getNumeroComprobante())
                .serieComprobante(comprobante.getSerieComprobante())
                .fechaEmision(comprobante.getFechaEmision())
                .estado(comprobante.getEstado())
                .hashVerificacion(comprobante.getHashVerificacion())
                .observaciones(comprobante.getObservaciones())
                .archivoTicketDisponible(comprobante.getRutaArchivoTicket() != null)
                .archivoPdfDisponible(comprobante.getRutaArchivoPdf() != null)
                .urlDescargaTicket("/api/v1/comprobantes/" + comprobante.getIdComprobante() + "/ticket")
                .urlDescargaPdf("/api/v1/comprobantes/" + comprobante.getIdComprobante() + "/pdf")
                .urlVisualizacion("/api/v1/comprobantes/pago/" + comprobante.getPago().getIdPago())
                .build();
    }
}
