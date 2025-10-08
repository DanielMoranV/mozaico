package com.djasoft.mozaico.domain.entities;

import com.djasoft.mozaico.domain.enums.facturacion.EstadoFormalizacion;
import com.djasoft.mozaico.domain.enums.facturacion.RegimenTributario;
import com.djasoft.mozaico.domain.enums.facturacion.TipoContribuyente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que contiene los datos de facturación electrónica SUNAT
 * Esta entidad es completamente opcional - solo existe si la empresa tiene RUC
 */
@Entity
@Table(name = "datos_facturacion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatosFacturacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDatosFacturacion;

    // === DATOS BÁSICOS SUNAT (Solo si tiene RUC) ===
    @Column(unique = true, length = 11)
    private String ruc; // Puede ser NULL

    @Column(length = 300)
    private String razonSocial;

    @Column(length = 300)
    private String nombreComercial;

    @Column(columnDefinition = "TEXT")
    private String direccionFiscal;

    @Column(length = 6)
    private String ubigeo;

    @Column(length = 100)
    private String provincia;

    @Column(length = 100)
    private String departamento;

    @Column(length = 100)
    private String distrito;

    // === ESTADO DE FORMALIZACIÓN ===
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoFormalizacion estadoFormalizacion = EstadoFormalizacion.SIN_RUC;

    @Enumerated(EnumType.STRING)
    private TipoContribuyente tipoContribuyente; // Solo si tiene RUC

    @Enumerated(EnumType.STRING)
    private RegimenTributario regimenTributario; // Solo si tiene RUC

    // === FACTURACIÓN ELECTRÓNICA (Solo si está habilitado) ===
    @Column(nullable = false)
    @Builder.Default
    private Boolean facturacionElectronicaActiva = false;

    // === SERIES DE COMPROBANTES (Solo si tiene facturación electrónica) ===
    @Column(length = 4)
    private String serieFactura; // Solo si emite facturas

    @Column(length = 4)
    private String serieBoleta; // Solo si emite boletas electrónicas

    @Column(length = 4)
    private String serieNotaCredito;

    @Column(length = 4)
    private String serieNotaDebito;

    // === NUMERACIÓN CORRELATIVA ===
    @Builder.Default
    private Long correlativoFactura = 1L;

    @Builder.Default
    private Long correlativoBoleta = 1L;

    @Builder.Default
    private Long correlativoNotaCredito = 1L;

    @Builder.Default
    private Long correlativoNotaDebito = 1L;

    // === CONFIGURACIÓN TÉCNICA (Solo si usa facturación electrónica) ===
    @Column(length = 255)
    private String certificadoDigitalPath;

    @Column(length = 100)
    private String certificadoPassword; // Encriptado

    @Column(columnDefinition = "TEXT")
    private String claveSOL; // Encriptado

    @Column(length = 50)
    private String usuarioSOL;

    // === OSE (Operador de Servicios Electrónicos) ===
    @Column(length = 100)
    private String oseProveedor; // SUNAT, NUBEFACT, FACTURADOR, etc.

    @Column(length = 255)
    private String oseEndpoint;

    @Column(length = 100)
    private String oseUsuario;

    @Column(columnDefinition = "TEXT")
    private String oseClave; // Encriptado

    @Column(nullable = false)
    @Builder.Default
    private Boolean oseActivo = false;

    // === CONFIGURACIÓN TRIBUTARIA ===
    @Column(nullable = false)
    @Builder.Default
    private Boolean validarReceptorRuc = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean envioAutomaticoSunat = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer diasMaximoEnvio = 3;

    @Column(columnDefinition = "TEXT")
    private String observacionesFacturacion;

    // === AUDITORÍA ===
    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    private LocalDateTime fechaUltimaActualizacionSunat;

    // === RELACIÓN CON EMPRESA ===
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;
}