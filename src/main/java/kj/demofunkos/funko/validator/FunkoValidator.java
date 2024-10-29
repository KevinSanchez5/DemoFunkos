package kj.demofunkos.funko.validator;

import kj.demofunkos.funko.dto.FunkoUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FunkoValidator {

    public void validarFunkoUpdateDto(FunkoUpdateDto dto) {
        if (dto.getNombre() == null  && dto.getPrecio() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El nombre y el precio no pueden estar vacíos"
            );
        }
        if (dto.getPrecio() != null && dto.getPrecio() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El precio no puede ser menor o igual a 0"
            );
        }
        if(dto.getNombre()!=null && dto.getNombre().trim().equals("")){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El nombre no puede estar vacio o ser espacios en blanco"
            );
        }
    }
}
