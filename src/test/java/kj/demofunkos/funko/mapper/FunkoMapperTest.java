package kj.demofunkos.funko.mapper;

import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.model.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FunkoMapperTest {
    FunkoMapper mapper;

    Categoria categoriaVieja = new Categoria(UUID.randomUUID(), "testCategoria", LocalDateTime.now(), LocalDateTime.now(), true, new ArrayList<>());
    Funko funkoViejo = new Funko(1L, "testViejo", 1.0, LocalDateTime.now(), LocalDateTime.now(), false , null, categoriaVieja);


    @BeforeEach
    void setUp(){
        mapper = new FunkoMapper();
        categoriaVieja.getFunkos().add(funkoViejo);
    }

    @Test
    void fromEntitytoCreateDto() {
        Funko funko = new Funko("testViejo", 1.0, categoriaVieja);
        FunkoCreateDto dto = mapper.fromEntitytoCreateDto(funko);

        assertAll(
                ()-> assertEquals(funko.getNombre(), dto.getNombre()),
                ()-> assertEquals(funko.getPrecio(), dto.getPrecio()),
                ()-> assertEquals(funko.getDetalles().getDescripcion(), dto.getDescripcion()),
                ()-> assertEquals(funko.getDetalles().getFechaDeFabricacion(), dto.getFechaDeFabricacion()),
                ()-> assertFalse(funko.isBorrado()),
                ()-> assertEquals(funko.getCategoria().getNombre(), dto.getNombreCategoria())
        );
    }

    @Test
    void fromCreatetoEntity() {
        FunkoCreateDto dto = new FunkoCreateDto("test", 1.0, null, null, "algo");
        Funko funko = mapper.fromCreatetoEntity(dto, categoriaVieja);

        assertAll(
                ()-> assertNull(funko.getId()),
                ()-> assertEquals(dto.getNombre(), funko.getNombre()),
                ()-> assertEquals(dto.getPrecio(), funko.getPrecio()),
                ()-> assertNotNull(funko.getFechaAlta()),
                ()-> assertNotNull(funko.getFechaModificacion()),
                ()-> assertFalse(funko.isBorrado())
        );
    }

    @Test
    void fromUpdateToEntity() {

        FunkoUpdateDto dto = new FunkoUpdateDto("testNuevo", null, null, null, null);
        Funko funkoUpdated = mapper.fromUpdateToEntity(funkoViejo, dto, null);

        assertAll(
                ()-> assertEquals(funkoViejo.getId(), funkoUpdated.getId()),
                ()-> assertEquals(dto.getNombre(), funkoUpdated.getNombre()),
                ()-> assertEquals(funkoViejo.getPrecio(), funkoUpdated.getPrecio()),
                ()-> assertEquals(funkoViejo.getFechaAlta(), funkoUpdated.getFechaAlta()),
                ()-> assertFalse(funkoUpdated.isBorrado())
        );

    }
}