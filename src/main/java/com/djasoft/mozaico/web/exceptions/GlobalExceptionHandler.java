package com.djasoft.mozaico.web.exceptions;

import com.djasoft.mozaico.web.dtos.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceConflictException(ResourceConflictException ex) {
        ApiResponse<?> errorResponse = ApiResponse.error(
                HttpStatus.CONFLICT.value(),
                "Conflicto de recursos",
                ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<?> errorResponse = ApiResponse.error(
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        ApiResponse<?> errorResponse = ApiResponse.error(
                HttpStatus.NOT_FOUND.value(),
                "La ruta de la API no fue encontrada",
                String.format("La ruta '%s' no existe.", ex.getRequestURL()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiResponse<?> errorResponse = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación",
                errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String message = "Error de integridad de datos. Verifique que los valores no estén duplicados.";
        if (ex.getMostSpecificCause().getMessage().contains("unique constraint")
                || ex.getMostSpecificCause().getMessage().contains("llave duplicada")) {
            // You can parse the detailed message to find which constraint was violated
            // For now, a generic message is provided.
            message = "Ya existe un registro con el mismo nombre de usuario, email o número de documento.";
        }

        ApiResponse<?> errorResponse = ApiResponse.error(
                HttpStatus.BAD_REQUEST.value(),
                "Datos duplicados",
                message);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneralException(Exception ex) {
        // Log the exception here for debugging purposes
        // log.error("An unexpected error occurred: ", ex);

        ApiResponse<?> errorResponse = ApiResponse.error(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocurrió un error inesperado en el servidor",
                ex.getMessage() // In production, you might want to hide the raw message
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
