package com.djasoft.mozaico.domain.enums.facturacion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum que define el estado de formalización de la empresa ante SUNAT
 */
@Getter
@RequiredArgsConstructor
public enum EstadoFormalizacion {
    SIN_RUC("Sin RUC - Negocio informal"),
    CON_RUC_INACTIVO("Tiene RUC pero sin facturación electrónica"),
    CON_RUC_ACTIVO("RUC activo con facturación electrónica"),
    EN_TRAMITE("En proceso de formalización");
    
    private final String descripcion;
}