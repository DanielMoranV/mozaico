package com.djasoft.mozaico.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para validar que las operaciones se realicen en el contexto de la empresa del usuario.
 * Verifica que el usuario autenticado pertenezca a la empresa de los datos que está manipulando.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireCompanyContext {
    /**
     * Nombre del parámetro que contiene el ID de la empresa a validar.
     * Si no se especifica, intentará buscar automáticamente.
     */
    String parameterName() default "";

    /**
     * Mensaje personalizado si la validación falla.
     */
    String message() default "No tienes acceso a datos de esta empresa";
}