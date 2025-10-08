package com.djasoft.mozaico.domain.enums.facturacion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum que define el tipo de contribuyente según SUNAT
 */
@Getter
@RequiredArgsConstructor
public enum TipoContribuyente {
    PERSONA_NATURAL("Persona Natural"),
    PERSONA_JURIDICA("Persona Jurídica"),
    ENTIDAD_PUBLICA("Entidad Pública");
    
    private final String descripcion;
}