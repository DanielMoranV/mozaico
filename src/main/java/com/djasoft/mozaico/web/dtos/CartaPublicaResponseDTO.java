package com.djasoft.mozaico.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la respuesta de la carta pública que incluye información de la empresa y sus productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartaPublicaResponseDTO {
    private EmpresaPublicaDTO empresa;
    private List<ProductoResponseDTO> productos;
}
