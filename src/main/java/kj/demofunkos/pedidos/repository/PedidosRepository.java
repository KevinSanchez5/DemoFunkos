package kj.demofunkos.pedidos.repository;

import kj.demofunkos.pedidos.models.Pedido;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidosRepository extends MongoRepository<Pedido, ObjectId> {

    Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable);
}
