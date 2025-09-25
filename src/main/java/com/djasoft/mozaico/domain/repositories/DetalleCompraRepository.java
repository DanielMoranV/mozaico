package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.Compra;
import com.djasoft.mozaico.domain.entities.DetalleCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleCompraRepository extends JpaRepository<DetalleCompra, Integer> {

    List<DetalleCompra> findByCompra(Compra compra);
}