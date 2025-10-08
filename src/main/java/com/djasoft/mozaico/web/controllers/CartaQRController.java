package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.domain.entities.Empresa;
import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.repositories.EmpresaRepository;
import com.djasoft.mozaico.services.QRCodeService;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller para generar códigos QR de cartas digitales
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/carta-qr")
@RequiredArgsConstructor
public class CartaQRController {

    private final QRCodeService qrCodeService;
    private final EmpresaRepository empresaRepository;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    /**
     * Genera código QR para la carta digital de la empresa autenticada
     * GET /api/v1/carta-qr/generar
     *
     * Requiere autenticación. Genera un QR que apunta a la carta pública
     * de la empresa usando su slug único.
     *
     * @return Imagen PNG del código QR
     */
    @GetMapping("/generar")
    public ResponseEntity<byte[]> generarQRCarta() {
        try {
            // Obtener usuario autenticado
            Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
            if (currentUser == null || currentUser.getEmpresa() == null) {
                throw new IllegalStateException("No se pudo determinar la empresa del usuario autenticado");
            }

            Empresa empresa = currentUser.getEmpresa();

            if (empresa.getSlug() == null || empresa.getSlug().isEmpty()) {
                throw new IllegalStateException("La empresa no tiene un slug configurado");
            }

            log.info("Generando QR para carta digital de empresa: {} (slug: {})",
                    empresa.getNombre(), empresa.getSlug());

            // Generar código QR
            byte[] qrImage = qrCodeService.generateCartaQRCode(empresa.getSlug(), frontendUrl);

            // Retornar imagen PNG
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"qr-carta-" + empresa.getSlug() + ".png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrImage);

        } catch (Exception e) {
            log.error("Error al generar código QR: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar código QR: " + e.getMessage());
        }
    }

    /**
     * Genera código QR para una empresa específica (endpoint público)
     * GET /api/v1/carta-qr/public/{slug}
     *
     * NO requiere autenticación. Permite generar el QR de cualquier empresa
     * para uso en marketing, impresión, etc.
     *
     * @param slug Slug único de la empresa
     * @return Imagen PNG del código QR
     */
    @GetMapping("/public/{slug}")
    public ResponseEntity<byte[]> generarQRPublico(@PathVariable String slug) {
        try {
            // Buscar empresa por slug
            Empresa empresa = empresaRepository.findBySlug(slug)
                    .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con slug: " + slug));

            log.info("Generando QR público para carta de: {} (slug: {})",
                    empresa.getNombre(), empresa.getSlug());

            // Generar código QR
            byte[] qrImage = qrCodeService.generateCartaQRCode(empresa.getSlug(), frontendUrl);

            // Retornar imagen PNG
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=\"qr-carta-" + empresa.getSlug() + ".png\"")
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrImage);

        } catch (Exception e) {
            log.error("Error al generar código QR público: {}", e.getMessage(), e);
            throw new RuntimeException("Error al generar código QR: " + e.getMessage());
        }
    }
}
