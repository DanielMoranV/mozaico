package com.djasoft.mozaico.application.services;

import com.djasoft.mozaico.domain.entities.Comprobante;
import com.djasoft.mozaico.domain.entities.Pago;
import com.djasoft.mozaico.domain.enums.comprobante.TipoComprobante;
import com.djasoft.mozaico.web.dtos.ComprobanteResponseDTO;

import java.io.IOException;
import java.util.List;

public interface ComprobanteService {

    /**
     * Genera un comprobante automáticamente basado en la configuración de la empresa
     */
    Comprobante generarComprobanteAutomatico(Pago pago);

    /**
     * Genera un comprobante de tipo específico
     */
    Comprobante generarComprobante(Pago pago, TipoComprobante tipoComprobante);

    /**
     * Genera el archivo PDF del comprobante
     */
    String generarPDF(Comprobante comprobante);

    /**
     * Genera el archivo de ticket (texto plano para impresoras térmicas)
     */
    String generarTicket(Comprobante comprobante) throws IOException;

    /**
     * Obtiene un comprobante por ID de pago
     */
    Comprobante obtenerComprobantePorPago(Integer idPago);

    /**
     * Obtiene un comprobante por ID
     */
    Comprobante obtenerComprobantePorId(Integer idComprobante);

    /**
     * Obtiene todos los comprobantes con información completa
     */
    List<ComprobanteResponseDTO> obtenerTodosLosComprobantes();

    /**
     * Reimprime un comprobante existente (regenera archivos si no existen)
     */
    void reimprimirComprobante(Integer idComprobante);

    /**
     * Descarga comprobante y marca como impreso automáticamente
     * @param idComprobante ID del comprobante
     * @param esImpresionAutomatica true si es para impresión automática
     * @return Resource del archivo
     */
    org.springframework.core.io.Resource descargarYMarcarImpreso(Integer idComprobante, boolean esImpresionAutomatica);

    /**
     * Anula un comprobante con validaciones
     */
    void anularComprobante(Integer idComprobante, String motivo);

    /**
     * Envía comprobante por email
     */
    void enviarPorEmail(Integer idComprobante, String emailDestino);
}