package kj.demofunkos.funko.mapper;

import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.model.Funko;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FunkoMapper {

    public FunkoCreateDto fromEntitytoCreateDto(Funko funko) {
        return new FunkoCreateDto(funko.getNombre(), funko.getPrecio());
    }

    public Funko fromCreatetoEntity(FunkoCreateDto funkoCreateDto) {
        return new Funko(funkoCreateDto.getNombre().trim(), funkoCreateDto.getPrecio());
    }


    public Funko fromUpdateToEntity(Funko funkoViejo, FunkoUpdateDto funkoUpdateDto) {
        return new Funko(
                funkoViejo.getId(),
                funkoUpdateDto.getNombre() != null ? funkoUpdateDto.getNombre().trim() : funkoViejo.getNombre(),
                funkoUpdateDto.getPrecio() != null ? funkoUpdateDto.getPrecio() : funkoViejo.getPrecio(),
                funkoViejo.getFechaAlta(),
                LocalDateTime.now(),
                false,
                funkoViejo.getDetalles()
        );
    }
}
