package kj.demofunkos.pedidos.service;

import kj.demofunkos.pedidos.models.Pedido;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PedidoService {

    Page<Pedido> findAll(Pageable pageable);

    Pedido findById(ObjectId idPedido);

    Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable);

    Pedido save(Pedido pedido);

    Pedido update(ObjectId idPedido, Pedido pedido);

    void delete(ObjectId idPedido);


}
