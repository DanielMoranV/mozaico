package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.Empresa;
import com.djasoft.mozaico.domain.enums.empresa.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    Optional<Empresa> findByActivaTrue();
    
    List<Empresa> findByTipoOperacion(TipoOperacion tipoOperacion);
    
    List<Empresa> findByActivaTrueOrderByFechaCreacionDesc();
    
    @Query("SELECT e FROM Empresa e WHERE e.activa = true AND e.aplicaIgv = true")
    List<Empresa> findEmpresasConIgv();
    
    @Query("SELECT e FROM Empresa e WHERE e.activa = true AND e.aplicaIgv = false")
    List<Empresa> findEmpresasSinIgv();
    
    boolean existsByActivaTrue();

    /**
     * Buscar empresa por su slug único (para URLs públicas)
     */
    Optional<Empresa> findBySlug(String slug);

    /**
     * Verificar si un slug ya está en uso
     */
    boolean existsBySlug(String slug);
}