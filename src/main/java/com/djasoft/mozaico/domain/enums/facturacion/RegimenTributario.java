package com.djasoft.mozaico.domain.enums.facturacion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum que define los regímenes tributarios disponibles en Perú
 */
@Getter
@RequiredArgsConstructor
public enum RegimenTributario {
    GENERAL("Régimen General"),
    ESPECIAL("Régimen Especial de Renta"),
    MYPE("Régimen MYPE Tributario"),
    NUEVO_RUS("Nuevo Régimen Único Simplificado"),
    AGRARIO("Régimen Agrario"),
    AMAZONICO("Régimen de la Amazonía");
    
    private final String descripcion;
}