package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.DetallePedido;
import com.djasoft.mozaico.domain.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer>, JpaSpecificationExecutor<DetallePedido> {

    List<DetallePedido> findByPedidoIdPedido(Integer idPedido);

    List<DetallePedido> findByEstado(com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido estado);

    List<DetallePedido> findByEstadoAndProductoRequierePreparacion(
            com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido estado,
            Boolean requierePreparacion
    );

    /**
     * Query optimizada para KDS con ordenamiento condicional según el estado:
     * - PEDIDO y EN_PREPARACION: Más antiguo primero (ASC) - FIFO
     * - LISTO: Más nuevo primero (DESC) - LIFO (mostrar recién terminados arriba)
     *
     * Ordena por:
     * 1. fechaEstadoActualizado cuando existe (cuando cambió al estado actual)
     * 2. fechaCreacion como fallback (cuando se creó el detalle)
     */
    @Query("""
        SELECT d FROM DetallePedido d
        WHERE d.estado = :estado
        AND d.producto.requierePreparacion = :requierePreparacion
        AND d.pedido.estado IN ('ABIERTO', 'ATENDIDO')
        ORDER BY
            CASE WHEN d.estado = 'LISTO'
                 THEN COALESCE(d.fechaEstadoActualizado, d.fechaCreacion)
            END DESC,
            CASE WHEN d.estado != 'LISTO'
                 THEN COALESCE(d.fechaEstadoActualizado, d.fechaCreacion)
            END ASC
    """)
    List<DetallePedido> findByEstadoParaKds(
            @Param("estado") com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido estado,
            @Param("requierePreparacion") Boolean requierePreparacion
    );

    List<DetallePedido> findByPedido(Pedido pedido);
}
