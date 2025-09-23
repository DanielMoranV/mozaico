package com.djasoft.mozaico.web.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "El nombre del producto no puede estar vacío.")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo.")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero.")
    private BigDecimal precio;

    @NotNull(message = "La categoría no puede ser nula.")
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
