package com.djasoft.mozaico.domain.enums.comprobante;

public enum TipoComprobante {
    TICKET_INTERNO,           // Para empresas informales
    BOLETA_VENTA,            // Para empresas con RUC
    FACTURA,                 // Para empresas con facturación electrónica
    NOTA_CREDITO,            // Para devoluciones
    NOTA_DEBITO              // Para cargos adicionales
}