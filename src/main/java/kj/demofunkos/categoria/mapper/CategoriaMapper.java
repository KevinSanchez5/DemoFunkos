package kj.demofunkos.categoria.mapper;

import kj.demofunkos.categoria.dto.CategoriaCreateDto;
import kj.demofunkos.categoria.models.Categoria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoriaMapper {

    public Categoria fromDtoToEntity(CategoriaCreateDto dto){
        return new Categoria(dto.getNombre().trim().toUpperCase());
    }

    public Categoria updateCategoriaFromDtoToEntity(CategoriaCreateDto dto, Categoria categoriaVieja){
        return new Categoria(
                categoriaVieja.getId(),
                dto.getNombre() != null ? dto.getNombre().trim().toUpperCase() : categoriaVieja.getNombre(),
                categoriaVieja.getFechaAlta(),
                LocalDateTime.now(),
                categoriaVieja.isActiva(),
                categoriaVieja.getFunkos()
        );
    }
}
