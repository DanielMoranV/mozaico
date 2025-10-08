package com.djasoft.mozaico.services;

/**
 * Servicio para generación de códigos QR
 */
public interface QRCodeService {

    /**
     * Genera un código QR para la URL especificada
     *
     * @param url URL que contendrá el código QR
     * @param width Ancho del QR en píxeles
     * @param height Alto del QR en píxeles
     * @return Bytes de la imagen PNG del código QR
     * @throws Exception Si hay un error al generar el QR
     */
    byte[] generateQRCode(String url, int width, int height) throws Exception;

    /**
     * Genera un código QR para la carta digital de una empresa
     *
     * @param slugEmpresa Slug único de la empresa
     * @param baseUrl URL base del frontend (ej: http://localhost:5173)
     * @return Bytes de la imagen PNG del código QR
     * @throws Exception Si hay un error al generar el QR
     */
    byte[] generateCartaQRCode(String slugEmpresa, String baseUrl) throws Exception;
}
