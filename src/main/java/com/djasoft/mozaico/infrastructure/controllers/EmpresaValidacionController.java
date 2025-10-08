package com.djasoft.mozaico.infrastructure.controllers;

import com.djasoft.mozaico.application.dtos.empresa.ValidacionIgvResponseDto;
import com.djasoft.mozaico.application.services.EmpresaValidacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controlador REST para validaciones de empresa e IGV
 */
@RestController
@RequestMapping("/api/v1/empresa/validacion")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpresaValidacionController {

    private final EmpresaValidacionService empresaValidacionService;

    /**
     * Obtiene la configuración completa de IGV y capacidades de emisión
     * GET /api/v1/empresa/validacion/igv
     */
    @GetMapping("/igv")
    public ResponseEntity<ValidacionIgvResponseDto> obtenerValidacionIgv() {
        try {
            ValidacionIgvResponseDto validacion = empresaValidacionService.validarConfiguracionIgv();
            return ResponseEntity.ok(validacion);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Validación específica de una empresa
     * GET /api/v1/empresa/validacion/igv/{idEmpresa}
     */
    @GetMapping("/igv/{idEmpresa}")
    public ResponseEntity<ValidacionIgvResponseDto> obtenerValidacionIgvPorEmpresa(
            @PathVariable Long idEmpresa) {
        try {
            ValidacionIgvResponseDto validacion = empresaValidacionService.validarConfiguracionIgv(idEmpresa);
            return ResponseEntity.ok(validacion);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Verificación rápida si aplica IGV
     * GET /api/v1/empresa/validacion/aplica-igv
     */
    @GetMapping("/aplica-igv")
    public ResponseEntity<Boolean> verificarAplicaIgv() {
        Boolean aplicaIgv = empresaValidacionService.puedeAplicarIgv();
        return ResponseEntity.ok(aplicaIgv);
    }

    /**
     * Obtener porcentaje de IGV configurado
     * GET /api/v1/empresa/validacion/porcentaje-igv
     */
    @GetMapping("/porcentaje-igv")
    public ResponseEntity<BigDecimal> obtenerPorcentajeIgv() {
        BigDecimal porcentaje = empresaValidacionService.obtenerPorcentajeIgv();
        return ResponseEntity.ok(porcentaje);
    }

    /**
     * Endpoint para mostrar mensaje al cliente sobre capacidades de facturación
     * GET /api/v1/empresa/validacion/mensaje-cliente
     */
    @GetMapping("/mensaje-cliente")
    public ResponseEntity<String> obtenerMensajeCliente() {
        try {
            ValidacionIgvResponseDto validacion = empresaValidacionService.validarConfiguracionIgv();
            return ResponseEntity.ok(validacion.getMensajeCliente());
        } catch (Exception e) {
            return ResponseEntity.ok("No se pudo obtener información de la empresa.");
        }
    }
}