package com.djasoft.mozaico.application.dtos.empresa;

import com.djasoft.mozaico.domain.enums.empresa.TipoOperacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO que contiene la validación de IGV y capacidades de emisión de comprobantes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionIgvResponseDto {
    
    // === INFORMACIÓN DE IGV ===
    private Boolean aplicaIgv;
    private BigDecimal porcentajeIgv;
    private String moneda;
    
    // === CAPACIDADES DE EMISIÓN ===
    private TipoOperacion tipoOperacion;
    private Boolean puedeEmitirFacturas;
    private Boolean puedeEmitirBoletas;
    private Boolean puedeEmitirTickets;
    private Boolean facturacionElectronicaActiva;
    
    // === INFORMACIÓN PARA EL CLIENTE ===
    private String mensajeCliente;
    private String tipoComprobanteDisponible;
    private List<String> comprobantesPermitidos;
    
    // === DATOS DE LA EMPRESA ===
    private String nombreEmpresa;
    private String ruc;
    private Boolean tieneRuc;
    private String logoUrl;
    
    // === CONFIGURACIÓN DE CÁLCULOS ===
    private Boolean incluyeIgvEnPrecio;
    private String formatoNumeracion;
    private String prefijoComprobante;
    
    // === ESTADO Y VALIDACIONES ===
    private Boolean empresaActiva;
    private Boolean configuracionValida;
    private List<String> advertencias;
    private List<String> limitaciones;
}