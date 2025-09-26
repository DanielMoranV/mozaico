package com.djasoft.mozaico.web.dtos;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.djasoft.mozaico.domain.enums.producto.EstadoProducto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoUpdateDTO {

    private String nombre;

    private String descripcion;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero.")
    private BigDecimal precio;

    private Long idCategoria;

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
}
