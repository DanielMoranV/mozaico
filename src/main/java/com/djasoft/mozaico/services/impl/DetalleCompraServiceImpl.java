package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.domain.entities.Compra;
import com.djasoft.mozaico.domain.entities.DetalleCompra;
import com.djasoft.mozaico.domain.entities.Inventario;
import com.djasoft.mozaico.domain.entities.Producto;
import com.djasoft.mozaico.domain.repositories.CompraRepository;
import com.djasoft.mozaico.domain.repositories.DetalleCompraRepository;
import com.djasoft.mozaico.domain.repositories.InventarioRepository;
import com.djasoft.mozaico.domain.repositories.ProductoRepository;
import com.djasoft.mozaico.services.CompraService;
import com.djasoft.mozaico.services.DetalleCompraService;
import com.djasoft.mozaico.web.dtos.DetalleCompraRequestDTO;
import com.djasoft.mozaico.web.dtos.DetalleCompraResponseDTO;
import com.djasoft.mozaico.web.dtos.DetalleCompraUpdateDTO;
import com.djasoft.mozaico.web.dtos.ProductoResponseDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetalleCompraServiceImpl implements DetalleCompraService {

        private final DetalleCompraRepository detalleCompraRepository;
        private final CompraRepository compraRepository;
        private final ProductoRepository productoRepository;
        private final InventarioRepository inventarioRepository;
        private final CompraService compraService;

        @Override
        @Transactional
        public DetalleCompraResponseDTO crearDetalleCompra(DetalleCompraRequestDTO requestDTO) {
                Compra compra = compraRepository.findById(requestDTO.getIdCompra())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Compra no encontrada con id: " + requestDTO.getIdCompra()));

                Producto producto = productoRepository.findById(requestDTO.getIdProducto())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Producto no encontrado con id: " + requestDTO.getIdProducto()));

                Inventario inventario = inventarioRepository.findByProductoIdProducto(producto.getIdProducto())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Inventario no encontrado para el producto con id: "
                                                                + producto.getIdProducto()));

                BigDecimal precioUnitario = inventario.getCostoUnitario();
                BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(requestDTO.getCantidad()));

                DetalleCompra nuevoDetalle = DetalleCompra.builder()
                                .compra(compra)
                                .producto(producto)
                                .cantidad(requestDTO.getCantidad())
                                .precioUnitario(precioUnitario)
                                .subtotal(subtotal)
                                .build();

                DetalleCompra detalleGuardado = detalleCompraRepository.save(nuevoDetalle);

                compraService.recalcularTotalesCompra(compra.getIdCompra());

                return mapToResponseDTO(detalleGuardado);
        }

        @Override
        @Transactional
        public DetalleCompraResponseDTO actualizarDetalleCompra(Integer id, DetalleCompraUpdateDTO updateDTO) {
                DetalleCompra detalleExistente = detalleCompraRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Detalle de compra no encontrado con id: " + id));

                int cantidadOriginal = detalleExistente.getCantidad();
                int nuevaCantidad = updateDTO.getCantidad() != null ? updateDTO.getCantidad() : cantidadOriginal;

                if (updateDTO.getCantidad() != null && updateDTO.getCantidad() != cantidadOriginal) {
                        detalleExistente.setCantidad(nuevaCantidad);
                        BigDecimal nuevoSubtotal = detalleExistente.getPrecioUnitario()
                                        .multiply(new BigDecimal(nuevaCantidad));
                        detalleExistente.setSubtotal(nuevoSubtotal);
                }

                DetalleCompra detalleActualizado = detalleCompraRepository.save(detalleExistente);

                compraService.recalcularTotalesCompra(detalleActualizado.getCompra().getIdCompra());

                return mapToResponseDTO(detalleActualizado);
        }

        @Override
        @Transactional
        public void eliminarDetalleCompra(Integer id) {
                DetalleCompra detalleExistente = detalleCompraRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Detalle de compra no encontrado con id: " + id));

                Compra compra = detalleExistente.getCompra();

                detalleCompraRepository.delete(detalleExistente);

                compraService.recalcularTotalesCompra(compra.getIdCompra());
        }

        @Override
        @Transactional(readOnly = true)
        public List<DetalleCompraResponseDTO> obtenerTodosLosDetallesPorCompra(Integer idCompra) {
                Compra compra = compraRepository.findById(idCompra)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Compra no encontrada con id: " + idCompra));
                return detalleCompraRepository.findByCompra(compra).stream()
                                .map(this::mapToResponseDTO)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public DetalleCompraResponseDTO obtenerDetalleCompraPorId(Integer id) {
                return detalleCompraRepository.findById(id)
                                .map(this::mapToResponseDTO)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Detalle de compra no encontrado con id: " + id));
        }

        private DetalleCompraResponseDTO mapToResponseDTO(DetalleCompra detalle) {
                ProductoResponseDTO productoDTO = ProductoResponseDTO.builder()
                                .idProducto(detalle.getProducto().getIdProducto())
                                .nombre(detalle.getProducto().getNombre())
                                .build();

                return DetalleCompraResponseDTO.builder()
                                .idDetalleCompra(detalle.getIdDetalleCompra())
                                .idCompra(detalle.getCompra().getIdCompra())
                                .producto(productoDTO)
                                .cantidad(detalle.getCantidad())
                                .precioUnitario(detalle.getPrecioUnitario())
                                .subtotal(detalle.getSubtotal())
                                .build();
        }
}
