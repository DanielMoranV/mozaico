package com.djasoft.mozaico.domain.entities;

import com.djasoft.mozaico.domain.enums.comprobante.TipoComprobante;
import com.djasoft.mozaico.domain.enums.comprobante.EstadoComprobante;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comprobantes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comprobante")
    private Integer idComprobante;

    @OneToOne
    @JoinColumn(name = "id_pago", referencedColumnName = "id_pago")
    private Pago pago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoComprobante tipoComprobante;

    @Column(name = "numero_comprobante", unique = true, nullable = false)
    private String numeroComprobante;

    @Column(name = "serie_comprobante")
    private String serieComprobante;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoComprobante estado = EstadoComprobante.GENERADO;

    @Column(name = "ruta_archivo_pdf")
    private String rutaArchivoPdf;

    @Column(name = "ruta_archivo_ticket")
    private String rutaArchivoTicket;

    @Column(name = "hash_verificacion")
    private String hashVerificacion;

    @Column(name = "qr_code")
    private String qrCode;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "contador_impresiones")
    @Builder.Default
    private Integer contadorImpresiones = 0;

    @Column(name = "fecha_primera_impresion")
    private LocalDateTime fechaPrimeraImpresion;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "usuario_anulacion")
    private String usuarioAnulacion;

    @Column(name = "fecha_envio_digital")
    private LocalDateTime fechaEnvioDigital;

    @Column(name = "email_envio")
    private String emailEnvio;

    @Column(name = "fecha_creacion")
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}