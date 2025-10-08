package com.djasoft.mozaico.web.controllers;

import com.djasoft.mozaico.domain.entities.AuditLog;
import com.djasoft.mozaico.security.annotations.RequirePermission;
import com.djasoft.mozaico.services.AuditService;
import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Auditoría", description = "Endpoints para consultar logs de auditoría")
public class AuditController {

        private final AuditService auditService;

        @GetMapping
        @Operation(summary = "Obtener logs de auditoría", description = "Obtiene los logs de auditoría de la empresa paginados")
        @RequirePermission({ "VIEW_REPORTS", "ALL_PERMISSIONS" })
        public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(
                        @PageableDefault(size = 20, sort = "fechaHora") @Parameter(description = "Configuración de paginación") Pageable pageable) {

                Page<AuditLog> auditLogs = auditService.getAuditLogsForCurrentCompany(pageable);

                return ResponseEntity.ok(ApiResponse.success(
                                auditLogs,
                                "Logs de auditoría obtenidos exitosamente"));
        }

        @GetMapping("/user/{usuarioId}")
        @Operation(summary = "Obtener logs de un usuario específico", description = "Obtiene los logs de auditoría de un usuario específico")
        @RequirePermission({ "VIEW_REPORTS", "MANAGE_USERS", "ALL_PERMISSIONS" })
        public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogsForUser(
                        @PathVariable @Parameter(description = "ID del usuario") Long usuarioId,
                        @PageableDefault(size = 20, sort = "fechaHora") @Parameter(description = "Configuración de paginación") Pageable pageable) {

                Page<AuditLog> auditLogs = auditService.getAuditLogsForUser(usuarioId, pageable);

                return ResponseEntity.ok(ApiResponse.success(
                                auditLogs,
                                "Logs de auditoría del usuario obtenidos exitosamente"));
        }

        @GetMapping("/entity/{entidad}/{entidadId}")
        @Operation(summary = "Obtener logs de una entidad específica", description = "Obtiene los logs de auditoría de una entidad específica")
        @RequirePermission({ "VIEW_REPORTS", "ALL_PERMISSIONS" })
        public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogsForEntity(
                        @PathVariable @Parameter(description = "Nombre de la entidad") String entidad,
                        @PathVariable @Parameter(description = "ID de la entidad") Long entidadId) {

                List<AuditLog> auditLogs = auditService.getAuditLogsForEntity(entidad, entidadId);

                return ResponseEntity.ok(ApiResponse.success(
                                auditLogs,
                                "Logs de auditoría de la entidad obtenidos exitosamente"));
        }
}