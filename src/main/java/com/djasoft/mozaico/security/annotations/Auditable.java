package com.djasoft.mozaico.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para marcar métodos que deben ser auditados.
 * Registra automáticamente las acciones realizadas por el usuario.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    /**
     * Acción que se está realizando (CREATE, UPDATE, DELETE, etc.)
     */
    String action();

    /**
     * Entidad sobre la que se realiza la acción.
     */
    String entity();

    /**
     * Descripción adicional de la operación.
     */
    String description() default "";

    /**
     * Si debe incluir detalles de los parámetros en el log.
     */
    boolean includeParameters() default false;
}