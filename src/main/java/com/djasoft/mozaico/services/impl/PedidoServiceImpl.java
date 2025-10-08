package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.security.annotations.RequirePermission;
import com.djasoft.mozaico.security.annotations.RequireCompanyContext;
import com.djasoft.mozaico.security.annotations.Auditable;
import com.djasoft.mozaico.domain.entities.Cliente;
import com.djasoft.mozaico.domain.entities.DetallePedido;
import com.djasoft.mozaico.domain.entities.Mesa;
import com.djasoft.mozaico.domain.entities.Pedido;
import com.djasoft.mozaico.domain.entities.Producto;
import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import com.djasoft.mozaico.domain.enums.pedido.EstadoPedido;
import com.djasoft.mozaico.domain.enums.pedido.TipoServicio;
import com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido;
import com.djasoft.mozaico.domain.repositories.ClienteRepository;
import com.djasoft.mozaico.domain.repositories.DetallePedidoRepository;
import com.djasoft.mozaico.domain.repositories.MesaRepository;
import com.djasoft.mozaico.domain.repositories.PedidoRepository;
import com.djasoft.mozaico.domain.repositories.ProductoRepository;
import com.djasoft.mozaico.domain.repositories.UsuarioRepository;
import com.djasoft.mozaico.services.MesaService;
import com.djasoft.mozaico.services.PedidoService;
import com.djasoft.mozaico.web.dtos.ClienteResponseDTO;
import com.djasoft.mozaico.web.dtos.MesaResponseDTO;
import com.djasoft.mozaico.web.dtos.PedidoRequestDTO;
import com.djasoft.mozaico.web.dtos.PedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.PedidoUpdateDTO;
import com.djasoft.mozaico.web.dtos.PedidoCompletoRequestDTO;
import com.djasoft.mozaico.web.dtos.UsuarioResponseDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.ProductoResponseDTO;
import com.djasoft.mozaico.web.dtos.AgregarProductoRequestDTO;
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
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final MesaService mesaService;
    private final com.djasoft.mozaico.services.InventarioService inventarioService;

    // Tasa de impuestos y descuento (pueden ser configurables)
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10%
    private static final BigDecimal DISCOUNT_RATE = BigDecimal.ZERO; // 0% por ahora

    @Override
    @Transactional
    @RequirePermission({"MANAGE_ORDERS", "ALL_PERMISSIONS"})
    @Auditable(action = "CREATE", entity = "Pedido", description = "Crear nuevo pedido")
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

        // Obtener usuario actual para trazabilidad
        var currentUser = JwtAuthenticationFilter.getCurrentUser();

        // Validar que el empleado asignado pertenezca a la misma empresa
        if (!empleado.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new ResourceNotFoundException("Empleado no encontrado en tu empresa");
        }

        Pedido nuevoPedido = Pedido.builder()
                .cliente(cliente)
                .mesa(mesa)
                .empleado(empleado)
                .usuarioCreacion(currentUser) // Trazabilidad: quién creó el pedido
                .empresa(currentUser.getEmpresa()) // Contexto de empresa
                .estado(pedidoRequestDTO.getEstado() != null ? pedidoRequestDTO.getEstado() : EstadoPedido.ABIERTO)
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
    @RequirePermission({"MANAGE_ORDERS", "VIEW_ORDERS", "ALL_PERMISSIONS"})
    public List<PedidoResponseDTO> obtenerTodosLosPedidos() {
        // Solo mostrar pedidos de la empresa del usuario actual
        var currentUser = JwtAuthenticationFilter.getCurrentUser();
        Long empresaId = currentUser.getEmpresa().getIdEmpresa();

        return pedidoRepository.findAll().stream()
                .filter(pedido -> pedido.getEmpresa().getIdEmpresa().equals(empresaId))
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @RequirePermission({"MANAGE_ORDERS", "VIEW_ORDERS", "ALL_PERMISSIONS"})
    @RequireCompanyContext
    public PedidoResponseDTO obtenerPedidoPorId(Integer id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el id: " + id));

        // Validar que el pedido pertenezca a la empresa del usuario actual
        var currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (!pedido.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new ResourceNotFoundException("Pedido no encontrado");
        }

        return mapToResponseDTO(pedido);
    }

    @Override
    @Transactional
    @RequirePermission({"MANAGE_ORDERS", "ALL_PERMISSIONS"})
    @RequireCompanyContext
    @Auditable(action = "UPDATE", entity = "Pedido", description = "Actualizar pedido")
    public PedidoResponseDTO actualizarPedido(Integer id, PedidoUpdateDTO pedidoUpdateDTO) {
        Pedido pedidoExistente = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el id: " + id));

        // Validar que el pedido pertenezca a la empresa del usuario actual
        var currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (!pedidoExistente.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new ResourceNotFoundException("Pedido no encontrado");
        }

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

        // Actualizar estado de la mesa si el pedido es para mesa y cambia a pagado o cancelado
        if (pedidoActualizado.getTipoServicio() == TipoServicio.MESA &&
                (nuevoEstado == EstadoPedido.PAGADO || nuevoEstado == EstadoPedido.CANCELADO)) {
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

        UsuarioResponseDTO usuarioCreacionDTO = null;
        if (pedido.getUsuarioCreacion() != null) {
            usuarioCreacionDTO = UsuarioResponseDTO.builder()
                    .idUsuario(pedido.getUsuarioCreacion().getIdUsuario())
                    .nombre(pedido.getUsuarioCreacion().getNombre())
                    .username(pedido.getUsuarioCreacion().getUsername())
                    .tipoUsuario(pedido.getUsuarioCreacion().getTipoUsuario())
                    .build();
        }

        List<DetallePedidoResponseDTO> detallesDTO = detallePedidoRepository.findByPedido(pedido).stream()
                .map(this::mapDetallePedidoToResponseDTO)
                .collect(Collectors.toList());

        return PedidoResponseDTO.builder()
                .idPedido(pedido.getIdPedido())
                .cliente(clienteDTO)
                .mesa(mesaDTO)
                .empleado(empleadoDTO)
                .usuarioCreacion(usuarioCreacionDTO) // Usuario que creó el pedido
                .fechaPedido(pedido.getFechaPedido())
                .estado(pedido.getEstado())
                .tipoServicio(pedido.getTipoServicio())
                .subtotal(pedido.getSubtotal())
                .impuestos(pedido.getImpuestos())
                .descuento(pedido.getDescuento())
                .total(pedido.getTotal())
                .observaciones(pedido.getObservaciones())
                .direccionDelivery(pedido.getDireccionDelivery())
                .detalles(detallesDTO)
                .build();
    }

    private DetallePedidoResponseDTO mapDetallePedidoToResponseDTO(DetallePedido detalle) {
        ProductoResponseDTO productoDTO = ProductoResponseDTO.builder()
                .idProducto(detalle.getProducto().getIdProducto())
                .nombre(detalle.getProducto().getNombre())
                .precio(detalle.getProducto().getPrecio())
                .build();

        return DetallePedidoResponseDTO.builder()
                .idDetalle(detalle.getIdDetalle())
                .pedido(null)
                .producto(productoDTO)
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .observaciones(detalle.getObservaciones())
                .estado(detalle.getEstado())
                .build();
    }

    @Override
    @Transactional
    public PedidoResponseDTO crearPedidoCompleto(PedidoCompletoRequestDTO requestDTO) {
        // Validaciones iniciales
        validarPedidoCompleto(requestDTO);

        // Obtener entidades relacionadas
        Cliente cliente = null;
        if (requestDTO.getIdCliente() != null) {
            cliente = clienteRepository.findById(requestDTO.getIdCliente())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con el ID: " + requestDTO.getIdCliente()));
        }

        Mesa mesa = null;
        if (requestDTO.getIdMesa() != null) {
            mesa = mesaRepository.findById(requestDTO.getIdMesa())
                    .orElseThrow(() -> new ResourceNotFoundException("Mesa no encontrada con el ID: " + requestDTO.getIdMesa()));

            // Verificar que la mesa esté disponible
            if (mesa.getEstado() != EstadoMesa.DISPONIBLE) {
                throw new IllegalArgumentException("La mesa " + mesa.getNumeroMesa() + " no está disponible");
            }
        }

        Usuario empleado = usuarioRepository.findById(requestDTO.getIdEmpleado())
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado con el ID: " + requestDTO.getIdEmpleado()));

        // Obtener usuario actual para trazabilidad
        var currentUser = JwtAuthenticationFilter.getCurrentUser();

        // Validar que el empleado asignado pertenezca a la misma empresa
        if (!empleado.getEmpresa().getIdEmpresa().equals(currentUser.getEmpresa().getIdEmpresa())) {
            throw new ResourceNotFoundException("Empleado no encontrado en tu empresa");
        }

        // Crear el pedido principal
        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .mesa(mesa)
                .empleado(empleado)
                .usuarioCreacion(currentUser) // Trazabilidad: quién creó el pedido
                .empresa(currentUser.getEmpresa()) // Contexto de empresa
                .estado(EstadoPedido.ABIERTO)
                .tipoServicio(requestDTO.getTipoServicio())
                .observaciones(requestDTO.getObservaciones())
                .direccionDelivery(requestDTO.getDireccionDelivery())
                .subtotal(BigDecimal.ZERO)
                .impuestos(BigDecimal.ZERO)
                .descuento(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();

        // Guardar el pedido para obtener el ID
        pedido = pedidoRepository.save(pedido);

        // Crear los detalles del pedido
        BigDecimal subtotalCalculado = BigDecimal.ZERO;
        for (PedidoCompletoRequestDTO.DetallePedidoCompletoRequestDTO detalleDTO : requestDTO.getDetalles()) {
            Producto producto = productoRepository.findById(detalleDTO.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + detalleDTO.getIdProducto()));

            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal subtotalDetalle = precioUnitario.multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(detalleDTO.getCantidad())
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotalDetalle)
                    .observaciones(detalleDTO.getObservaciones())
                    .estado(EstadoDetallePedido.PEDIDO)
                    .build();

            detallePedidoRepository.save(detalle);

            // Descontar del inventario
            inventarioService.actualizarStockPorVenta(producto.getIdProducto(), detalleDTO.getCantidad());

            subtotalCalculado = subtotalCalculado.add(subtotalDetalle);
        }

        // Calcular totales
        BigDecimal impuestos = subtotalCalculado.multiply(TAX_RATE);
        BigDecimal descuento = subtotalCalculado.multiply(DISCOUNT_RATE);
        BigDecimal total = subtotalCalculado.add(impuestos).subtract(descuento);

        // Actualizar totales del pedido
        pedido.setSubtotal(subtotalCalculado);
        pedido.setImpuestos(impuestos);
        pedido.setDescuento(descuento);
        pedido.setTotal(total);

        // Guardar el pedido actualizado
        pedido = pedidoRepository.save(pedido);

        // Actualizar estado de la mesa si es necesario
        if (mesa != null && requestDTO.getTipoServicio() == TipoServicio.MESA) {
            mesaService.cambiarEstadoMesa(mesa.getIdMesa(), EstadoMesa.OCUPADA);
        }

        return mapToResponseDTO(pedido);
    }

    private void validarPedidoCompleto(PedidoCompletoRequestDTO requestDTO) {
        // Validar que para delivery tenga dirección
        if (requestDTO.getTipoServicio() == TipoServicio.DELIVERY) {
            if (requestDTO.getDireccionDelivery() == null || requestDTO.getDireccionDelivery().trim().isEmpty()) {
                throw new IllegalArgumentException("La dirección de delivery es requerida para pedidos de delivery");
            }
        }

        // Validar que para mesa tenga mesa asignada
        if (requestDTO.getTipoServicio() == TipoServicio.MESA) {
            if (requestDTO.getIdMesa() == null) {
                throw new IllegalArgumentException("El ID de mesa es requerido para pedidos en mesa");
            }
        }

        // Validar que la lista de detalles no esté vacía
        if (requestDTO.getDetalles() == null || requestDTO.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El pedido debe tener al menos un producto");
        }

        // Validar cantidades positivas
        for (PedidoCompletoRequestDTO.DetallePedidoCompletoRequestDTO detalle : requestDTO.getDetalles()) {
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
            }
        }
    }

    @Override
    @Transactional
    public DetallePedidoResponseDTO agregarProductoAPedido(Integer idPedido, AgregarProductoRequestDTO requestDTO) {
        // Verificar que el pedido existe y está abierto
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con el ID: " + idPedido));

        if (pedido.getEstado() != EstadoPedido.ABIERTO) {
            throw new IllegalStateException("Solo se pueden agregar productos a pedidos abiertos");
        }

        // Verificar que el producto existe
        Producto producto = productoRepository.findById(requestDTO.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con el ID: " + requestDTO.getIdProducto()));

        // Crear el detalle del pedido
        BigDecimal precioUnitario = producto.getPrecio();
        BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(requestDTO.getCantidad()));

        DetallePedido detalle = DetallePedido.builder()
                .pedido(pedido)
                .producto(producto)
                .cantidad(requestDTO.getCantidad())
                .precioUnitario(precioUnitario)
                .subtotal(subtotal)
                .observaciones(requestDTO.getObservaciones())
                .estado(EstadoDetallePedido.PEDIDO)
                .build();

        DetallePedido detalleGuardado = detallePedidoRepository.save(detalle);

        // Descontar del inventario
        inventarioService.actualizarStockPorVenta(producto.getIdProducto(), requestDTO.getCantidad());

        // Recalcular totales del pedido
        recalcularTotalesPedido(idPedido);

        return mapDetallePedidoToResponseDTO(detalleGuardado);
    }
}