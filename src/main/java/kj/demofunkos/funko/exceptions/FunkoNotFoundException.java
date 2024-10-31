package kj.demofunkos.funko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FunkoNotFoundException extends FunkoException{

    public FunkoNotFoundException(Long id) {
        super("Funko con id " + id + " no encontrado");
    }

    public FunkoNotFoundException(String nombre){
        super("Funko con nombre " + nombre + " no encontrado");
    }
}
