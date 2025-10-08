package com.djasoft.mozaico.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.djasoft.mozaico.domain.enums.producto.EstadoProducto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "productos")
@EqualsAndHashCode(exclude = {"categoria", "empresa", "usuarioCreacion"})
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @Column(name = "tiempo_preparacion", columnDefinition = "INT DEFAULT 0")
    private Integer tiempoPreparacion;

    @Column(name = "disponible", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean disponible;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    @Column(name = "ingredientes", columnDefinition = "TEXT")
    private String ingredientes;

    @Column(name = "calorias")
    private Integer calorias;

    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    @Column(name = "marca", length = 50)
    private String marca;

    @Column(name = "presentacion", length = 50)
    private String presentacion;

    @Column(name = "requiere_preparacion", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean requierePreparacion;

    @Column(name = "es_alcoholico", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean esAlcoholico;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoProducto estado = EstadoProducto.ACTIVO;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // === AUDITORÍA Y SEGURIDAD ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creacion", nullable = false)
    private Usuario usuarioCreacion;
}
