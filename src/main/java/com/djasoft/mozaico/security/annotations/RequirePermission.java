package com.djasoft.mozaico.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para requerir permisos específicos en métodos o clases.
 * Valida que el usuario autenticado tenga al menos uno de los permisos especificados.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
    /**
     * Lista de permisos requeridos. El usuario debe tener al menos uno.
     */
    String[] value();

    /**
     * Mensaje personalizado para la excepción si no tiene permisos.
     */
    String message() default "No tienes permisos suficientes para realizar esta acción";
}