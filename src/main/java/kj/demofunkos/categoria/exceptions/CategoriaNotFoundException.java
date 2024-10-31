package kj.demofunkos.categoria.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoriaNotFoundException extends CategoriaException{

    public CategoriaNotFoundException(UUID id){
        super("Categoria con id " + id + " no encontrado");
    }

    public CategoriaNotFoundException(String nombre){
        super("Categoria con nombre " + nombre + " no encontrado");
    }
}
