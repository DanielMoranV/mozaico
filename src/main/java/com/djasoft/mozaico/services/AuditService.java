package com.djasoft.mozaico.services;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.domain.entities.AuditLog;
import com.djasoft.mozaico.domain.entities.Empresa;
import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.domain.repositories.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Async
    public void logAction(String accion, String entidad, Long entidadId, String descripcion) {
        logAction(accion, entidad, entidadId, descripcion, null, true, null);
    }

    @Async
    public void logAction(String accion, String entidad, Long entidadId, String descripcion, Object detallesAdicionales) {
        logAction(accion, entidad, entidadId, descripcion, detallesAdicionales, true, null);
    }

    @Async
    public void logFailedAction(String accion, String entidad, Long entidadId, String descripcion, String mensajeError) {
        logAction(accion, entidad, entidadId, descripcion, null, false, mensajeError);
    }

    @Async
    public void logAction(String accion, String entidad, Long entidadId, String descripcion,
                         Object detallesAdicionales, boolean exitoso, String mensajeError) {
        try {
            Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
            if (currentUser == null) {
                log.warn("Intento de auditoría sin usuario autenticado para acción: {}", accion);
                return;
            }

            // Obtener información de la request
            String ipAddress = "unknown";
            String userAgent = "unknown";

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = getClientIpAddress(request);
                userAgent = request.getHeader("User-Agent");
            }

            // Convertir detalles adicionales a JSON
            String detallesJson = null;
            if (detallesAdicionales != null) {
                try {
                    detallesJson = objectMapper.writeValueAsString(detallesAdicionales);
                } catch (JsonProcessingException e) {
                    log.error("Error al serializar detalles adicionales para auditoría", e);
                    detallesJson = "Error al serializar: " + e.getMessage();
                }
            }

            AuditLog auditLog = AuditLog.builder()
                    .usuario(currentUser)
                    .empresa(currentUser.getEmpresa())
                    .accion(accion)
                    .entidad(entidad)
                    .entidadId(entidadId)
                    .descripcion(descripcion)
                    .detallesAdicionales(detallesJson)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent != null && userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent)
                    .exitoso(exitoso)
                    .mensajeError(mensajeError)
                    .build();

            auditLogRepository.save(auditLog);

            log.debug("Acción auditada: {} - {} - Usuario: {} - Empresa: {}",
                    accion, entidad, currentUser.getUsername(), currentUser.getEmpresa().getIdEmpresa());

        } catch (Exception e) {
            log.error("Error al registrar auditoría para acción: {} - entidad: {}", accion, entidad, e);
        }
    }

    @Async
    public void logUserAction(Usuario usuario, Empresa empresa, String accion, String descripcion,
                             String ipAddress, String userAgent) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .usuario(usuario)
                    .empresa(empresa)
                    .accion(accion)
                    .entidad("Usuario")
                    .entidadId(usuario.getIdUsuario())
                    .descripcion(descripcion)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent != null && userAgent.length() > 500 ? userAgent.substring(0, 500) : userAgent)
                    .exitoso(true)
                    .build();

            auditLogRepository.save(auditLog);

        } catch (Exception e) {
            log.error("Error al registrar auditoría de usuario: {}", usuario.getUsername(), e);
        }
    }

    public Page<AuditLog> getAuditLogsForCurrentCompany(Pageable pageable) {
        Long empresaId = JwtAuthenticationFilter.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("No hay empresa en el contexto actual");
        }
        return auditLogRepository.findByEmpresaIdOrderByFechaHoraDesc(empresaId, pageable);
    }

    public Page<AuditLog> getAuditLogsForUser(Long usuarioId, Pageable pageable) {
        Long empresaId = JwtAuthenticationFilter.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("No hay empresa en el contexto actual");
        }
        return auditLogRepository.findByUsuarioIdAndEmpresaIdOrderByFechaHoraDesc(usuarioId, empresaId, pageable);
    }

    public List<AuditLog> getAuditLogsForEntity(String entidad, Long entidadId) {
        Long empresaId = JwtAuthenticationFilter.getCurrentEmpresaId();
        if (empresaId == null) {
            throw new IllegalStateException("No hay empresa en el contexto actual");
        }
        return auditLogRepository.findByEntidadAndEntidadIdAndEmpresaIdOrderByFechaHoraDesc(entidad, entidadId, empresaId);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null || xForwardedForHeader.isEmpty() || "unknown".equalsIgnoreCase(xForwardedForHeader)) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0].trim();
        }
    }

    public Map<String, Object> createAuditDetails(Object oldValue, Object newValue) {
        Map<String, Object> details = new HashMap<>();
        details.put("valorAnterior", oldValue);
        details.put("valorNuevo", newValue);
        details.put("timestamp", System.currentTimeMillis());
        return details;
    }

    public Map<String, Object> createAuditDetails(Map<String, Object> customDetails) {
        Map<String, Object> details = new HashMap<>(customDetails);
        details.put("timestamp", System.currentTimeMillis());
        return details;
    }
}