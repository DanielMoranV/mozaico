package com.djasoft.mozaico.domain.entities;

import com.djasoft.mozaico.domain.enums.cliente.TipoDocumentoCliente;
import com.djasoft.mozaico.domain.enums.cliente.TipoPersona;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer idCliente;

    // === DATOS BÁSICOS (para persona natural y jurídica) ===
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", length = 100)
    private String apellido;

    // === DATOS DE CONTACTO ===
    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;

    // === TIPO DE PERSONA ===
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_persona", nullable = false, length = 20)
    @Builder.Default
    private TipoPersona tipoPersona = TipoPersona.NATURAL;

    // === DOCUMENTO DE IDENTIDAD ===
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", length = 30)
    private TipoDocumentoCliente tipoDocumento;

    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    // === DATOS ESPECÍFICOS PERSONA NATURAL ===
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "preferencias_alimentarias", columnDefinition = "TEXT")
    private String preferenciasAlimentarias;

    // === DATOS ESPECÍFICOS PERSONA JURÍDICA ===
    @Column(name = "razon_social", length = 200)
    private String razonSocial; // Nombre completo de la empresa

    @Column(name = "nombre_comercial", length = 200)
    private String nombreComercial; // Nombre con el que opera

    @Column(name = "representante_legal", length = 200)
    private String representanteLegal;

    // === PROGRAMA DE FIDELIZACIÓN ===
    @Builder.Default
    @Column(name = "puntos_fidelidad")
    private Integer puntosFidelidad = 0;

    @CreationTimestamp
    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro;

    @Builder.Default
    @Column(name = "activo")
    private Boolean activo = true;

    // === AUDITORÍA Y SEGURIDAD ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creacion", nullable = false)
    private Usuario usuarioCreacion;
}
