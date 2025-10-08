package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.comprobante.EstadoComprobante;
import com.djasoft.mozaico.domain.enums.comprobante.TipoComprobante;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprobanteResponseDTO {
    
    private Integer idComprobante;
    private TipoComprobante tipoComprobante;
    private String numeroComprobante;
    private String serieComprobante;
    private LocalDateTime fechaEmision;
    private EstadoComprobante estado;
    private String hashVerificacion;
    
    // URLs para descargar archivos
    private String urlDescargaTicket;
    private String urlDescargaPdf;
    private String urlVisualizacion;
    
    // Información adicional
    private String observaciones;
    private boolean archivoTicketDisponible;
    private boolean archivoPdfDisponible;
}