package com.djasoft.mozaico.security.aspects;

import com.djasoft.mozaico.security.annotations.Auditable;
import com.djasoft.mozaico.services.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @AfterReturning(value = "@annotation(auditable)", returning = "result")
    public void auditSuccessfulAction(JoinPoint joinPoint, Auditable auditable, Object result) {
        try {
            String descripcion = buildDescription(auditable, joinPoint, true);
            Object detalles = null;

            if (auditable.includeParameters()) {
                detalles = createParameterDetails(joinPoint);
            }

            // Intentar extraer ID de la entidad del resultado o parámetros
            Long entidadId = extractEntityId(result, joinPoint.getArgs());

            auditService.logAction(
                    auditable.action(),
                    auditable.entity(),
                    entidadId,
                    descripcion,
                    detalles
            );

        } catch (Exception e) {
            log.error("Error en auditoría de acción exitosa", e);
        }
    }

    @AfterThrowing(value = "@annotation(auditable)", throwing = "ex")
    public void auditFailedAction(JoinPoint joinPoint, Auditable auditable, Throwable ex) {
        try {
            String descripcion = buildDescription(auditable, joinPoint, false);
            Long entidadId = extractEntityId(null, joinPoint.getArgs());

            auditService.logFailedAction(
                    auditable.action(),
                    auditable.entity(),
                    entidadId,
                    descripcion,
                    ex.getMessage()
            );

        } catch (Exception e) {
            log.error("Error en auditoría de acción fallida", e);
        }
    }

    private String buildDescription(Auditable auditable, JoinPoint joinPoint, boolean successful) {
        StringBuilder desc = new StringBuilder();

        if (!auditable.description().isEmpty()) {
            desc.append(auditable.description());
        } else {
            desc.append(successful ? "Acción completada: " : "Acción fallida: ")
                .append(auditable.action())
                .append(" en ")
                .append(auditable.entity());
        }

        desc.append(" - Método: ").append(joinPoint.getSignature().getName());

        return desc.toString();
    }

    private Map<String, Object> createParameterDetails(JoinPoint joinPoint) {
        Map<String, Object> details = new HashMap<>();
        details.put("metodo", joinPoint.getSignature().getName());
        details.put("clase", joinPoint.getTarget().getClass().getSimpleName());
        details.put("parametros", Arrays.toString(joinPoint.getArgs()));
        return details;
    }

    private Long extractEntityId(Object result, Object[] args) {
        // Intentar extraer ID del resultado
        if (result != null) {
            Long id = extractIdFromObject(result);
            if (id != null) return id;
        }

        // Intentar extraer ID de los parámetros
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
            Long id = extractIdFromObject(arg);
            if (id != null) return id;
        }

        return null;
    }

    private Long extractIdFromObject(Object obj) {
        if (obj == null) return null;

        try {
            // Intentar obtener id
            var field = obj.getClass().getDeclaredField("id");
            field.setAccessible(true);
            Object value = field.get(obj);
            if (value instanceof Long) return (Long) value;
        } catch (Exception e) {
            // Ignorar
        }

        try {
            // Intentar obtener idXXX donde XXX es el nombre de la entidad
            for (var field : obj.getClass().getDeclaredFields()) {
                if (field.getName().startsWith("id") && field.getType() == Long.class) {
                    field.setAccessible(true);
                    Object value = field.get(obj);
                    if (value instanceof Long) return (Long) value;
                }
            }
        } catch (Exception e) {
            // Ignorar
        }

        return null;
    }
}