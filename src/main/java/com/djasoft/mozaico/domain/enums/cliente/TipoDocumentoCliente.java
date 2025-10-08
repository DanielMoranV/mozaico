package com.djasoft.mozaico.domain.enums.cliente;

/**
 * Tipos de documento de identidad según SUNAT (Perú)
 */
public enum TipoDocumentoCliente {
    // Persona Natural
    DNI("DNI", "Documento Nacional de Identidad", 8, TipoPersona.NATURAL, "1"),
    CARNET_EXTRANJERIA("Carnet de Extranjería", "Carnet de Extranjería", 12, TipoPersona.NATURAL, "4"),
    PASAPORTE("Pasaporte", "Pasaporte", 12, TipoPersona.NATURAL, "7"),

    // Persona Jurídica
    RUC("RUC", "Registro Único de Contribuyentes", 11, TipoPersona.JURIDICA, "6"),

    // Sin documento (consumidor final)
    SIN_DOCUMENTO("Sin Documento", "Cliente sin documento", 0, TipoPersona.NATURAL, "0");

    private final String displayName;
    private final String descripcion;
    private final int longitudEsperada;
    private final TipoPersona tipoPersona;
    private final String codigoSunat; // Código según tabla 2 de SUNAT

    TipoDocumentoCliente(String displayName, String descripcion, int longitudEsperada,
                         TipoPersona tipoPersona, String codigoSunat) {
        this.displayName = displayName;
        this.descripcion = descripcion;
        this.longitudEsperada = longitudEsperada;
        this.tipoPersona = tipoPersona;
        this.codigoSunat = codigoSunat;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getLongitudEsperada() {
        return longitudEsperada;
    }

    public TipoPersona getTipoPersona() {
        return tipoPersona;
    }

    public String getCodigoSunat() {
        return codigoSunat;
    }

    public boolean isPersonaJuridica() {
        return tipoPersona == TipoPersona.JURIDICA;
    }

    public boolean isPersonaNatural() {
        return tipoPersona == TipoPersona.NATURAL;
    }

    /**
     * Valida que el número de documento tenga la longitud correcta
     */
    public boolean validarLongitud(String numeroDocumento) {
        if (this == SIN_DOCUMENTO) return true;
        if (numeroDocumento == null) return false;
        return numeroDocumento.length() == longitudEsperada;
    }
}