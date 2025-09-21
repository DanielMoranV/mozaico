package com.djasoft.mozaico.web.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DocumentValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDocument {

    String message() default "El número de documento no es válido para el tipo de documento especificado.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
