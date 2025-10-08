package com.djasoft.mozaico.infrastructure.controllers;

import com.djasoft.mozaico.application.services.ComprobanteService;
import com.djasoft.mozaico.domain.entities.Comprobante;
import com.djasoft.mozaico.web.dtos.ComprobanteResponseDTO;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de comprobantes de pago
 */
@RestController
@RequestMapping("/api/v1/comprobantes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComprobanteController {

    private final ComprobanteService comprobanteService;

    /**
     * Obtener lista de todos los comprobantes
     * GET /api/v1/comprobantes
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ComprobanteResponseDTO>>> obtenerTodosLosComprobantes() {
        List<ComprobanteResponseDTO> comprobantes = comprobanteService.obtenerTodosLosComprobantes();
        return ResponseEntity.ok(ApiResponse.success(comprobantes, "Lista de comprobantes obtenida exitosamente"));
    }

    /**
     * Obtener comprobante por ID de pago
     * GET /api/v1/comprobantes/pago/{idPago}
     */
    @GetMapping("/pago/{idPago}")
    public ResponseEntity<ApiResponse<Comprobante>> obtenerComprobantePorPago(@PathVariable Integer idPago) {
        Comprobante comprobante = comprobanteService.obtenerComprobantePorPago(idPago);
        return ResponseEntity.ok(ApiResponse.success(comprobante, "Comprobante encontrado exitosamente"));
    }

    /**
     * Descargar ticket en formato PDF
     * GET /api/v1/comprobantes/{id}/ticket
     */
    @GetMapping("/{id}/ticket")
    public ResponseEntity<Resource> descargarTicket(@PathVariable Integer id) {
        // Marcar como impreso y obtener archivo
        Resource resource = comprobanteService.descargarYMarcarImpreso(id, false);
        Comprobante comprobante = comprobanteService.obtenerComprobantePorId(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                       "attachment; filename=\"ticket_" + comprobante.getNumeroComprobante() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    /**
     * Descargar ticket con header para impresión automática
     * GET /api/v1/comprobantes/{id}/ticket/imprimir
     */
    @GetMapping("/{id}/ticket/imprimir")
    public ResponseEntity<Resource> descargarTicketParaImprimir(@PathVariable Integer id) {
        // Marcar como impreso y obtener archivo
        Resource resource = comprobanteService.descargarYMarcarImpreso(id, true);
        Comprobante comprobante = comprobanteService.obtenerComprobantePorId(id);

        // Headers para impresión automática
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                       "inline; filename=\"ticket_" + comprobante.getNumeroComprobante() + ".pdf\"")
                .header("X-Auto-Print", "true")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    /**
     * Descargar comprobante en formato PDF
     * GET /api/v1/comprobantes/{id}/pdf
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> descargarPDF(@PathVariable Integer id) {
        // Marcar como impreso y obtener archivo PDF completo
        Resource resource = comprobanteService.descargarYMarcarImpreso(id, false);
        Comprobante comprobante = comprobanteService.obtenerComprobantePorId(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                       "attachment; filename=\"comprobante_" + comprobante.getNumeroComprobante() + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    /**
     * Reimprimir comprobante
     * POST /api/v1/comprobantes/{id}/reimprimir
     */
    @PostMapping("/{id}/reimprimir")
    public ResponseEntity<ApiResponse<String>> reimprimirComprobante(@PathVariable Integer id) {
        comprobanteService.reimprimirComprobante(id);
        return ResponseEntity.ok(ApiResponse.success("Comprobante enviado a impresión", 
                                                   "Comprobante reimpreso exitosamente"));
    }

    /**
     * Anular comprobante
     * POST /api/v1/comprobantes/{id}/anular
     */
    @PostMapping("/{id}/anular")
    public ResponseEntity<ApiResponse<String>> anularComprobante(
            @PathVariable Integer id,
            @RequestParam String motivo) {
        comprobanteService.anularComprobante(id, motivo);
        return ResponseEntity.ok(ApiResponse.success("Comprobante anulado",
                                                   "Comprobante anulado exitosamente"));
    }

    /**
     * Enviar comprobante por email
     * POST /api/v1/comprobantes/{id}/enviar
     */
    @PostMapping("/{id}/enviar")
    public ResponseEntity<ApiResponse<String>> enviarPorEmail(
            @PathVariable Integer id,
            @RequestParam String email) {
        comprobanteService.enviarPorEmail(id, email);
        return ResponseEntity.ok(ApiResponse.success("Comprobante enviado",
                                                   "Comprobante enviado a: " + email));
    }
}