package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Integer>, JpaSpecificationExecutor<MetodoPago> {
}
