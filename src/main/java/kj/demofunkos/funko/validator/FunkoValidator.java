package kj.demofunkos.funko.validator;

import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.exceptions.FunkoBadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class FunkoValidator {

    public void validarFunkoUpdateDto(FunkoUpdateDto dto) {
        //regex para que la fecha tenga formato aaaa-mm-dd
        String regex = "\\d{4}-\\d{2}-\\d{2}";

        if (dto.getNombre() == null && dto.getPrecio() == null && dto.getDescripcion() == null && dto.getFechaDeFabricacion() == null && dto.getNombreCategoria() == null) {
            throw new FunkoBadRequestException("El nombre y el precio no pueden estar vacíos");
        }
        if (dto.getPrecio() != null && dto.getPrecio() <= 0) {
            throw new FunkoBadRequestException("El precio no puede ser menor o igual a 0");
        }
        if (dto.getNombre() != null && dto.getNombre().trim().equals("")) {
            throw new FunkoBadRequestException("El nombre no puede estar vacio o ser espacios en blanco");
        }
        if (dto.getDescripcion() != null && dto.getDescripcion().trim().equals("")) {
            throw new FunkoBadRequestException("La descripcion no puede estar vacia o ser espacios en blanco");
        }
        if (dto.getFechaDeFabricacion() != null && !dto.getFechaDeFabricacion().toString().matches(regex)) {
            {
                throw new FunkoBadRequestException("La fecha de fabricación no tiene el formato aaaa-mm-dd");
            }
        }
        if (dto.getNombreCategoria() != null && dto.getNombreCategoria().trim().equals("")) {
            throw new FunkoBadRequestException("El nombre de la categoria no puede estar vacio o ser espacios en blanco");
        }
    }
}
