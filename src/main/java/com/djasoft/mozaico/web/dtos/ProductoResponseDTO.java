package com.djasoft.mozaico.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.djasoft.mozaico.domain.enums.producto.EstadoProducto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private Long idProducto;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private CategoriaResponseDTO categoria;
    private Integer tiempoPreparacion;
    private Boolean disponible;
    private String imagenUrl;
    private String ingredientes;
    private Integer calorias;
    private String codigoBarras;
    private String marca;
    private String presentacion;
    private Boolean requierePreparacion;
    private Boolean esAlcoholico;
    private EstadoProducto estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
