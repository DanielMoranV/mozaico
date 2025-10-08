package com.djasoft.mozaico.security.aspects;

import com.djasoft.mozaico.config.JwtAuthenticationFilter;
import com.djasoft.mozaico.domain.entities.Usuario;
import com.djasoft.mozaico.security.annotations.RequireCompanyContext;
import com.djasoft.mozaico.security.annotations.RequirePermission;
import com.djasoft.mozaico.web.exceptions.CompanyContextViolationException;
import com.djasoft.mozaico.web.exceptions.InsufficientPermissionsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    @Before("@annotation(requirePermission)")
    public void checkPermissions(JoinPoint joinPoint, RequirePermission requirePermission) {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null) {
            throw new InsufficientPermissionsException("Usuario no autenticado");
        }

        String[] requiredPermissions = requirePermission.value();
        boolean hasPermission = Arrays.stream(requiredPermissions)
                .anyMatch(currentUser::hasPermission);

        if (!hasPermission) {
            log.warn("Usuario {} sin permisos suficientes. Requeridos: {}, Usuario tiene: {}",
                    currentUser.getUsername(),
                    Arrays.toString(requiredPermissions),
                    currentUser.getTipoUsuario().getPermissions());

            throw new InsufficientPermissionsException(requirePermission.message());
        }

        log.debug("Validación de permisos exitosa para usuario: {} - Permisos requeridos: {}",
                currentUser.getUsername(), Arrays.toString(requiredPermissions));
    }

    @Before("@annotation(requireCompanyContext)")
    public void checkCompanyContext(JoinPoint joinPoint, RequireCompanyContext requireCompanyContext) {
        Usuario currentUser = JwtAuthenticationFilter.getCurrentUser();
        if (currentUser == null) {
            throw new CompanyContextViolationException("Usuario no autenticado");
        }

        Long userEmpresaId = currentUser.getEmpresa().getIdEmpresa();

        // Intentar encontrar el ID de empresa en los parámetros
        Long empresaIdFromParams = extractEmpresaIdFromParameters(joinPoint, requireCompanyContext.parameterName());

        if (empresaIdFromParams != null && !userEmpresaId.equals(empresaIdFromParams)) {
            log.warn("Violación de contexto de empresa. Usuario {} (empresa {}) intentó acceder a datos de empresa {}",
                    currentUser.getUsername(), userEmpresaId, empresaIdFromParams);

            throw new CompanyContextViolationException(requireCompanyContext.message());
        }

        log.debug("Validación de contexto de empresa exitosa para usuario: {} - Empresa: {}",
                currentUser.getUsername(), userEmpresaId);
    }

    private Long extractEmpresaIdFromParameters(JoinPoint joinPoint, String parameterName) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        // Si se especifica un nombre de parámetro específico
        if (!parameterName.isEmpty()) {
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].getName().equals(parameterName)) {
                    Object value = args[i];
                    if (value instanceof Long) {
                        return (Long) value;
                    }
                }
            }
        }

        // Buscar automáticamente parámetros que contengan "empresa" en el nombre
        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName().toLowerCase();
            if (paramName.contains("empresa")) {
                Object value = args[i];
                if (value instanceof Long) {
                    return (Long) value;
                }
            }
        }

        // Buscar en objetos que tengan un campo empresaId o idEmpresa
        for (Object arg : args) {
            if (arg != null) {
                try {
                    // Intentar obtener empresaId por reflexión
                    var field = arg.getClass().getDeclaredField("empresaId");
                    field.setAccessible(true);
                    Object value = field.get(arg);
                    if (value instanceof Long) {
                        return (Long) value;
                    }
                } catch (Exception e) {
                    // Ignorar si no existe el campo
                }

                try {
                    // Intentar obtener idEmpresa por reflexión
                    var field = arg.getClass().getDeclaredField("idEmpresa");
                    field.setAccessible(true);
                    Object value = field.get(arg);
                    if (value instanceof Long) {
                        return (Long) value;
                    }
                } catch (Exception e) {
                    // Ignorar si no existe el campo
                }
            }
        }

        return null;
    }
}