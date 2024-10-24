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


    public Funko fromUpdateToEntity(Funko funkoViejo, FunkoUpdateDto funkoupdateDto) {
        return new Funko(
                funkoViejo.getId(),
                funkoupdateDto.getNombre() != null ? funkoupdateDto.getNombre().trim() : funkoViejo.getNombre(),
                funkoupdateDto.getPrecio() != null ? funkoupdateDto.getPrecio() : funkoViejo.getPrecio(),
                funkoViejo.getFechaAlta(),
                LocalDateTime.now()
        );
    }
}
