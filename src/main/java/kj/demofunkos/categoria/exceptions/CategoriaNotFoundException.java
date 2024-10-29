package kj.demofunkos.categoria.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoriaNotFoundException extends CategoriaException{

    public CategoriaNotFoundException(String id){
        super("Categoria con id" + id + " no encontrado");
    }
}
