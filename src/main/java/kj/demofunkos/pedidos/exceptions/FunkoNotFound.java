package kj.demofunkos.pedidos.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FunkoNotFound extends PedidoException {

    public FunkoNotFound(Long id) {
        super("Producto con id " + id + " no encontrado");
    }

}
