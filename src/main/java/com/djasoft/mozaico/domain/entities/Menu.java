package com.djasoft.mozaico.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menus")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idMenu;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Builder.Default
    private boolean disponible = true;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "menu_productos",
            joinColumns = @JoinColumn(name = "id_menu"),
            inverseJoinColumns = @JoinColumn(name = "id_producto")
    )
    @Builder.Default
    private Set<Producto> productos = new HashSet<>();

    // === AUDITORÍA Y SEGURIDAD ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creacion", nullable = false)
    private Usuario usuarioCreacion;
}
