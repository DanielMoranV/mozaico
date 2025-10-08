package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.DatosFacturacion;
import com.djasoft.mozaico.domain.entities.Empresa;
import com.djasoft.mozaico.domain.enums.facturacion.EstadoFormalizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatosFacturacionRepository extends JpaRepository<DatosFacturacion, Long> {
    
    Optional<DatosFacturacion> findByEmpresa(Empresa empresa);
    
    Optional<DatosFacturacion> findByRuc(String ruc);
    
    List<DatosFacturacion> findByEstadoFormalizacion(EstadoFormalizacion estadoFormalizacion);
    
    List<DatosFacturacion> findByFacturacionElectronicaActivaTrue();
    
    @Query("SELECT df FROM DatosFacturacion df WHERE df.facturacionElectronicaActiva = true AND df.oseActivo = true")
    List<DatosFacturacion> findByFacturacionElectronicaYOseActivos();
    
    @Query("SELECT df FROM DatosFacturacion df WHERE df.ruc IS NOT NULL AND df.estadoFormalizacion IN :estados")
    List<DatosFacturacion> findByEstadosFormalizacion(@Param("estados") List<EstadoFormalizacion> estados);
    
    boolean existsByRuc(String ruc);
}