package com.djasoft.mozaico.domain.enums.cliente;

/**
 * Tipo de persona según normativa SUNAT (Perú)
 */
public enum TipoPersona {
    NATURAL("Persona Natural", "Para consumidores finales"),
    JURIDICA("Persona Jurídica", "Para empresas con RUC");

    private final String displayName;
    private final String descripcion;

    TipoPersona(String displayName, String descripcion) {
        this.displayName = displayName;
        this.descripcion = descripcion;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescripcion() {
        return descripcion;
    }
}