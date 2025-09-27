package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.mesa.EstadoMesa;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MesaEstadoDetalladoResponseDTO {
    private Integer idMesa;
    private Integer numeroMesa;
    private Integer capacidad;
    private String ubicacion;
    private EstadoMesa estado;
    private String observaciones;
    private LocalDateTime fechaCreacion;

    // Información del último pedido (si está ocupada)
    private PedidoBasicoResponseDTO ultimoPedido;

    // Información de la última reserva (si está reservada)
    private ReservaBasicaResponseDTO ultimaReserva;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PedidoBasicoResponseDTO {
        private Integer idPedido;
        private LocalDateTime fechaPedido;
        private String estado;
        private String tipoServicio;
        private String cliente;
        private String empleado;
        private Double total;
        private java.util.List<DetallePedidoBasicoResponseDTO> detalles;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetallePedidoBasicoResponseDTO {
        private Integer idDetalle;
        private String producto;
        private Integer cantidad;
        private Double precioUnitario;
        private String estado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReservaBasicaResponseDTO {
        private Integer idReserva;
        private LocalDateTime fechaHoraReserva;
        private Integer numeroPersonas;
        private String estado;
        private String cliente;
        private String observaciones;
        private LocalDateTime fechaCreacion;
    }
}