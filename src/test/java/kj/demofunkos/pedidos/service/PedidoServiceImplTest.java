package kj.demofunkos.pedidos.service;

import kj.demofunkos.funko.repository.FunkoRepository;
import kj.demofunkos.pedidos.exceptions.PedidoNotFound;
import kj.demofunkos.pedidos.models.Pedido;
import kj.demofunkos.pedidos.repository.PedidosRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.bson.types.ObjectId;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

        @Mock
        private PedidosRepository pedidosRepository;

        @Mock
        private FunkoRepository funkoRepository;

        @InjectMocks
        private PedidoServiceImpl pedidoService;

        @Test
        void findAll() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pedido> pedidos = new PageImpl<>(List.of(new Pedido(), new Pedido()));

            when(pedidosRepository.findAll(pageable)).thenReturn(pedidos);

            Page<Pedido> result = pedidoService.findAll(pageable);

            assertEquals(2, result.getTotalElements());
            assertNotNull(result.getContent());
            assertEquals(pedidos.getTotalElements(), result.getTotalElements());
            assertEquals(pedidos.getContent(), result.getContent());

            verify(pedidosRepository, times(1)).findAll(pageable);
        }

        @Test
        void findById() {
            ObjectId idPedido = new ObjectId();
            Pedido pedido = new Pedido();
            pedido.setId(idPedido);

            when(pedidosRepository.findById(idPedido)).thenReturn(Optional.of(pedido));

            Pedido result = pedidoService.findById(idPedido);

            assertEquals(idPedido, result.getId());
            assertEquals(pedido, result);

            verify(pedidosRepository, times(1)).findById(idPedido);
        }

        @Test
        void findByIdNotFound() {
            ObjectId idPedido = new ObjectId();
            Pedido pedido = new Pedido();
            pedido.setId(idPedido);

            when(pedidosRepository.findById(idPedido)).thenReturn(Optional.empty());

            assertThrows(PedidoNotFound.class, () -> pedidoService.findById(idPedido));

            verify(pedidosRepository, times(1)).findById(idPedido);
        }

        @Test
        void findByIdUsuario() {
            Long idUsuario = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pedido> pedidos = new PageImpl<>(List.of(new Pedido(), new Pedido()));

            when(pedidosRepository.findByIdUsuario(idUsuario, pageable)).thenReturn(pedidos);

            Page<Pedido> result = pedidoService.findByIdUsuario(idUsuario, pageable);

            assertEquals(2, result.getTotalElements());
        }

        @Test
        void findByIdUsuarioNotFound() {
            Long idUsuario = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            Page<Pedido> pedidos = new PageImpl<>(List.of());

            when(pedidosRepository.findByIdUsuario(idUsuario, pageable)).thenReturn(pedidos);

            Page<Pedido> result = pedidoService.findByIdUsuario(idUsuario, pageable);

            assertEquals(0, result.getTotalElements());
        }



}