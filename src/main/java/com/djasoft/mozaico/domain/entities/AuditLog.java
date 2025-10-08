package com.djasoft.mozaico.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    @Column(name = "accion", nullable = false, length = 50)
    private String accion; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.

    @Column(name = "entidad", nullable = false, length = 100)
    private String entidad; // Usuario, Pago, Pedido, etc.

    @Column(name = "entidad_id")
    private Long entidadId; // ID del registro afectado

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "detalles_adicionales", columnDefinition = "TEXT")
    private String detallesAdicionales; // JSON con parámetros, valores anteriores, etc.

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @CreationTimestamp
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "exitoso", nullable = false)
    @Builder.Default
    private Boolean exitoso = true;

    @Column(name = "mensaje_error", columnDefinition = "TEXT")
    private String mensajeError;
}