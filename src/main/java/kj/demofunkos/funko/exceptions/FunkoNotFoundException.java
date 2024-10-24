package kj.demofunkos.funko.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FunkoNotFoundException extends FunkoException{

    public FunkoNotFoundException(String id) {
        super("Funko con id " + id + " no encontrado");
    }
}
