package com.djasoft.mozaico.application.services.impl;

import com.djasoft.mozaico.application.dtos.empresa.ValidacionIgvResponseDto;
import com.djasoft.mozaico.application.services.ComprobanteService;
import com.djasoft.mozaico.application.services.EmpresaValidacionService;
import com.djasoft.mozaico.domain.entities.Comprobante;
import com.djasoft.mozaico.domain.entities.DatosFacturacion;
import com.djasoft.mozaico.domain.entities.Empresa;
import com.djasoft.mozaico.domain.entities.Pago;
import com.djasoft.mozaico.domain.enums.comprobante.EstadoComprobante;
import com.djasoft.mozaico.domain.enums.comprobante.TipoComprobante;
import com.djasoft.mozaico.domain.repositories.ComprobanteRepository;
import com.djasoft.mozaico.web.dtos.ComprobanteResponseDTO;
import com.djasoft.mozaico.web.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// iText imports
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.io.image.ImageDataFactory;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprobanteServiceImpl implements ComprobanteService {

    private final ComprobanteRepository comprobanteRepository;
    private final EmpresaValidacionService empresaValidacionService;
    private final com.djasoft.mozaico.services.DetallePedidoService detallePedidoService;
    private final com.djasoft.mozaico.domain.repositories.EmpresaRepository empresaRepository;
    private final com.djasoft.mozaico.domain.repositories.DatosFacturacionRepository datosFacturacionRepository;

    @Value("${app.comprobantes.directorio:/uploads/comprobantes}")
    private String directorioComprobantes;

    @Value("${app.logo.default:classpath:static/images/default-logo.png}")
    private String logoDefault;

    @Override
    @Transactional
    public Comprobante generarComprobanteAutomatico(Pago pago) {
        log.info("Iniciando generación automática de comprobante para pago ID: {}", pago.getIdPago());

        // Obtener configuración de empresa para determinar tipo de comprobante
        ValidacionIgvResponseDto validacion = empresaValidacionService.validarConfiguracionIgv();

        TipoComprobante tipoComprobante = determinarTipoComprobante(validacion);

        return generarComprobante(pago, tipoComprobante);
    }

    @Override
    @Transactional
    public Comprobante generarComprobante(Pago pago, TipoComprobante tipoComprobante) {
        log.info("Generando comprobante tipo {} para pago ID: {}", tipoComprobante, pago.getIdPago());

        // Obtener empresa activa
        Empresa empresa = empresaRepository.findByActivaTrue()
                .orElseThrow(() -> new IllegalStateException("No se encontró empresa activa"));

        // Generar número secuencial y serie
        String numeroComprobante = generarNumeroSecuencial(empresa, tipoComprobante);
        String serieComprobante = generarSerieComprobante(empresa, tipoComprobante);

        // Crear comprobante
        Comprobante comprobante = Comprobante.builder()
                .pago(pago)
                .tipoComprobante(tipoComprobante)
                .numeroComprobante(numeroComprobante)
                .serieComprobante(serieComprobante)
                .fechaEmision(LocalDateTime.now())
                .estado(EstadoComprobante.GENERADO)
                .hashVerificacion(generarHashVerificacion(pago))
                .build();

        Comprobante comprobanteGuardado = comprobanteRepository.save(comprobante);

        // Generar archivos
        try {
            crearDirectorioSiNoExiste();

            String rutaTicket = generarTicket(comprobanteGuardado);
            String rutaPdf = generarPDF(comprobanteGuardado);

            comprobanteGuardado.setRutaArchivoTicket(rutaTicket);
            comprobanteGuardado.setRutaArchivoPdf(rutaPdf);

            comprobanteRepository.save(comprobanteGuardado);

            log.info("Comprobante generado exitosamente ID: {}", comprobanteGuardado.getIdComprobante());

        } catch (IOException e) {
            log.error("Error al generar archivos del comprobante: {}", e.getMessage());
            comprobanteGuardado.setEstado(EstadoComprobante.ERROR);
            comprobanteGuardado.setObservaciones("Error en generación: " + e.getMessage());
            comprobanteRepository.save(comprobanteGuardado);
        }

        return comprobanteGuardado;
    }

    @Override
    public String generarTicket(Comprobante comprobante) throws IOException {
        log.info("Generando ticket PDF para comprobante ID: {}", comprobante.getIdComprobante());

        String nombreArchivo = String.format("ticket_%s_%s.pdf",
                comprobante.getNumeroComprobante(),
                System.currentTimeMillis());

        Path rutaArchivo = Paths.get(directorioComprobantes, nombreArchivo);
        Pago pago = comprobante.getPago();

        // Validar datos necesarios
        if (pago == null || pago.getPedido() == null) {
            throw new IllegalArgumentException("Datos de pago o pedido no válidos para generar ticket");
        }

        // Obtener información de empresa
        ValidacionIgvResponseDto validacion = empresaValidacionService.validarConfiguracionIgv();

        PdfWriter writer = null;
        PdfDocument pdfDoc = null;
        Document document = null;

        try {
            // Crear PDF en formato ticket térmico (80mm = 226.77 puntos)
            writer = new PdfWriter(rutaArchivo.toFile());
            pdfDoc = new PdfDocument(writer);

            // Tamaño personalizado para ticket térmico 80mm de ancho, altura variable
            pdfDoc.setDefaultPageSize(new PageSize(226.77f, 841.89f)); // 80mm x 297mm
            document = new Document(pdfDoc);
            document.setMargins(10, 10, 10, 10);

            // Configurar fuentes pequeñas para ticket
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // LOGO (si existe)
            Image logo = obtenerLogoEmpresa(validacion);
            if (logo != null) {
                logo.setWidth(60);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                logo.setMarginBottom(5);
                document.add(logo);
            }

            // ENCABEZADO
            Paragraph empresaNombre = new Paragraph(validacion.getNombreEmpresa())
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setMarginBottom(2);
            document.add(empresaNombre);

            if (validacion.getTieneRuc()) {
                Paragraph ruc = new Paragraph("RUC: " + validacion.getRuc())
                        .setFont(normalFont)
                        .setFontSize(8)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setMarginBottom(2);
                document.add(ruc);
            }

            // Línea separadora
            Paragraph linea1 = new Paragraph("========================================")
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(linea1);

            // TIPO Y NÚMERO
            Paragraph tipo = new Paragraph(comprobante.getTipoComprobante().toString())
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(2);
            document.add(tipo);

            Paragraph numero = new Paragraph(comprobante.getSerieComprobante() + " - " + comprobante.getNumeroComprobante())
                    .setFont(boldFont)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(numero);

            // Línea separadora
            Paragraph linea2 = new Paragraph("========================================")
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(linea2);

            // INFORMACIÓN DEL PEDIDO
            Paragraph fecha = new Paragraph("FECHA: " +
                    comprobante.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setMarginBottom(2);
            document.add(fecha);

            if (pago.getPedido().getMesa() != null) {
                Paragraph mesa = new Paragraph("MESA: " + pago.getPedido().getMesa().getNumeroMesa())
                        .setFont(normalFont)
                        .setFontSize(8)
                        .setMarginBottom(2);
                document.add(mesa);
            }

            if (pago.getPedido().getCliente() != null) {
                Paragraph cliente = new Paragraph("CLIENTE: " +
                        pago.getPedido().getCliente().getNombre() + " " +
                        pago.getPedido().getCliente().getApellido())
                        .setFont(normalFont)
                        .setFontSize(8)
                        .setMarginBottom(2);
                document.add(cliente);
            }

            Paragraph empleado = new Paragraph("ATENDIDO POR: " + pago.getPedido().getEmpleado().getNombre())
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setMarginBottom(5);
            document.add(empleado);

            // Línea separadora
            Paragraph linea3 = new Paragraph("========================================")
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(linea3);

            // DETALLE DEL CONSUMO
            Paragraph detalleTitle = new Paragraph("DETALLE DEL CONSUMO:")
                    .setFont(boldFont)
                    .setFontSize(8)
                    .setMarginBottom(3);
            document.add(detalleTitle);

            // Productos (sin tabla, solo texto formateado)
            Document finalDocument = document;
            detallePedidoService.obtenerTodosLosDetallesPorPedido(pago.getPedido().getIdPedido())
                    .forEach(detalleDTO -> {
                        String producto = truncarTexto(detalleDTO.getProducto().getNombre(), 22);
                        Paragraph prodNombre = new Paragraph(producto)
                                .setFont(normalFont)
                                .setFontSize(8)
                                .setMarginBottom(1);
                        finalDocument.add(prodNombre);

                        Paragraph prodDetalle = new Paragraph(String.format("  %d x S/ %.2f = S/ %.2f",
                                detalleDTO.getCantidad(),
                                detalleDTO.getPrecioUnitario(),
                                detalleDTO.getSubtotal()))
                                .setFont(normalFont)
                                .setFontSize(7)
                                .setMarginBottom(3);
                        finalDocument.add(prodDetalle);
                    });

            // Línea separadora
            Paragraph linea4 = new Paragraph("----------------------------------------")
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(3);
            document.add(linea4);

            // TOTALES
            Paragraph subtotal = new Paragraph(String.format("SUBTOTAL:           S/ %.2f",
                    pago.getPedido().getSubtotal()))
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setMarginBottom(2);
            document.add(subtotal);

            if (validacion.getAplicaIgv()) {
                Paragraph igv = new Paragraph(String.format("IGV (%.1f%%):          S/ %.2f",
                        validacion.getPorcentajeIgv(),
                        pago.getPedido().getImpuestos()))
                        .setFont(normalFont)
                        .setFontSize(8)
                        .setMarginBottom(2);
                document.add(igv);
            }

            if (pago.getPedido().getDescuento().compareTo(java.math.BigDecimal.ZERO) > 0) {
                Paragraph descuento = new Paragraph(String.format("DESCUENTO:          S/ %.2f",
                        pago.getPedido().getDescuento()))
                        .setFont(normalFont)
                        .setFontSize(8)
                        .setMarginBottom(2);
                document.add(descuento);
            }

            // Línea separadora
            Paragraph linea5 = new Paragraph("========================================")
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(3);
            document.add(linea5);

            // TOTAL
            Paragraph total = new Paragraph(String.format("TOTAL:              S/ %.2f",
                    pago.getPedido().getTotal()))
                    .setFont(boldFont)
                    .setFontSize(10)
                    .setBold()
                    .setMarginBottom(5);
            document.add(total);

            // Línea separadora
            Paragraph linea6 = new Paragraph("========================================")
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(3);
            document.add(linea6);

            // FORMA DE PAGO
            Paragraph pagoTitle = new Paragraph("FORMA DE PAGO:")
                    .setFont(boldFont)
                    .setFontSize(8)
                    .setMarginBottom(2);
            document.add(pagoTitle);

            Paragraph pagoMetodo = new Paragraph(String.format("%s: S/ %.2f",
                    pago.getMetodoPago().getNombre(),
                    pago.getMonto()))
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setMarginBottom(2);
            document.add(pagoMetodo);

            if (pago.getReferencia() != null) {
                Paragraph ref = new Paragraph("REF: " + pago.getReferencia())
                        .setFont(normalFont)
                        .setFontSize(7)
                        .setMarginBottom(5);
                document.add(ref);
            }

            // Línea separadora
            Paragraph linea7 = new Paragraph("========================================")
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(3);
            document.add(linea7);

            // MENSAJE DE EMPRESA
            Paragraph mensaje = new Paragraph(validacion.getMensajeCliente())
                    .setFont(normalFont)
                    .setFontSize(7)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic()
                    .setMarginBottom(5);
            document.add(mensaje);

            // AGRADECIMIENTO
            Paragraph agradecimiento = new Paragraph("¡GRACIAS POR SU PREFERENCIA!")
                    .setFont(boldFont)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setMarginBottom(3);
            document.add(agradecimiento);

            // HASH
            Paragraph hash = new Paragraph("Hash: " + comprobante.getHashVerificacion().substring(0, 12) + "...")
                    .setFont(normalFont)
                    .setFontSize(6)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            document.add(hash);

            // Cerrar documento
            document.close();

            log.info("Ticket PDF generado exitosamente: {}", rutaArchivo.toString());
            return rutaArchivo.toString();

        } catch (Exception e) {
            log.error("Error generando ticket PDF: {}", e.getMessage(), e);

            // Cerrar recursos y eliminar archivo corrupto
            try {
                if (document != null) {
                    document.close();
                } else if (pdfDoc != null) {
                    pdfDoc.close();
                } else if (writer != null) {
                    writer.close();
                }

                // Eliminar archivo corrupto si existe
                Files.deleteIfExists(rutaArchivo);
                log.info("Archivo ticket PDF corrupto eliminado: {}", rutaArchivo);
            } catch (Exception cleanupEx) {
                log.error("Error al limpiar recursos: {}", cleanupEx.getMessage());
            }

            throw new IOException("Error generando ticket PDF: " + e.getMessage(), e);
        }
    }

    @Override
    public String generarPDF(Comprobante comprobante) {
        log.info("Generando PDF para comprobante ID: {}", comprobante.getIdComprobante());

        String nombreArchivo = String.format("comprobante_%s_%s.pdf",
                comprobante.getNumeroComprobante().replace("/", "-"),
                System.currentTimeMillis());

        Path rutaArchivo = Paths.get(directorioComprobantes, nombreArchivo);
        Pago pago = comprobante.getPago();

        // Validar datos necesarios antes de crear el PDF
        if (pago == null || pago.getPedido() == null) {
            throw new IllegalArgumentException("Datos de pago o pedido no válidos para generar PDF");
        }

        // Obtener información de empresa
        ValidacionIgvResponseDto validacion = empresaValidacionService.validarConfiguracionIgv();

        PdfWriter writer = null;
        PdfDocument pdfDoc = null;
        Document document = null;

        try {
            // Crear documento PDF con iText
            writer = new PdfWriter(rutaArchivo.toFile());
            pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            document = new Document(pdfDoc);

            // Configurar fuentes
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // LOGO (si existe)
            Image logo = obtenerLogoEmpresa(validacion);
            if (logo != null) {
                logo.setWidth(120);
                logo.setHorizontalAlignment(HorizontalAlignment.CENTER);
                logo.setMarginBottom(10);
                document.add(logo);
            }

            // ENCABEZADO - Nombre de empresa
            Paragraph header = new Paragraph(validacion.getNombreEmpresa())
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            document.add(header);

            // RUC si existe
            if (validacion.getTieneRuc()) {
                Paragraph ruc = new Paragraph("RUC: " + validacion.getRuc())
                        .setFont(normalFont)
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER);
                document.add(ruc);
            }

            // Línea separadora
            document.add(new Paragraph("\n"));

            // TIPO Y NÚMERO DE COMPROBANTE
            Paragraph tipoComprobante = new Paragraph(comprobante.getTipoComprobante().toString())
                    .setFont(boldFont)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setPadding(5);
            document.add(tipoComprobante);

            Paragraph numeroComprobante = new Paragraph(
                    String.format("%s - %s",
                            comprobante.getSerieComprobante(),
                            comprobante.getNumeroComprobante()))
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(numeroComprobante);

            document.add(new Paragraph("\n"));

            // INFORMACIÓN DEL COMPROBANTE
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}))
                    .setWidth(UnitValue.createPercentValue(100));

            infoTable.addCell(createCell("Fecha:", boldFont, false));
            infoTable.addCell(createCell(
                    comprobante.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    normalFont, false));

            if (pago.getPedido().getMesa() != null) {
                infoTable.addCell(createCell("Mesa:", boldFont, false));
                infoTable.addCell(createCell(
                        String.valueOf(pago.getPedido().getMesa().getNumeroMesa()),
                        normalFont, false));
            }

            if (pago.getPedido().getCliente() != null) {
                infoTable.addCell(createCell("Cliente:", boldFont, false));
                infoTable.addCell(createCell(
                        pago.getPedido().getCliente().getNombre() + " " +
                        pago.getPedido().getCliente().getApellido(),
                        normalFont, false));
            }

            infoTable.addCell(createCell("Atendido por:", boldFont, false));
            infoTable.addCell(createCell(
                    pago.getPedido().getEmpleado().getNombre(),
                    normalFont, false));

            document.add(infoTable);
            document.add(new Paragraph("\n"));

            // DETALLE DEL CONSUMO
            Paragraph detalleTitle = new Paragraph("DETALLE DEL CONSUMO")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setPadding(5);
            document.add(detalleTitle);

            // Tabla de productos
            Table productTable = new Table(UnitValue.createPercentArray(new float[]{3, 1, 2, 2}))
                    .setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            productTable.addHeaderCell(createCell("Producto", boldFont, true));
            productTable.addHeaderCell(createCell("Cant.", boldFont, true));
            productTable.addHeaderCell(createCell("P. Unit.", boldFont, true));
            productTable.addHeaderCell(createCell("Subtotal", boldFont, true));

            // Productos
            detallePedidoService.obtenerTodosLosDetallesPorPedido(pago.getPedido().getIdPedido())
                    .forEach(detalleDTO -> {
                        productTable.addCell(createCell(detalleDTO.getProducto().getNombre(), normalFont, false));
                        productTable.addCell(createCell(String.valueOf(detalleDTO.getCantidad()), normalFont, false));
                        productTable.addCell(createCell(String.format("S/ %.2f", detalleDTO.getPrecioUnitario()), normalFont, false));
                        productTable.addCell(createCell(String.format("S/ %.2f", detalleDTO.getSubtotal()), normalFont, false));
                    });

            document.add(productTable);
            document.add(new Paragraph("\n"));

            // TOTALES
            Table totalesTable = new Table(UnitValue.createPercentArray(new float[]{2, 1}))
                    .setWidth(UnitValue.createPercentValue(60))
                    .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);

            totalesTable.addCell(createCell("Subtotal:", boldFont, false));
            totalesTable.addCell(createCell(
                    String.format("S/ %.2f", pago.getPedido().getSubtotal()),
                    normalFont, false));

            if (validacion.getAplicaIgv()) {
                totalesTable.addCell(createCell(
                        String.format("IGV (%.1f%%):", validacion.getPorcentajeIgv()),
                        boldFont, false));
                totalesTable.addCell(createCell(
                        String.format("S/ %.2f", pago.getPedido().getImpuestos()),
                        normalFont, false));
            }

            if (pago.getPedido().getDescuento().compareTo(java.math.BigDecimal.ZERO) > 0) {
                totalesTable.addCell(createCell("Descuento:", boldFont, false));
                totalesTable.addCell(createCell(
                        String.format("S/ %.2f", pago.getPedido().getDescuento()),
                        normalFont, false));
            }

            // Total final con fondo
            Cell totalLabelCell = new Cell().add(new Paragraph("TOTAL:"))
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setPadding(5);

            Cell totalValueCell = new Cell().add(new Paragraph(
                    String.format("S/ %.2f", pago.getPedido().getTotal())))
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setPadding(5);

            totalesTable.addCell(totalLabelCell);
            totalesTable.addCell(totalValueCell);

            document.add(totalesTable);
            document.add(new Paragraph("\n"));

            // FORMA DE PAGO
            Paragraph pagoTitle = new Paragraph("FORMA DE PAGO")
                    .setFont(boldFont)
                    .setFontSize(10);
            document.add(pagoTitle);

            Paragraph pagoDetalle = new Paragraph(
                    String.format("%s: S/ %.2f", pago.getMetodoPago().getNombre(), pago.getMonto()))
                    .setFont(normalFont)
                    .setFontSize(10);
            document.add(pagoDetalle);

            if (pago.getReferencia() != null) {
                Paragraph referencia = new Paragraph("Referencia: " + pago.getReferencia())
                        .setFont(normalFont)
                        .setFontSize(8)
                        .setItalic();
                document.add(referencia);
            }

            document.add(new Paragraph("\n"));

            // MENSAJE FINAL
            Paragraph mensaje = new Paragraph(validacion.getMensajeCliente())
                    .setFont(normalFont)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic();
            document.add(mensaje);

            // PIE - Agradecimiento
            Paragraph agradecimiento = new Paragraph("¡GRACIAS POR SU PREFERENCIA!")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20);
            document.add(agradecimiento);

            // Hash de verificación
            Paragraph hash = new Paragraph("Hash: " + comprobante.getHashVerificacion().substring(0, 16) + "...")
                    .setFont(normalFont)
                    .setFontSize(7)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(hash);

            // Cerrar documento
            document.close();

            log.info("PDF generado exitosamente: {}", rutaArchivo.toString());
            return rutaArchivo.toString();

        } catch (Exception e) {
            log.error("Error generando PDF: {}", e.getMessage(), e);

            // Cerrar recursos y eliminar archivo corrupto
            try {
                if (document != null) {
                    document.close();
                } else if (pdfDoc != null) {
                    pdfDoc.close();
                } else if (writer != null) {
                    writer.close();
                }

                // Eliminar archivo corrupto si existe
                Files.deleteIfExists(rutaArchivo);
                log.info("Archivo PDF corrupto eliminado: {}", rutaArchivo);
            } catch (Exception cleanupEx) {
                log.error("Error al limpiar recursos: {}", cleanupEx.getMessage());
            }

            throw new RuntimeException("Error generando PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Método auxiliar para crear celdas con formato
     */
    private Cell createCell(String text, PdfFont font, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(text))
                .setFont(font)
                .setFontSize(isHeader ? 10 : 9)
                .setPadding(5);

        if (isHeader) {
            cell.setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setBold();
        }

        return cell;
    }

    @Override
    public Comprobante obtenerComprobantePorPago(Integer idPago) {
        return comprobanteRepository.findByPagoIdPago(idPago)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comprobante no encontrado para el pago ID: " + idPago));
    }

    @Override
    public Comprobante obtenerComprobantePorId(Integer idComprobante) {
        return comprobanteRepository.findById(idComprobante)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comprobante no encontrado con ID: " + idComprobante));
    }

    @Override
    public List<ComprobanteResponseDTO> obtenerTodosLosComprobantes() {
        log.info("Obteniendo lista de todos los comprobantes");

        List<Comprobante> comprobantes = comprobanteRepository.findAll();

        return comprobantes.stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void reimprimirComprobante(Integer idComprobante) {
        Comprobante comprobante = comprobanteRepository.findById(idComprobante)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comprobante no encontrado con ID: " + idComprobante));

        if (comprobante.getEstado() == EstadoComprobante.ANULADO) {
            throw new IllegalStateException("No se puede reimprimir un comprobante anulado");
        }

        // Verificar si los archivos existen, si no, regenerarlos
        boolean ticketExiste = comprobante.getRutaArchivoTicket() != null &&
                new File(comprobante.getRutaArchivoTicket()).exists();
        boolean pdfExiste = comprobante.getRutaArchivoPdf() != null &&
                new File(comprobante.getRutaArchivoPdf()).exists();

        if (!ticketExiste || !pdfExiste) {
            log.warn("Archivos de comprobante {} no existen, regenerando...", idComprobante);
            try {
                crearDirectorioSiNoExiste();

                if (!ticketExiste) {
                    String rutaTicket = generarTicket(comprobante);
                    comprobante.setRutaArchivoTicket(rutaTicket);
                }

                if (!pdfExiste) {
                    String rutaPdf = generarPDF(comprobante);
                    comprobante.setRutaArchivoPdf(rutaPdf);
                }

                log.info("Archivos regenerados exitosamente para comprobante ID: {}", idComprobante);
            } catch (IOException e) {
                log.error("Error al regenerar archivos del comprobante: {}", e.getMessage());
                throw new RuntimeException("Error al regenerar archivos del comprobante", e);
            }
        }

        // Incrementar contador de impresiones (manejar null para comprobantes existentes)
        Integer contadorActual = comprobante.getContadorImpresiones();
        comprobante.setContadorImpresiones(contadorActual != null ? contadorActual + 1 : 1);
        comprobante.setEstado(EstadoComprobante.IMPRESO);
        comprobanteRepository.save(comprobante);

        log.info("Comprobante reimpreso ID: {}, total impresiones: {}",
                idComprobante, comprobante.getContadorImpresiones());
    }

    @Override
    @Transactional
    public Resource descargarYMarcarImpreso(Integer idComprobante, boolean esImpresionAutomatica) {
        Comprobante comprobante = comprobanteRepository.findById(idComprobante)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comprobante no encontrado con ID: " + idComprobante));

        if (comprobante.getEstado() == EstadoComprobante.ANULADO) {
            throw new IllegalStateException("No se puede descargar un comprobante anulado");
        }

        // Determinar qué archivo usar (ticket o PDF completo)
        String rutaArchivo = esImpresionAutomatica ?
                comprobante.getRutaArchivoTicket() : comprobante.getRutaArchivoPdf();

        if (rutaArchivo == null) {
            throw new ResourceNotFoundException("Archivo de comprobante no disponible");
        }

        File archivo = new File(rutaArchivo);

        // Si el archivo no existe, regenerarlo
        if (!archivo.exists()) {
            log.warn("Archivo de comprobante {} no existe, regenerando...", idComprobante);
            try {
                crearDirectorioSiNoExiste();

                if (esImpresionAutomatica) {
                    rutaArchivo = generarTicket(comprobante);
                    comprobante.setRutaArchivoTicket(rutaArchivo);
                } else {
                    rutaArchivo = generarPDF(comprobante);
                    comprobante.setRutaArchivoPdf(rutaArchivo);
                }

                archivo = new File(rutaArchivo);
            } catch (IOException e) {
                log.error("Error al regenerar archivo del comprobante: {}", e.getMessage());
                throw new RuntimeException("Error al regenerar archivo del comprobante", e);
            }
        }

        // Marcar como impreso y actualizar contador
        if (comprobante.getEstado() == EstadoComprobante.GENERADO) {
            comprobante.setEstado(EstadoComprobante.IMPRESO);
            comprobante.setFechaPrimeraImpresion(LocalDateTime.now());
        }

        // Incrementar contador (manejar null para comprobantes existentes)
        Integer contadorActual = comprobante.getContadorImpresiones();
        comprobante.setContadorImpresiones(contadorActual != null ? contadorActual + 1 : 1);
        comprobanteRepository.save(comprobante);

        log.info("Comprobante {} descargado, impresiones totales: {}",
                idComprobante, comprobante.getContadorImpresiones());

        return new FileSystemResource(archivo);
    }

    @Override
    @Transactional
    public void anularComprobante(Integer idComprobante, String motivo) {
        Comprobante comprobante = comprobanteRepository.findById(idComprobante)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comprobante no encontrado con ID: " + idComprobante));

        // Validaciones
        if (comprobante.getEstado() == EstadoComprobante.ANULADO) {
            throw new IllegalStateException("El comprobante ya está anulado");
        }

        if (comprobante.getEstado() == EstadoComprobante.ERROR) {
            throw new IllegalStateException("No se puede anular un comprobante con error");
        }

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de anulación es obligatorio");
        }

        // Obtener usuario actual (si existe contexto de seguridad)
        String usuarioAnulacion = "SISTEMA";
        try {
            com.djasoft.mozaico.domain.entities.Usuario currentUser =
                    com.djasoft.mozaico.config.JwtAuthenticationFilter.getCurrentUser();
            if (currentUser != null) {
                usuarioAnulacion = currentUser.getNombre();
            }
        } catch (Exception e) {
            log.debug("No se pudo obtener usuario actual para anulación");
        }

        // Anular comprobante
        comprobante.setEstado(EstadoComprobante.ANULADO);
        comprobante.setFechaAnulacion(LocalDateTime.now());
        comprobante.setUsuarioAnulacion(usuarioAnulacion);
        comprobante.setObservaciones("ANULADO: " + motivo);
        comprobanteRepository.save(comprobante);

        log.info("Comprobante anulado ID: {}, motivo: {}, usuario: {}",
                idComprobante, motivo, usuarioAnulacion);
    }

    @Override
    @Transactional
    public void enviarPorEmail(Integer idComprobante, String emailDestino) {
        Comprobante comprobante = comprobanteRepository.findById(idComprobante)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Comprobante no encontrado con ID: " + idComprobante));

        if (comprobante.getEstado() == EstadoComprobante.ANULADO) {
            throw new IllegalStateException("No se puede enviar un comprobante anulado");
        }

        if (emailDestino == null || !emailDestino.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }

        // Verificar que los archivos existen
        if (comprobante.getRutaArchivoPdf() == null ||
            !new File(comprobante.getRutaArchivoPdf()).exists()) {
            throw new IllegalStateException("No se encontró el archivo PDF del comprobante");
        }

        // TODO: Implementar envío de email usando JavaMailSender
        // Por ahora solo actualizamos el estado

        comprobante.setEstado(EstadoComprobante.ENVIADO);
        comprobante.setFechaEnvioDigital(LocalDateTime.now());
        comprobante.setEmailEnvio(emailDestino);
        comprobanteRepository.save(comprobante);

        log.info("Comprobante {} marcado para envío a: {}", idComprobante, emailDestino);
        log.warn("Funcionalidad de envío de email no implementada completamente. " +
                "Requiere configuración de JavaMailSender");
    }

    // Métodos auxiliares
    private TipoComprobante determinarTipoComprobante(ValidacionIgvResponseDto validacion) {
        switch (validacion.getTipoOperacion()) {
            case TICKET_SIMPLE:
                return TipoComprobante.TICKET_INTERNO;
            case BOLETA_MANUAL:
                return TipoComprobante.BOLETA_VENTA;
            case FACTURACION_ELECTRONICA:
                return TipoComprobante.FACTURA;
            default:
                return TipoComprobante.TICKET_INTERNO;
        }
    }

    /**
     * Genera número secuencial y actualiza el correlativo en la base de datos
     * Método sincronizado para evitar duplicados en entornos concurrentes
     */
    private synchronized String generarNumeroSecuencial(Empresa empresa, TipoComprobante tipo) {
        Long correlativo;

        switch (tipo) {
            case TICKET_INTERNO:
                // Usar correlativo de tickets de la empresa
                correlativo = empresa.getCorrelativoTicket();
                empresa.setCorrelativoTicket(correlativo + 1);
                empresaRepository.save(empresa);
                break;

            case BOLETA_VENTA:
                // Usar correlativo de boletas de DatosFacturacion
                DatosFacturacion datosFacturacion = empresa.getDatosFacturacion();
                if (datosFacturacion == null) {
                    throw new IllegalStateException("No se puede emitir boleta sin datos de facturación");
                }
                correlativo = datosFacturacion.getCorrelativoBoleta();
                datosFacturacion.setCorrelativoBoleta(correlativo + 1);
                datosFacturacionRepository.save(datosFacturacion);
                break;

            case FACTURA:
                // Usar correlativo de facturas de DatosFacturacion
                DatosFacturacion datosFacturacionFactura = empresa.getDatosFacturacion();
                if (datosFacturacionFactura == null) {
                    throw new IllegalStateException("No se puede emitir factura sin datos de facturación");
                }
                correlativo = datosFacturacionFactura.getCorrelativoFactura();
                datosFacturacionFactura.setCorrelativoFactura(correlativo + 1);
                datosFacturacionRepository.save(datosFacturacionFactura);
                break;

            default:
                // Para otros tipos, usar correlativo de tickets
                correlativo = empresa.getCorrelativoTicket();
                empresa.setCorrelativoTicket(correlativo + 1);
                empresaRepository.save(empresa);
                break;
        }

        // Formatear a 8 dígitos con ceros a la izquierda
        return String.format("%08d", correlativo);
    }

    /**
     * Obtiene la serie del comprobante según tipo y configuración de la empresa
     */
    private String generarSerieComprobante(Empresa empresa, TipoComprobante tipo) {
        switch (tipo) {
            case TICKET_INTERNO:
                // Usar prefijo configurado en la empresa
                return empresa.getPrefijoTicket() != null ? empresa.getPrefijoTicket() : "TKT";

            case BOLETA_VENTA:
                // Usar serie de boleta de DatosFacturacion
                DatosFacturacion datosFacturacion = empresa.getDatosFacturacion();
                if (datosFacturacion != null && datosFacturacion.getSerieBoleta() != null) {
                    return datosFacturacion.getSerieBoleta();
                }
                return "B001";

            case FACTURA:
                // Usar serie de factura de DatosFacturacion
                DatosFacturacion datosFacturacionFactura = empresa.getDatosFacturacion();
                if (datosFacturacionFactura != null && datosFacturacionFactura.getSerieFactura() != null) {
                    return datosFacturacionFactura.getSerieFactura();
                }
                return "F001";

            default:
                return "DOC";
        }
    }

    private String generarHashVerificacion(Pago pago) {
        String data = pago.getIdPago() + pago.getMonto().toString() +
                pago.getFechaPago().toString();
        return UUID.nameUUIDFromBytes(data.getBytes()).toString();
    }

    private void crearDirectorioSiNoExiste() throws IOException {
        Path directorio = Paths.get(directorioComprobantes);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
            log.info("Directorio de comprobantes creado: {}", directorio);
        }
    }

    private String truncarTexto(String texto, int longitud) {
        if (texto == null)
            return "";
        return texto.length() > longitud ? texto.substring(0, longitud - 3) + "..." : texto;
    }

    /**
     * Obtiene el logo de la empresa o uno por defecto
     */
    private Image obtenerLogoEmpresa(ValidacionIgvResponseDto validacion) {
        try {
            String logoUrl = validacion.getLogoUrl();

            // Si la empresa tiene logo configurado
            if (logoUrl != null && !logoUrl.isEmpty()) {
                // Si es una URL local (inicia con /)
                if (logoUrl.startsWith("/")) {
                    // Convertir ruta relativa a absoluta
                    String rutaAbsoluta = System.getProperty("user.dir") + logoUrl;
                    File logoFile = new File(rutaAbsoluta);

                    if (logoFile.exists() && logoFile.isFile()) {
                        Image logo = new Image(ImageDataFactory.create(rutaAbsoluta));
                        return logo;
                    }
                } else if (logoUrl.startsWith("http://") || logoUrl.startsWith("https://")) {
                    // Si es una URL externa, intentar descargarla
                    try {
                        java.net.URI uri = new java.net.URI(logoUrl);
                        Image logo = new Image(ImageDataFactory.create(uri.toURL()));
                        return logo;
                    } catch (Exception e) {
                        log.warn("No se pudo cargar logo desde URL: {}", logoUrl);
                    }
                }
            }

            // Intentar cargar logo por defecto del classpath
            try {
                var resource = getClass().getClassLoader().getResourceAsStream("static/images/default-logo.png");
                if (resource != null) {
                    byte[] bytes = resource.readAllBytes();
                    resource.close();
                    Image logo = new Image(ImageDataFactory.create(bytes));
                    return logo;
                }
            } catch (Exception e) {
                log.debug("Logo por defecto no encontrado en classpath");
            }

            return null;
        } catch (Exception e) {
            log.error("Error al cargar logo: {}", e.getMessage());
            return null;
        }
    }

    private ComprobanteResponseDTO convertirAResponseDTO(Comprobante comprobante) {
        // Verificar existencia de archivos
        boolean ticketDisponible = comprobante.getRutaArchivoTicket() != null &&
                new File(comprobante.getRutaArchivoTicket()).exists();
        boolean pdfDisponible = comprobante.getRutaArchivoPdf() != null &&
                new File(comprobante.getRutaArchivoPdf()).exists();

        return ComprobanteResponseDTO.builder()
                .idComprobante(comprobante.getIdComprobante())
                .tipoComprobante(comprobante.getTipoComprobante())
                .numeroComprobante(comprobante.getNumeroComprobante())
                .serieComprobante(comprobante.getSerieComprobante())
                .fechaEmision(comprobante.getFechaEmision())
                .estado(comprobante.getEstado())
                .hashVerificacion(comprobante.getHashVerificacion())
                .urlDescargaTicket("/api/v1/comprobantes/" + comprobante.getIdComprobante() + "/ticket")
                .urlDescargaPdf("/api/v1/comprobantes/" + comprobante.getIdComprobante() + "/pdf")
                .urlVisualizacion("/api/v1/comprobantes/" + comprobante.getIdComprobante())
                .observaciones(comprobante.getObservaciones())
                .archivoTicketDisponible(ticketDisponible)
                .archivoPdfDisponible(pdfDisponible)
                .build();
    }
}