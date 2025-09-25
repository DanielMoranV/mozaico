package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer>, JpaSpecificationExecutor<Proveedor> {
}
