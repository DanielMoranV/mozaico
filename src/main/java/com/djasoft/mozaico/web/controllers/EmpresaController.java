package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.domain.entities.Empresa;
import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.repositories.EmpresaRepository;
import com.djasoft.mozaico.infrastructure.utils.SlugGenerator;
import com.djasoft.mozaico.services.EmpresaService;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import com.djasoft.mozaico.web.dtos.EmpresaResponseDTO;
import com.djasoft.mozaico.web.dtos.EmpresaSlugUpdateDTO;
import com.djasoft.mozaico.web.dtos.EmpresaUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller para gestión de empresa
 *
 * Permisos:
 * - Todos los usuarios autenticados pueden VER su empresa
 * - Solo ADMIN y SUPER_ADMIN pueden MODIFICAR la empresa
 * - Solo SUPER_ADMIN puede ACTIVAR/DESACTIVAR la empresa
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/empresa")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaRepository empresaRepository;
    private final EmpresaService empresaService;

    /**
     * Obtener información de la empresa del usuario autenticado
     * GET /api/v1/empresa
     *
     * Cualquier usuario autenticado puede ver la información de su empresa
     */
    @GetMapping
    public ResponseEntity<ApiResponse<EmpresaResponseDTO>> obtenerEmpresa() {
        EmpresaResponseDTO empresa = empresaService.obtenerMiEmpresa();
        return ResponseEntity.ok(ApiResponse.success(empresa, "Información de empresa obtenida"));
    }

    /**
     * Actualizar información de la empresa
     * PUT /api/v1/empresa
     *
     * Solo ADMIN y SUPER_ADMIN pueden modificar la empresa
     */
    @PutMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ALL_PERMISSIONS') or " +
            "@securityService.isTipoUsuario('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EmpresaResponseDTO>> actualizarEmpresa(
            @Valid @RequestBody EmpresaUpdateDTO dto) {

        log.info("Actualizando información de empresa");
        EmpresaResponseDTO empresa = empresaService.actualizarEmpresa(dto);

        return ResponseEntity.ok(ApiResponse.success(
                empresa,
                "Empresa actualizada exitosamente"));
    }

    /**
     * Actualizar logo de la empresa
     * PUT /api/v1/empresa/logo
     *
     * Solo ADMIN y SUPER_ADMIN pueden modificar el logo
     */
    @PutMapping(value = "/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ALL_PERMISSIONS') or " +
            "@securityService.isTipoUsuario('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EmpresaResponseDTO>> actualizarLogo(
            @RequestParam("file") MultipartFile file) {

        log.info("Actualizando logo de empresa");
        EmpresaResponseDTO empresa = empresaService.actualizarLogo(file);

        return ResponseEntity.ok(ApiResponse.success(
                empresa,
                "Logo actualizado exitosamente"));
    }

    /**
     * Activar o desactivar la empresa
     * PATCH /api/v1/empresa/estado
     *
     * Solo SUPER_ADMIN puede cambiar el estado
     */
    @PatchMapping("/estado")
    @PreAuthorize("@securityService.isTipoUsuario('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<EmpresaResponseDTO>> cambiarEstado(
            @RequestParam Boolean activa) {

        log.info("Cambiando estado de empresa a: {}", activa ? "ACTIVA" : "INACTIVA");
        EmpresaResponseDTO empresa = empresaService.cambiarEstadoEmpresa(activa);

        return ResponseEntity.ok(ApiResponse.success(
                empresa,
                "Estado de empresa actualizado"));
    }

    /**
     * Obtener estadísticas de la empresa
     * GET /api/v1/empresa/estadisticas
     *
     * ADMIN y SUPER_ADMIN pueden ver estadísticas
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAnyAuthority('ROLE_ALL_PERMISSIONS') or " +
            "@securityService.isTipoUsuario('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Object>> obtenerEstadisticas() {
        Object stats = empresaService.obtenerEstadisticas();
        return ResponseEntity.ok(ApiResponse.success(stats, "Estadísticas obtenidas"));
    }

    /**
     * Actualizar slug de la empresa
     * PUT /api/v1/empresa/slug
     *
     * Permite cambiar el slug único de la empresa para URLs públicas
     */
    @PutMapping("/slug")
    public ResponseEntity<ApiResponse<EmpresaResponseDTO>> actualizarSlug(
            @Valid @RequestBody EmpresaSlugUpdateDTO dto) {

        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null || currentUser.getEmpresa() == null) {
            throw new IllegalStateException("Usuario no tiene empresa asociada");
        }

        Empresa empresa = currentUser.getEmpresa();
        String nuevoSlug = dto.getSlug().toLowerCase().trim();

        // Validar que el slug tenga formato correcto
        if (!SlugGenerator.isValidSlug(nuevoSlug)) {
            throw new IllegalArgumentException(
                    "El slug solo puede contener letras minúsculas, números y guiones. " +
                            "No puede empezar ni terminar con guión.");
        }

        // Verificar que el nuevo slug no esté en uso por otra empresa
        if (!nuevoSlug.equals(empresa.getSlug())) {
            boolean existe = empresaRepository.existsBySlug(nuevoSlug);
            if (existe) {
                throw new IllegalArgumentException("El slug '" + nuevoSlug + "' ya está en uso por otra empresa");
            }
        }

        log.info("Actualizando slug de empresa {} de '{}' a '{}'",
                empresa.getNombre(), empresa.getSlug(), nuevoSlug);

        empresa.setSlug(nuevoSlug);
        Empresa empresaActualizada = empresaRepository.save(empresa);

        // Obtener DTO actualizado
        EmpresaResponseDTO empresaDTO = empresaService.obtenerMiEmpresa();

        return ResponseEntity.ok(ApiResponse.success(
                empresaDTO,
                "Slug actualizado exitosamente. URL pública: /public/" + nuevoSlug + "/carta"));
    }

    /**
     * Generar slug automático desde el nombre de la empresa
     * POST /api/v1/empresa/slug/generar
     *
     * Genera un slug basado en el nombre actual de la empresa
     */
    @PostMapping("/slug/generar")
    public ResponseEntity<ApiResponse<String>> generarSlugAutomatico() {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null || currentUser.getEmpresa() == null) {
            throw new IllegalStateException("Usuario no tiene empresa asociada");
        }

        Empresa empresa = currentUser.getEmpresa();

        // Generar slug base del nombre
        String slugBase = SlugGenerator.generateSlug(empresa.getNombre());

        // Generar slug único (agregar número si ya existe)
        String slugUnico = SlugGenerator.generateUniqueSlug(
                slugBase,
                empresaRepository::existsBySlug);

        return ResponseEntity.ok(ApiResponse.success(
                slugUnico,
                "Slug generado: '" + slugUnico + "'. Usa PUT /api/v1/empresa/slug para guardarlo."));
    }

    /**
     * Verificar si un slug está disponible
     * GET /api/v1/empresa/slug/disponible?slug=mi-slug
     */
    @GetMapping("/slug/disponible")
    public ResponseEntity<ApiResponse<Boolean>> verificarSlugDisponible(
            @RequestParam String slug) {

        String slugNormalizado = slug.toLowerCase().trim();

        // Validar formato
        if (!SlugGenerator.isValidSlug(slugNormalizado)) {
            return ResponseEntity.ok(ApiResponse.success(
                    false,
                    "Formato inválido. Solo letras minúsculas, números y guiones."));
        }

        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        Empresa empresa = currentUser != null ? currentUser.getEmpresa() : null;

        // Si es el slug actual de la empresa, está disponible para ella
        if (empresa != null && slugNormalizado.equals(empresa.getSlug())) {
            return ResponseEntity.ok(ApiResponse.success(
                    true,
                    "Este es tu slug actual"));
        }

        // Verificar si otra empresa lo usa
        boolean existe = empresaRepository.existsBySlug(slugNormalizado);

        return ResponseEntity.ok(ApiResponse.success(
                !existe,
                existe ? "Slug no disponible (ya está en uso)" : "Slug disponible"));
    }
}
