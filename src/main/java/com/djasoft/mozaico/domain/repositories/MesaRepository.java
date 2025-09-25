package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Integer>, JpaSpecificationExecutor<Mesa> {
}
