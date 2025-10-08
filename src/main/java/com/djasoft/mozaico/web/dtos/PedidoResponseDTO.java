package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.pedido.EstadoPedido;
import com.djasoft.mozaico.domain.enums.pedido.TipoServicio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Integer idPedido;
    private ClienteResponseDTO cliente;
    private MesaResponseDTO mesa;
    private UsuarioResponseDTO empleado;
    private UsuarioResponseDTO usuarioCreacion; // Usuario que creó el pedido
    private LocalDateTime fechaPedido;
    private EstadoPedido estado;
    private TipoServicio tipoServicio;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal descuento;
    private BigDecimal total;
    private String observaciones;
    private String direccionDelivery;
    private List<DetallePedidoResponseDTO> detalles;
}
