package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Cliente;
import com.djasoft.mozaico.domain.entities.DetallePedido;
import com.djasoft.mozaico.domain.entities.Mesa;
import com.djasoft.mozaico.domain.entities.Pedido;
import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import com.djasoft.mozaico.domain.enums.pedido.EstadoPedido;
import com.djasoft.mozaico.domain.enums.pedido.TipoServicio;
import com.djasoft.mozaico.domain.repositories.ClienteRepository;
import com.djasoft.mozaico.domain.repositories.DetallePedidoRepository;
import com.djasoft.mozaico.domain.repositories.MesaRepository;
import com.djasoft.mozaico.domain.repositories.PedidoRepository;
import com.djasoft.mozaico.domain.repositories.UsuarioRepository;
import com.djasoft.mozaico.services.MesaService;
import com.djasoft.mozaico.services.PedidoService;
import com.djasoft.mozaico.web.dtos.ClienteResponseDTO;
import com.djasoft.mozaico.web.dtos.MesaResponseDTO;
import com.djasoft.mozaico.web.dtos.PedidoRequestDTO;
import com.djasoft.mozaico.web.dtos.PedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.PedidoUpdateDTO;
import com.djasoft.mozaico.web.dtos.UsuarioResponseDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final MesaRepository mesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final MesaService mesaService;

    // Tasa de impuestos y descuento (pueden ser configurables)
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10%
    private static final BigDecimal DISCOUNT_RATE = BigDecimal.ZERO; // 0% por ahora

    @Override
    @Transactional
    public PedidoResponseDTO crearPedido(PedidoRequestDTO pedidoRequestDTO) {
        Cliente cliente = clienteRepository.findById(pedidoRequestDTO.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con el id: " + pedidoRequestDTO.getIdCliente()));

        Mesa mesa = mesaRepository.findById(pedidoRequestDTO.getIdMesa())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mesa no encontrada con el id: " + pedidoRequestDTO.getIdMesa()));

        Usuario empleado = usuarioRepository.findById(pedidoRequestDTO.getIdEmpleado())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empleado (Usuario) no encontrado con el id: " + pedidoRequestDTO.getIdEmpleado()));

        Pedido nuevoPedido = Pedido.builder()
                .cliente(cliente)
                .mesa(mesa)
                .empleado(empleado)
                .estado(pedidoRequestDTO.getEstado() != null ? pedidoRequestDTO.getEstado() : EstadoPedido.PENDIENTE)
                .tipoServicio(pedidoRequestDTO.getTipoServicio() != null ? pedidoRequestDTO.getTipoServicio()
                        : TipoServicio.MESA)
                .observaciones(pedidoRequestDTO.getObservaciones())
                .direccionDelivery(pedidoRequestDTO.getDireccionDelivery())
                .subtotal(BigDecimal.ZERO) // Inicializar en cero
                .impuestos(BigDecimal.ZERO)
                .descuento(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();

        Pedido pedidoGuardado = pedidoRepository.save(nuevoPedido);

        // Actualizar estado de la mesa si el pedido es para mesa
        if (pedidoGuardado.getTipoServicio() == TipoServicio.MESA) {
            mesaService.cambiarEstadoMesa(pedidoGuardado.getMesa().getIdMesa(), EstadoMesa.OCUPADA);
        }

        return mapToResponseDTO(pedidoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PedidoResponseDTO obtenerPedidoPorId(Integer id) {
        return pedidoRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el id: " + id));
    }

    @Override
    @Transactional
    public PedidoResponseDTO actualizarPedido(Integer id, PedidoUpdateDTO pedidoUpdateDTO) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el id: " + id));

        if (pedidoUpdateDTO.getIdCliente() != null) {
            Cliente cliente = clienteRepository.findById(pedidoUpdateDTO.getIdCliente())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Cliente no encontrado con el id: " + pedidoUpdateDTO.getIdCliente()));
            pedidoExistente.setCliente(cliente);
        }
        if (pedidoUpdateDTO.getIdMesa() != null) {
            Mesa mesa = mesaRepository.findById(pedidoUpdateDTO.getIdMesa())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Mesa no encontrada con el id: " + pedidoUpdateDTO.getIdMesa()));
            pedidoExistente.setMesa(mesa);
        }
        if (pedidoUpdateDTO.getIdEmpleado() != null) {
            Usuario empleado = usuarioRepository.findById(pedidoUpdateDTO.getIdEmpleado())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Empleado (Usuario) no encontrado con el id: " + pedidoUpdateDTO.getIdEmpleado()));
            pedidoExistente.setEmpleado(empleado);
        }
        if (pedidoUpdateDTO.getEstado() != null) {
            pedidoExistente.setEstado(pedidoUpdateDTO.getEstado());
        }
        if (pedidoUpdateDTO.getTipoServicio() != null) {
            pedidoExistente.setTipoServicio(pedidoUpdateDTO.getTipoServicio());
        }
        if (pedidoUpdateDTO.getObservaciones() != null) {
            pedidoExistente.setObservaciones(pedidoUpdateDTO.getObservaciones());
        }
        if (pedidoUpdateDTO.getDireccionDelivery() != null) {
            pedidoExistente.setDireccionDelivery(pedidoUpdateDTO.getDireccionDelivery());
        }
        // Los campos subtotal, impuestos, descuento y total se recalculan, no se
        // actualizan directamente

        Pedido pedidoActualizado = pedidoRepository.save(pedidoExistente);
        return mapToResponseDTO(pedidoActualizado);
    }

    @Override
    @Transactional
    public void eliminarPedido(Integer id) {
        if (!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido no encontrado con el id: " + id);
        }
        pedidoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public PedidoResponseDTO cambiarEstadoPedido(Integer id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el id: " + id));
        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        // Actualizar estado de la mesa si el pedido es para mesa y cambia a entregado o
        // cancelado
        if (pedidoActualizado.getTipoServicio() == TipoServicio.MESA &&
                (nuevoEstado == EstadoPedido.ENTREGADO || nuevoEstado == EstadoPedido.CANCELADO)) {
            mesaService.cambiarEstadoMesa(pedidoActualizado.getMesa().getIdMesa(), EstadoMesa.DISPONIBLE);
        }

        return mapToResponseDTO(pedidoActualizado);
    }

    @Override
    @Transactional
    public PedidoResponseDTO recalcularTotalesPedido(Integer idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el id: " + idPedido));

        // Obtener todos los detalles de pedido para este pedido
        List<DetallePedido> detalles = detallePedidoRepository.findAll(
                (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("pedido").get("idPedido"), idPedido));

        BigDecimal subtotalCalculado = detalles.stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal impuestosCalculados = subtotalCalculado.multiply(TAX_RATE);
        BigDecimal descuentoCalculado = subtotalCalculado.multiply(DISCOUNT_RATE); // Por ahora 0
        BigDecimal totalCalculado = subtotalCalculado.add(impuestosCalculados).subtract(descuentoCalculado);

        pedido.setSubtotal(subtotalCalculado);
        pedido.setImpuestos(impuestosCalculados);
        pedido.setDescuento(descuentoCalculado);
        pedido.setTotal(totalCalculado);

        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        return mapToResponseDTO(pedidoActualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> buscarPedidos(Integer idCliente, Integer idMesa, Long idEmpleado,
            LocalDateTime fechaPedidoDesde, LocalDateTime fechaPedidoHasta, EstadoPedido estado,
            TipoServicio tipoServicio, String searchTerm, String logic) {
        Specification<Pedido> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (idCliente != null) {
                predicates.add(criteriaBuilder.equal(root.get("cliente").get("idCliente"), idCliente));
            }
            if (idMesa != null) {
                predicates.add(criteriaBuilder.equal(root.get("mesa").get("idMesa"), idMesa));
            }
            if (idEmpleado != null) {
                predicates.add(criteriaBuilder.equal(root.get("empleado").get("idUsuario"), idEmpleado));
            }
            if (fechaPedidoDesde != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fechaPedido"), fechaPedidoDesde));
            }
            if (fechaPedidoHasta != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("fechaPedido"), fechaPedidoHasta));
            }
            if (estado != null) {
                predicates.add(criteriaBuilder.equal(root.get("estado"), estado));
            }
            if (tipoServicio != null) {
                predicates.add(criteriaBuilder.equal(root.get("tipoServicio"), tipoServicio));
            }

            if (searchTerm != null && !searchTerm.isEmpty()) {
                String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
                Predicate globalSearch = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("observaciones")), lowerSearchTerm),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("direccionDelivery")), lowerSearchTerm));
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

        return pedidoRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private PedidoResponseDTO mapToResponseDTO(Pedido pedido) {
        ClienteResponseDTO clienteDTO = null;
        if (pedido.getCliente() != null) {
            clienteDTO = ClienteResponseDTO.builder()
                    .idCliente(pedido.getCliente().getIdCliente())
                    .nombre(pedido.getCliente().getNombre())
                    .apellido(pedido.getCliente().getApellido())
                    .build();
        }

        MesaResponseDTO mesaDTO = null;
        if (pedido.getMesa() != null) {
            mesaDTO = MesaResponseDTO.builder()
                    .idMesa(pedido.getMesa().getIdMesa())
                    .numeroMesa(pedido.getMesa().getNumeroMesa())
                    .build();
        }

        UsuarioResponseDTO empleadoDTO = null;
        if (pedido.getEmpleado() != null) {
            empleadoDTO = UsuarioResponseDTO.builder()
                    .idUsuario(pedido.getEmpleado().getIdUsuario())
                    .nombre(pedido.getEmpleado().getNombre())
                    .username(pedido.getEmpleado().getUsername())
                    .build();
        }

        return PedidoResponseDTO.builder()
                .idPedido(pedido.getIdPedido())
                .cliente(clienteDTO)
                .mesa(mesaDTO)
                .empleado(empleadoDTO)
                .fechaPedido(pedido.getFechaPedido())
                .estado(pedido.getEstado())
                .tipoServicio(pedido.getTipoServicio())
                .subtotal(pedido.getSubtotal())
                .impuestos(pedido.getImpuestos())
                .descuento(pedido.getDescuento())
                .total(pedido.getTotal())
                .observaciones(pedido.getObservaciones())
                .direccionDelivery(pedido.getDireccionDelivery())
                .build();
    }
}