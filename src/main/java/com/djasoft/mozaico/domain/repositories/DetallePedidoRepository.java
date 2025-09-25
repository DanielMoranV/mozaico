package com.djasoft.mozaico.domain.repositories;

import com.djasoft.mozaico.domain.entities.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Integer>, JpaSpecificationExecutor<DetallePedido> {

    List<DetallePedido> findByPedidoIdPedido(Integer idPedido);

    List<DetallePedido> findByEstado(com.djasoft.mozaico.domain.enums.detallepedido.EstadoDetallePedido estado);
}
