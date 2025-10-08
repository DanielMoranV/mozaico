package com.djasoft.mozaico.application.services;

import com.djasoft.mozaico.application.dtos.empresa.ValidacionIgvResponseDto;
import com.djasoft.mozaico.domain.entities.Empresa;
import com.djasoft.mozaico.domain.entities.DatosFacturacion;
import com.djasoft.mozaico.domain.enums.empresa.TipoOperacion;
import com.djasoft.mozaico.domain.enums.facturacion.EstadoFormalizacion;
import com.djasoft.mozaico.domain.repositories.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para validar configuración de IGV y capacidades de emisión de
 * comprobantes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmpresaValidacionService {

    private final EmpresaRepository empresaRepository;

    /**
     * Valida la configuración de IGV de la empresa activa
     * 
     * @return ValidacionIgvResponseDto con toda la información de validación
     */
    public ValidacionIgvResponseDto validarConfiguracionIgv() {
        log.info("Iniciando validación de configuración IGV");

        Empresa empresa = empresaRepository.findByActivaTrue()
                .orElseThrow(() -> new RuntimeException("No se encontró empresa activa configurada"));

        return construirValidacionIgv(empresa);
    }

    /**
     * Valida configuración específica de una empresa
     * 
     * @param idEmpresa ID de la empresa a validar
     * @return ValidacionIgvResponseDto con información de validación
     */
    public ValidacionIgvResponseDto validarConfiguracionIgv(Long idEmpresa) {
        log.info("Validando configuración IGV para empresa ID: {}", idEmpresa);

        Empresa empresa = empresaRepository.findById(idEmpresa)
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada con ID: " + idEmpresa));

        return construirValidacionIgv(empresa);
    }

    /**
     * Verifica si la empresa puede aplicar IGV
     * 
     * @return true si puede aplicar IGV, false en caso contrario
     */
    public Boolean puedeAplicarIgv() {
        return empresaRepository.findByActivaTrue()
                .map(Empresa::getAplicaIgv)
                .orElse(false);
    }

    /**
     * Obtiene el porcentaje de IGV configurado
     * 
     * @return Porcentaje de IGV o BigDecimal.ZERO si no aplica
     */
    public BigDecimal obtenerPorcentajeIgv() {
        return empresaRepository.findByActivaTrue()
                .filter(Empresa::getAplicaIgv)
                .map(Empresa::getPorcentajeIgv)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Construye el DTO de validación completo
     */
    private ValidacionIgvResponseDto construirValidacionIgv(Empresa empresa) {
        DatosFacturacion datosFacturacion = empresa.getDatosFacturacion();

        List<String> comprobantesPermitidos = determinarComprobantesPermitidos(empresa);
        List<String> advertencias = generarAdvertencias(empresa);
        List<String> limitaciones = generarLimitaciones(empresa);

        return ValidacionIgvResponseDto.builder()
                // IGV
                .aplicaIgv(empresa.getAplicaIgv())
                .porcentajeIgv(empresa.getPorcentajeIgv())
                .moneda(empresa.getMoneda())

                // Capacidades
                .tipoOperacion(empresa.getTipoOperacion())
                .puedeEmitirFacturas(puedeEmitirFacturas(empresa))
                .puedeEmitirBoletas(puedeEmitirBoletas(empresa))
                .puedeEmitirTickets(puedeEmitirTickets(empresa))
                .facturacionElectronicaActiva(esFacturacionElectronicaActiva(datosFacturacion))

                // Información para cliente
                .mensajeCliente(generarMensajeCliente(empresa))
                .tipoComprobanteDisponible(determinarTipoComprobanteDisponible(empresa))
                .comprobantesPermitidos(comprobantesPermitidos)

                // Datos empresa
                .nombreEmpresa(empresa.getNombre())
                .ruc(datosFacturacion != null ? datosFacturacion.getRuc() : null)
                .tieneRuc(datosFacturacion != null && datosFacturacion.getRuc() != null)
                .logoUrl(empresa.getLogoUrl())

                // Configuración
                .incluyeIgvEnPrecio(empresa.getAplicaIgv())
                .formatoNumeracion(determinarFormatoNumeracion(empresa))
                .prefijoComprobante(determinarPrefijoComprobante(empresa))

                // Estado
                .empresaActiva(empresa.getActiva())
                .configuracionValida(esConfiguracionValida(empresa))
                .advertencias(advertencias)
                .limitaciones(limitaciones)
                .build();
    }

    private List<String> determinarComprobantesPermitidos(Empresa empresa) {
        List<String> comprobantes = new ArrayList<>();

        switch (empresa.getTipoOperacion()) {
            case TICKET_SIMPLE:
                comprobantes.add("Ticket interno sin valor tributario");
                break;
            case BOLETA_MANUAL:
                comprobantes.add("Boleta de venta manual");
                comprobantes.add("Ticket interno");
                break;
            case FACTURACION_ELECTRONICA:
                comprobantes.add("Factura electrónica");
                comprobantes.add("Boleta electrónica");
                comprobantes.add("Nota de crédito");
                comprobantes.add("Nota de débito");
                break;
            case MIXTO:
                comprobantes.add("Ticket interno");
                comprobantes.add("Boleta electrónica");
                comprobantes.add("Factura electrónica");
                break;
        }

        return comprobantes;
    }

    private Boolean puedeEmitirFacturas(Empresa empresa) {
        return empresa.getTipoOperacion() == TipoOperacion.FACTURACION_ELECTRONICA ||
                empresa.getTipoOperacion() == TipoOperacion.MIXTO;
    }

    private Boolean puedeEmitirBoletas(Empresa empresa) {
        return empresa.getTipoOperacion() != TipoOperacion.TICKET_SIMPLE;
    }

    private Boolean puedeEmitirTickets(Empresa empresa) {
        return true; // Todas las empresas pueden emitir tickets internos
    }

    private Boolean esFacturacionElectronicaActiva(DatosFacturacion datosFacturacion) {
        return datosFacturacion != null && datosFacturacion.getFacturacionElectronicaActiva();
    }

    private String generarMensajeCliente(Empresa empresa) {
        if (!empresa.getAplicaIgv()) {
            return "🎟️ Esta empresa opera como negocio informal. " +
                    "Los comprobantes emitidos son tickets internos sin valor tributario y NO incluyen IGV.";
        }

        DatosFacturacion datos = empresa.getDatosFacturacion();
        if (datos == null || datos.getRuc() == null) {
            return "⚠️ Esta empresa aplica IGV pero no tiene RUC registrado. " +
                    "Los precios incluyen IGV pero solo se pueden emitir comprobantes internos.";
        }

        if (empresa.getTipoOperacion() == TipoOperacion.BOLETA_MANUAL) {
            return "📄 Esta empresa puede emitir boletas de venta manuales. " +
                    "Los precios incluyen IGV (" + empresa.getPorcentajeIgv() + "%).";
        }

        if (empresa.getTipoOperacion() == TipoOperacion.FACTURACION_ELECTRONICA) {
            return "✅ Esta empresa puede emitir comprobantes electrónicos válidos ante SUNAT. " +
                    "Los precios incluyen IGV (" + empresa.getPorcentajeIgv() + "%). " +
                    "RUC: " + datos.getRuc();
        }

        return "ℹ️ Consulte con el personal sobre los tipos de comprobante disponibles.";
    }

    private String determinarTipoComprobanteDisponible(Empresa empresa) {
        switch (empresa.getTipoOperacion()) {
            case TICKET_SIMPLE:
                return "Ticket interno";
            case BOLETA_MANUAL:
                return "Boleta manual";
            case FACTURACION_ELECTRONICA:
                return "Comprobantes electrónicos";
            case MIXTO:
                return "Múltiples tipos";
            default:
                return "No definido";
        }
    }

    private String determinarFormatoNumeracion(Empresa empresa) {
        DatosFacturacion datos = empresa.getDatosFacturacion();

        if (datos != null && datos.getFacturacionElectronicaActiva()) {
            return datos.getSerieFactura() + "-########";
        }

        return empresa.getPrefijoTicket() + "-########";
    }

    private String determinarPrefijoComprobante(Empresa empresa) {
        DatosFacturacion datos = empresa.getDatosFacturacion();

        if (datos != null && datos.getFacturacionElectronicaActiva()) {
            return datos.getSerieFactura() != null ? datos.getSerieFactura() : "F001";
        }

        return empresa.getPrefijoTicket();
    }

    private List<String> generarAdvertencias(Empresa empresa) {
        List<String> advertencias = new ArrayList<>();

        if (!empresa.getActiva()) {
            advertencias.add("La empresa está marcada como inactiva");
        }

        if (empresa.getAplicaIgv() && empresa.getDatosFacturacion() == null) {
            advertencias.add("La empresa aplica IGV pero no tiene datos de facturación configurados");
        }

        DatosFacturacion datos = empresa.getDatosFacturacion();
        if (datos != null && datos.getEstadoFormalizacion() == EstadoFormalizacion.SIN_RUC && empresa.getAplicaIgv()) {
            advertencias.add("Configuración inconsistente: aplica IGV pero está marcada como sin RUC");
        }

        return advertencias;
    }

    private List<String> generarLimitaciones(Empresa empresa) {
        List<String> limitaciones = new ArrayList<>();

        if (empresa.getTipoOperacion() == TipoOperacion.TICKET_SIMPLE) {
            limitaciones.add("Solo puede emitir tickets internos sin valor tributario");
            limitaciones.add("No puede emitir comprobantes válidos ante SUNAT");
        }

        if (!empresa.getAplicaIgv()) {
            limitaciones.add("No puede incluir IGV en los comprobantes");
        }

        DatosFacturacion datos = empresa.getDatosFacturacion();
        if (datos == null || !datos.getFacturacionElectronicaActiva()) {
            limitaciones.add("No puede emitir comprobantes electrónicos");
        }

        return limitaciones;
    }

    private Boolean esConfiguracionValida(Empresa empresa) {
        // Validaciones básicas
        if (!empresa.getActiva())
            return false;

        // Si aplica IGV, debe tener configuración coherente
        if (empresa.getAplicaIgv()) {
            if (empresa.getPorcentajeIgv() == null || empresa.getPorcentajeIgv().compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }

            // Si tiene facturación electrónica, debe tener datos completos
            DatosFacturacion datos = empresa.getDatosFacturacion();
            if (empresa.getTipoOperacion() == TipoOperacion.FACTURACION_ELECTRONICA) {
                return datos != null && datos.getRuc() != null && datos.getFacturacionElectronicaActiva();
            }
        }

        return true;
    }
}