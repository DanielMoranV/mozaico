package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.Comprobante;
import com.djasoft.mozaico.domain.enums.comprobante.EstadoComprobante;
import com.djasoft.mozaico.domain.enums.comprobante.TipoComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, Integer> {

    /**
     * Buscar comprobante por ID de pago
     */
    Optional<Comprobante> findByPagoIdPago(Integer idPago);

    /**
     * Buscar comprobantes por número
     */
    Optional<Comprobante> findByNumeroComprobante(String numeroComprobante);

    /**
     * Buscar comprobantes por tipo
     */
    List<Comprobante> findByTipoComprobante(TipoComprobante tipoComprobante);

    /**
     * Buscar comprobantes por estado
     */
    List<Comprobante> findByEstado(EstadoComprobante estado);

    /**
     * Buscar comprobantes por rango de fechas
     */
    List<Comprobante> findByFechaEmisionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtener comprobantes de un cliente específico
     */
    @Query("SELECT c FROM Comprobante c " +
           "WHERE c.pago.pedido.cliente.idCliente = :clienteId " +
           "ORDER BY c.fechaEmision DESC")
    List<Comprobante> findByClienteId(@Param("clienteId") Integer clienteId);

    /**
     * Obtener comprobantes por empleado
     */
    @Query("SELECT c FROM Comprobante c " +
           "WHERE c.pago.pedido.empleado.idUsuario = :empleadoId " +
           "ORDER BY c.fechaEmision DESC")
    List<Comprobante> findByEmpleadoId(@Param("empleadoId") Integer empleadoId);

    /**
     * Buscar comprobantes por hash de verificación
     */
    Optional<Comprobante> findByHashVerificacion(String hash);

    /**
     * Contar comprobantes por tipo en un período
     */
    @Query("SELECT COUNT(c) FROM Comprobante c " +
           "WHERE c.tipoComprobante = :tipo " +
           "AND c.fechaEmision BETWEEN :fechaInicio AND :fechaFin")
    Long countByTipoAndFechaBetween(@Param("tipo") TipoComprobante tipo,
                                   @Param("fechaInicio") LocalDateTime fechaInicio,
                                   @Param("fechaFin") LocalDateTime fechaFin);
}