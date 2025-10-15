package com.djasoft.mozaico.web.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para exponer información pública de la empresa en la carta digital
 * Solo incluye campos seguros y relevantes para los clientes
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaPublicaDTO {
    private String nombre;
    private String slug;
    private String descripcion;
    private String direccion;
    private String telefono;
    private String email;
    private String logoUrl;
    private String paginaWeb;
    private String moneda;
}
