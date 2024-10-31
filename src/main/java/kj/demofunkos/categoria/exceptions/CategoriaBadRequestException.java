package kj.demofunkos.categoria.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CategoriaBadRequestException extends CategoriaException {

    public CategoriaBadRequestException(String nombre){
        super("La categoría con el nombre " + nombre + " ya existe");
    }
}
