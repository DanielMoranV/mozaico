package com.djasoft.mozaico.domain.entities;

import com.djasoft.mozaico.domain.enums.empresa.TipoOperacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la información básica de la empresa
 * Funciona tanto para negocios informales como formales
 */
@Entity
@Table(name = "empresas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEmpresa;
    
    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(unique = true, nullable = false, length = 100)
    private String slug; // Identificador único para URLs públicas (ej: "restaurante-mozaico")

    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(columnDefinition = "TEXT")
    private String direccion;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 255)
    private String logoUrl;
    
    @Column(length = 255)
    private String paginaWeb;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean activa = true;
    
    // === CONFIGURACIÓN DE OPERACIÓN ===
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TipoOperacion tipoOperacion = TipoOperacion.TICKET_SIMPLE; // Determina qué puede emitir
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean aplicaIgv = false; // Solo true si es formal con RUC
    
    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal porcentajeIgv = new BigDecimal("18.00"); // IGV Perú
    
    @Column(length = 3)
    @Builder.Default
    private String moneda = "PEN"; // ISO 4217
    
    // === NUMERACIÓN PARA TICKETS SIMPLES ===
    @Column(nullable = false)
    @Builder.Default
    private Long correlativoTicket = 1L; // Para tickets sin valor tributario
    
    @Column(length = 10)
    @Builder.Default
    private String prefijoTicket = "TKT"; // Prefijo para tickets internos

    // === CONFIGURACIÓN DE RESERVAS ===
    @Column(nullable = false)
    @Builder.Default
    private Integer duracionReservaHoras = 2; // Duración por defecto de una reserva en horas

    // === AUDITORÍA ===
    @CreationTimestamp
    private LocalDateTime fechaCreacion;
    
    @UpdateTimestamp 
    private LocalDateTime fechaActualizacion;
    
    // === RELACIÓN OPCIONAL CON FACTURACIÓN ===
    @OneToOne(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DatosFacturacion datosFacturacion; // NULL si no tiene RUC
}