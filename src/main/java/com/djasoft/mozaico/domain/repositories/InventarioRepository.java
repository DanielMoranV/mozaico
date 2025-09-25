package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer>, JpaSpecificationExecutor<Inventario> {
    Optional<Inventario> findByProductoIdProducto(Long idProducto);
}
