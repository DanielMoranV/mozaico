package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT a FROM AuditLog a WHERE a.empresa.idEmpresa = :empresaId ORDER BY a.fechaHora DESC")
    Page<AuditLog> findByEmpresaIdOrderByFechaHoraDesc(@Param("empresaId") Long empresaId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.usuario.idUsuario = :usuarioId AND a.empresa.idEmpresa = :empresaId ORDER BY a.fechaHora DESC")
    Page<AuditLog> findByUsuarioIdAndEmpresaIdOrderByFechaHoraDesc(
            @Param("usuarioId") Long usuarioId,
            @Param("empresaId") Long empresaId,
            Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.entidad = :entidad AND a.entidadId = :entidadId AND a.empresa.idEmpresa = :empresaId ORDER BY a.fechaHora DESC")
    List<AuditLog> findByEntidadAndEntidadIdAndEmpresaIdOrderByFechaHoraDesc(
            @Param("entidad") String entidad,
            @Param("entidadId") Long entidadId,
            @Param("empresaId") Long empresaId);

    @Query("SELECT a FROM AuditLog a WHERE a.accion = :accion AND a.empresa.idEmpresa = :empresaId AND a.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaHora DESC")
    List<AuditLog> findByAccionAndEmpresaIdAndFechaHoraBetweenOrderByFechaHoraDesc(
            @Param("accion") String accion,
            @Param("empresaId") Long empresaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.usuario.idUsuario = :usuarioId AND a.accion = 'LOGIN' AND a.exitoso = true AND a.fechaHora >= :fecha")
    Long countSuccessfulLoginsSince(@Param("usuarioId") Long usuarioId, @Param("fecha") LocalDateTime fecha);
}