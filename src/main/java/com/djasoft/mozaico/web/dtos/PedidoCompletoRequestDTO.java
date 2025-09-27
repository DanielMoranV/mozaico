package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.pedido.TipoServicio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCompletoRequestDTO {

    private Integer idCliente; // Opcional - puede ser null para clientes anónimos

    private Integer idMesa; // Opcional - puede ser null para delivery/takeaway

    @NotNull(message = "El ID del empleado no puede ser nulo")
    private Long idEmpleado;

    @NotNull(message = "El tipo de servicio no puede ser nulo")
    private TipoServicio tipoServicio;

    private String observaciones;

    private String direccionDelivery; // Requerido solo para delivery

    @NotEmpty(message = "La lista de detalles no puede estar vacía")
    @Valid
    private List<DetallePedidoCompletoRequestDTO> detalles;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetallePedidoCompletoRequestDTO {

        @NotNull(message = "El ID del producto no puede ser nulo")
        private Long idProducto;

        @NotNull(message = "La cantidad no puede ser nula")
        private Integer cantidad;

        private String observaciones;
    }
}