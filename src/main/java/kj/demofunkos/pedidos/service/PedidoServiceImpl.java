package kj.demofunkos.pedidos.service;

import jakarta.transaction.Transactional;
import kj.demofunkos.funko.model.Funko;
import kj.demofunkos.funko.repository.FunkoRepository;
import kj.demofunkos.pedidos.exceptions.*;
import kj.demofunkos.pedidos.models.Pedido;
import kj.demofunkos.pedidos.repository.PedidosRepository;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Cacheable("pedidos")
@Slf4j
public class PedidoServiceImpl implements PedidoService{

    private final PedidosRepository pedidosRepository;
    private final FunkoRepository funkoRepository;

    public PedidoServiceImpl(PedidosRepository pedidosRepository, FunkoRepository funkoRepository) {
        this.pedidosRepository = pedidosRepository;
        this.funkoRepository = funkoRepository;
    }


    @Override
    public Page<Pedido> findAll(Pageable pageable) {
        return pedidosRepository.findAll(pageable);
    }

    @Override
    public Pedido findById(ObjectId idPedido) {
        return pedidosRepository.findById(idPedido).orElseThrow(() -> new PedidoNotFound(idPedido.toHexString()));
    }

    @Override
    public Page<Pedido> findByIdUsuario(Long idUsuario, Pageable pageable) {
        return pedidosRepository.findByIdUsuario(idUsuario, pageable);
    }

    @Override
    @Transactional
    public Pedido save(Pedido pedido) {
        log.info("Guardando pedido");

        checkPedido(pedido);

        Pedido pedidoAGuardar = reserveStockPedidos(pedido);

        pedidoAGuardar.setCreatedAt(LocalDateTime.now());
        pedidoAGuardar.setUpdatedAt(LocalDateTime.now());

        return pedidosRepository.save(pedidoAGuardar);
    }

    @Override
    @Transactional
    public Pedido update(ObjectId idPedido, Pedido pedido) {
        log.info("Actualizando pedido con id: " + idPedido);

        Pedido pedidoAActualizar = this.findById(idPedido);

        returnStockPedidos(pedidoAActualizar);

        checkPedido(pedido);

        Pedido pedidoAGuardar = reserveStockPedidos(pedido);

        pedidoAGuardar.setUpdatedAt(LocalDateTime.now());

        return pedidosRepository.save(pedidoAGuardar);
    }

    @Override
    @Transactional
    public void delete(ObjectId idPedido) {
        log.info("Borrando pedido: {}", idPedido);

        Pedido pedidoABorrar = this.findById(idPedido); //lanza pedido not found si no encuentra

        returnStockPedidos(pedidoABorrar);

        pedidosRepository.deleteById(idPedido);

    }

    Pedido returnStockPedidos(Pedido pedido){
        log.info("Retornando stock del pedido: {}", pedido);
        if (pedido.getLineasPedido() != null) {
            pedido.getLineasPedido().forEach(lineaPedido -> {
                Funko funko = funkoRepository.findById(lineaPedido.getIdFunko()).get(); // Siempre existe porque ha pasado el check
                funko.setStock(funko.getStock() + lineaPedido.getCantidad());
                funkoRepository.save(funko);
            });
        }
        return pedido;
    }


    void checkPedido(Pedido pedido) {
        log.info("Comprobando pedido: {}", pedido);

        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNotItems(pedido.getId().toHexString());
        }
        pedido.getLineasPedido().forEach(lineaPedido -> {
            Funko funko = funkoRepository.findById(lineaPedido.getIdFunko())
                    .orElseThrow(() -> new FunkoNotFound(lineaPedido.getIdFunko()));

            if (funko.getStock() < lineaPedido.getCantidad()) {
                throw new FunkoNotStock(lineaPedido.getIdFunko());
            }

            if (!funko.getPrecio().equals(lineaPedido.getPrecioFunko())) {
                throw new FunkoBadPrice(lineaPedido.getIdFunko());
            }
        });
    }

    Pedido reserveStockPedidos(Pedido pedido) {
        log.info("Reservando stock del pedido: {}", pedido);

        if (pedido.getLineasPedido() == null || pedido.getLineasPedido().isEmpty()) {
            throw new PedidoNotItems(pedido.getId().toHexString());
        }

        pedido.getLineasPedido().forEach(lineaPedido -> {
            var funko = funkoRepository.findById(lineaPedido.getIdFunko()).get();
            funko.setStock(funko.getStock() - lineaPedido.getCantidad());
            funkoRepository.save(funko);
            lineaPedido.setPrecioTotal(lineaPedido.getCantidad() * lineaPedido.getPrecioFunko());
        });


        var total = pedido.getLineasPedido().stream()
                .map(lineaPedido -> lineaPedido.getCantidad() * lineaPedido.getPrecioFunko())
                .reduce(0.0, Double::sum);


        Integer totalItems = pedido.getLineasPedido().stream()
                .map(lineaPedido -> lineaPedido.getCantidad())
                .reduce(0, Integer::sum);

        // Actualizamos el total del pedido y el total de items
        pedido.setTotal(total);
        pedido.setTotalItems(totalItems);

        return pedido;
    }
}
