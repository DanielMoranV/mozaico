package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer>, JpaSpecificationExecutor<Pago> {
}
