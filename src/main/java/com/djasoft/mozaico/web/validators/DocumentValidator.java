package com.djasoft.mozaico.web.validators;

import com.djasoft.mozaico.domain.enums.usuario.TipoDocumentoIdentidad;
import com.djasoft.mozaico.web.dtos.DocumentInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DocumentValidator implements ConstraintValidator<ValidDocument, DocumentInfo> {

    @Override
    public boolean isValid(DocumentInfo dto, ConstraintValidatorContext context) {
        TipoDocumentoIdentidad tipo = dto.getTipoDocumentoIdentidad();
        String numero = dto.getNumeroDocumento();

        if (tipo == null || tipo == TipoDocumentoIdentidad.OTROS) {
            return true;
        }

        if (numero == null || numero.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("El número de documento es obligatorio para el tipo " + tipo)
                    .addPropertyNode("numeroDocumento")
                    .addConstraintViolation();
            return false;
        }

        boolean isValid;
        String message = "";

        switch (tipo) {
            case DNI:
                isValid = numero.matches("\\d{8}");
                message = "El DNI debe contener exactamente 8 dígitos numéricos.";
                break;

            case CARNE_EXTRANJERIA:
                isValid = numero.matches("[a-zA-Z0-9]{9,12}");
                message = "El Carné de Extranjería debe ser alfanumérico y tener entre 9 y 12 caracteres.";
                break;

            case PASAPORTE:
                isValid = numero.matches("[a-zA-Z0-9]{5,15}");
                message = "El Pasaporte debe ser alfanumérico y tener entre 5 y 15 caracteres.";
                break;

            default:
                isValid = true;
                break;
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode("numeroDocumento")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
