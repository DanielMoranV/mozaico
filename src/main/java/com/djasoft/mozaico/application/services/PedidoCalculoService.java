package com.djasoft.mozaico.application.services;

import com.djasoft.mozaico.application.dtos.empresa.ValidacionIgvResponseDto;
import com.djasoft.mozaico.domain.entities.DetallePedido;
import com.djasoft.mozaico.domain.entities.Pedido;
import com.djasoft.mozaico.domain.repositories.DetallePedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Servicio para cálculos de pedidos con validación automática de IGV
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoCalculoService {

    private final EmpresaValidacionService empresaValidacionService;
    private final DetallePedidoRepository detallePedidoRepository;

    /**
     * Calcula los totales de un pedido considerando la configuración de IGV de la empresa
     */
    public PedidoCalculoResult calcularTotalesPedido(Pedido pedido) {
        log.info("Calculando totales para pedido ID: {}", pedido.getIdPedido());
        
        // Validar configuración de la empresa
        ValidacionIgvResponseDto validacion = empresaValidacionService.validarConfiguracionIgv();
        
        // Obtener detalles del pedido
        List<DetallePedido> detalles = detallePedidoRepository.findByPedido(pedido);
        
        // Calcular subtotal
        BigDecimal subtotal = detalles.stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calcular IGV según configuración de empresa
        BigDecimal igv = BigDecimal.ZERO;
        if (validacion.getAplicaIgv()) {
            igv = subtotal.multiply(validacion.getPorcentajeIgv())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        
        // Calcular descuentos (por ahora 0)
        BigDecimal descuento = BigDecimal.ZERO;
        
        // Calcular total
        BigDecimal total = subtotal.add(igv).subtract(descuento);
        
        // Actualizar pedido
        pedido.setSubtotal(subtotal);
        pedido.setImpuestos(igv);
        pedido.setDescuento(descuento);
        pedido.setTotal(total);
        
        return PedidoCalculoResult.builder()
                .subtotal(subtotal)
                .igv(igv)
                .descuento(descuento)
                .total(total)
                .aplicaIgv(validacion.getAplicaIgv())
                .porcentajeIgv(validacion.getPorcentajeIgv())
                .tipoComprobante(validacion.getTipoComprobanteDisponible())
                .mensajeCliente(validacion.getMensajeCliente())
                .advertencias(validacion.getAdvertencias())
                .limitaciones(validacion.getLimitaciones())
                .formatoNumeracion(validacion.getFormatoNumeracion())
                .build();
    }

    /**
     * Simula el cálculo sin persistir cambios (para preview)
     */
    public PedidoCalculoResult simularCalculoPedido(List<DetallePedido> detalles) {
        log.info("Simulando cálculo para {} detalles", detalles.size());
        
        ValidacionIgvResponseDto validacion = empresaValidacionService.validarConfiguracionIgv();
        
        BigDecimal subtotal = detalles.stream()
                .map(DetallePedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal igv = BigDecimal.ZERO;
        if (validacion.getAplicaIgv()) {
            igv = subtotal.multiply(validacion.getPorcentajeIgv())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }
        
        BigDecimal total = subtotal.add(igv);
        
        return PedidoCalculoResult.builder()
                .subtotal(subtotal)
                .igv(igv)
                .descuento(BigDecimal.ZERO)
                .total(total)
                .aplicaIgv(validacion.getAplicaIgv())
                .porcentajeIgv(validacion.getPorcentajeIgv())
                .tipoComprobante(validacion.getTipoComprobanteDisponible())
                .mensajeCliente(validacion.getMensajeCliente())
                .advertencias(validacion.getAdvertencias())
                .limitaciones(validacion.getLimitaciones())
                .formatoNumeracion(validacion.getFormatoNumeracion())
                .build();
    }

    /**
     * DTO interno para resultados de cálculo
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class PedidoCalculoResult {
        private BigDecimal subtotal;
        private BigDecimal igv;
        private BigDecimal descuento;
        private BigDecimal total;
        
        private Boolean aplicaIgv;
        private BigDecimal porcentajeIgv;
        private String tipoComprobante;
        private String mensajeCliente;
        private String formatoNumeracion;
        
        private List<String> advertencias;
        private List<String> limitaciones;
    }
}