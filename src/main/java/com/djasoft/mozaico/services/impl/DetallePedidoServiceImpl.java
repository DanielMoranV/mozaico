package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.DetallePedido;
import com.djasoft.mozaico.domain.entities.Pedido;
import com.djasoft.mozaico.domain.entities.Producto;
import com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido;
import com.djasoft.mozaico.domain.repositories.DetallePedidoRepository;
import com.djasoft.mozaico.domain.repositories.PedidoRepository;
import com.djasoft.mozaico.domain.repositories.ProductoRepository;
import com.djasoft.mozaico.services.DetallePedidoService;
import com.djasoft.mozaico.services.InventarioService;
import com.djasoft.mozaico.services.PedidoService;
import com.djasoft.mozaico.web.dtos.DetallePedidoRequestDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.DetallePedidoUpdateDTO;
import com.djasoft.mozaico.web.dtos.PedidoResponseDTO;
import com.djasoft.mozaico.web.dtos.ProductoResponseDTO;
import com.djasoft.mozaico.web.dtos.ClienteResponseDTO;
import com.djasoft.mozaico.web.dtos.MesaResponseDTO;
import com.djasoft.mozaico.web.dtos.UsuarioResponseDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetallePedidoServiceImpl implements DetallePedidoService {

    private final DetallePedidoRepository detallePedidoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final InventarioService inventarioService;
    private final PedidoService pedidoService;

    @Override
    @Transactional
    public DetallePedidoResponseDTO crearDetallePedido(DetallePedidoRequestDTO requestDTO) {
        Pedido pedido = pedidoRepository.findById(requestDTO.getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + requestDTO.getIdPedido()));

        Producto producto = productoRepository.findById(requestDTO.getIdProducto())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + requestDTO.getIdProducto()));

        // Calcular subtotal
        BigDecimal subtotal = producto.getPrecio().multiply(new BigDecimal(requestDTO.getCantidad()));

        DetallePedido nuevoDetalle = DetallePedido.builder()
                .pedido(pedido)
                .producto(producto)
                .cantidad(requestDTO.getCantidad())
                .precioUnitario(producto.getPrecio())
                .subtotal(subtotal)
                .build();

        DetallePedido detalleGuardado = detallePedidoRepository.save(nuevoDetalle);

        // Lógica de negocio del README
        // 1. Actualizar inventario
        inventarioService.actualizarStockPorVenta(producto.getIdProducto(), requestDTO.getCantidad());

        // 2. Recalcular totales del pedido
        pedidoService.recalcularTotalesPedido(pedido.getIdPedido());

        return mapToResponseDTO(detalleGuardado);
    }

    @Override
    @Transactional
    public DetallePedidoResponseDTO actualizarDetallePedido(Integer id, DetallePedidoUpdateDTO updateDTO) {
        DetallePedido detalleExistente = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado con id: " + id));

        int cantidadOriginal = detalleExistente.getCantidad();
        int nuevaCantidad = updateDTO.getCantidad() != null ? updateDTO.getCantidad() : cantidadOriginal;

        if (updateDTO.getCantidad() != null && updateDTO.getCantidad() != cantidadOriginal) {
            detalleExistente.setCantidad(nuevaCantidad);
            // Recalcular subtotal del detalle
            BigDecimal nuevoSubtotal = detalleExistente.getPrecioUnitario().multiply(new BigDecimal(nuevaCantidad));
            detalleExistente.setSubtotal(nuevoSubtotal);
        }

        DetallePedido detalleActualizado = detallePedidoRepository.save(detalleExistente);

        // Lógica de negocio del README
        // 1. Ajustar inventario
        int diferenciaCantidad = nuevaCantidad - cantidadOriginal;
        if (diferenciaCantidad != 0) {
            inventarioService.actualizarStockPorVenta(detalleActualizado.getProducto().getIdProducto(), diferenciaCantidad);
        }

        // 2. Recalcular totales del pedido
        pedidoService.recalcularTotalesPedido(detalleActualizado.getPedido().getIdPedido());

        return mapToResponseDTO(detalleActualizado);
    }

    @Override
    @Transactional
    public void eliminarDetallePedido(Integer id) {
        DetallePedido detalleExistente = detallePedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado con id: " + id));

        int cantidadOriginal = detalleExistente.getCantidad();
        Producto producto = detalleExistente.getProducto();
        Pedido pedido = detalleExistente.getPedido();

        detallePedidoRepository.delete(detalleExistente);

        // Lógica de negocio del README
        // 1. Revertir el stock en el inventario
        inventarioService.actualizarStockPorVenta(producto.getIdProducto(), -cantidadOriginal); // Devolver stock

        // 2. Recalcular totales del pedido
        pedidoService.recalcularTotalesPedido(pedido.getIdPedido());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedidoResponseDTO> obtenerTodosLosDetallesPorPedido(Integer idPedido) {
        if (!pedidoRepository.existsById(idPedido)) {
            throw new ResourceNotFoundException("Pedido no encontrado con id: " + idPedido);
        }
        return detallePedidoRepository.findByPedidoIdPedido(idPedido).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DetallePedidoResponseDTO obtenerDetallePedidoPorId(Integer id) {
        return detallePedidoRepository.findById(id)
                .map(this::mapToResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedidoResponseDTO> obtenerDetallesPorEstado(EstadoDetallePedido estado) {
        return detallePedidoRepository.findByEstado(estado).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetallePedidoResponseDTO> obtenerDetallesKdsQueRequierenPreparacion(EstadoDetallePedido estado) {
        // Usa query optimizada que filtra por pedidos ABIERTO y ATENDIDO, excluyendo PAGADO y CANCELADO
        return detallePedidoRepository.findByEstadoParaKds(estado, true).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DetallePedidoResponseDTO cambiarEstadoDetalle(Integer idDetalle, EstadoDetallePedido nuevoEstado) {
        DetallePedido detalle = detallePedidoRepository.findById(idDetalle)
                .orElseThrow(() -> new ResourceNotFoundException("Detalle de pedido no encontrado con id: " + idDetalle));

        detalle.setEstado(nuevoEstado);
        DetallePedido detalleActualizado = detallePedidoRepository.save(detalle);
        return mapToResponseDTO(detalleActualizado);
    }

    private DetallePedidoResponseDTO mapToResponseDTO(DetallePedido detalle) {
        ProductoResponseDTO productoDTO = ProductoResponseDTO.builder()
                .idProducto(detalle.getProducto().getIdProducto())
                .nombre(detalle.getProducto().getNombre())
                .precio(detalle.getProducto().getPrecio())
                .build();

        Pedido pedido = detalle.getPedido();
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

        PedidoResponseDTO pedidoDTO = PedidoResponseDTO.builder()
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

        return DetallePedidoResponseDTO.builder()
                .idDetalle(detalle.getIdDetalle())
                .pedido(pedidoDTO)
                .producto(productoDTO)
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .subtotal(detalle.getSubtotal())
                .observaciones(detalle.getObservaciones())
                .estado(detalle.getEstado())
                .build();
    }
}