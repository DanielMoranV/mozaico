package com.djasoft.mozaico.domain.enums.empresa;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum que define el tipo de operación de la empresa
 * Determina qué tipo de comprobantes puede emitir
 */
@Getter
@RequiredArgsConstructor
public enum TipoOperacion {
    TICKET_SIMPLE("Solo tickets internos sin valor tributario"),
    BOLETA_MANUAL("Boletas manuales sin facturación electrónica"), 
    FACTURACION_ELECTRONICA("Facturación electrónica completa SUNAT"),
    MIXTO("Tickets internos + Comprobantes electrónicos");
    
    private final String descripcion;
}