package com.djasoft.mozaico.domain.enums.comprobante;

public enum EstadoComprobante {
    GENERADO,                // Comprobante creado
    IMPRESO,                 // Comprobante impreso
    ENVIADO,                 // Enviado por email/WhatsApp
    ANULADO,                 // Comprobante anulado
    ERROR                    // Error en generación
}