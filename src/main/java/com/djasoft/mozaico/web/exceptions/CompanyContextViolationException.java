package com.djasoft.mozaico.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class CompanyContextViolationException extends RuntimeException {
    public CompanyContextViolationException(String message) {
        super(message);
    }

    public CompanyContextViolationException(String message, Throwable cause) {
        super(message, cause);
    }
}