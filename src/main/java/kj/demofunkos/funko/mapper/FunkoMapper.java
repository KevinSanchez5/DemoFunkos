package kj.demofunkos.funko.mapper;

import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.model.Detalles;
import kj.demofunkos.funko.model.Funko;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class FunkoMapper {

    public FunkoCreateDto fromEntitytoCreateDto(Funko funko) {
        return new FunkoCreateDto(funko.getNombre(), funko.getPrecio(),
                funko.getStock(),
                funko.getDetalles().getDescripcion()
                , funko.getDetalles().getFechaDeFabricacion(), funko.getCategoria().getNombre());
    }

    public Funko fromCreatetoEntity(FunkoCreateDto funkoCreateDto, Categoria categoria) {

        Funko nuevoFunko = new Funko(funkoCreateDto.getNombre().trim(), funkoCreateDto.getPrecio(), funkoCreateDto.getStock(), categoria);

        nuevoFunko.setDetalles(new Detalles(
                funkoCreateDto.getDescripcion() != null ? funkoCreateDto.getDescripcion().trim() : null,
                funkoCreateDto.getFechaDeFabricacion() != null ? funkoCreateDto.getFechaDeFabricacion() : null
        ));

        return nuevoFunko;
    }


    public Funko fromUpdateToEntity(Funko funkoViejo, FunkoUpdateDto funkoUpdateDto, Categoria categoriaNueva) {
        Detalles nuevosDetalles = new Detalles(
                funkoUpdateDto.getDescripcion() != null ? funkoUpdateDto.getDescripcion().trim() : funkoViejo.getDetalles() != null ? funkoViejo.getDetalles().getDescripcion() : null,
                funkoUpdateDto.getFechaDeFabricacion() != null ? funkoUpdateDto.getFechaDeFabricacion() : funkoViejo.getDetalles() != null ? funkoViejo.getDetalles().getFechaDeFabricacion() : null
        );
        return new Funko(
                funkoViejo.getId(),
                funkoUpdateDto.getNombre() != null ? funkoUpdateDto.getNombre().trim() : funkoViejo.getNombre(),
                funkoUpdateDto.getPrecio() != null ? funkoUpdateDto.getPrecio() : funkoViejo.getPrecio(),
                funkoUpdateDto.getStock() != null ? funkoUpdateDto.getStock() : funkoViejo.getStock(),
                funkoViejo.getFechaAlta(),
                LocalDateTime.now(),
                false,
                nuevosDetalles,
                categoriaNueva
        );
    }
}
