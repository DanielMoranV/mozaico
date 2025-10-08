package com.djasoft.mozaico.services.impl;

import com.djasoft.mozaico.services.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class QRCodeServiceImpl implements QRCodeService {

    @Override
    public byte[] generateQRCode(String url, int width, int height) throws WriterException, IOException {
        log.info("Generando código QR para URL: {}", url);

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // Configuración del QR
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, 1); // Margen reducido

        // Generar matriz de bits del QR
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, width, height, hints);

        // Convertir a imagen PNG
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        log.info("Código QR generado exitosamente. Tamaño: {} bytes", outputStream.size());
        return outputStream.toByteArray();
    }

    @Override
    public byte[] generateCartaQRCode(String slugEmpresa, String baseUrl) throws Exception {
        // Construir URL completa de la carta digital
        String cartaUrl = String.format("%s/carta/%s", baseUrl, slugEmpresa);

        log.info("Generando QR de carta digital para empresa '{}': {}", slugEmpresa, cartaUrl);

        // Generar QR de 400x400 píxeles (buen tamaño para imprimir)
        return generateQRCode(cartaUrl, 400, 400);
    }
}
