package kj.demofunkos.funko.mapper;

import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.model.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FunkoMapperTest {
    FunkoMapper mapper;

    Funko funkoViejo = new Funko(1L, "testViejo", 1.0, LocalDateTime.now(), LocalDateTime.now(), false , null);

    @BeforeEach
    void setUp(){
        mapper = new FunkoMapper();
    }

    @Test
    void fromEntitytoCreateDto() {
        Funko funko = new Funko("test", 1.0);
        FunkoCreateDto dto = mapper.fromEntitytoCreateDto(funko);

        assertAll(
                ()-> assertEquals(funko.getNombre(), dto.getNombre()),
                ()-> assertEquals(funko.getPrecio(), dto.getPrecio())
        );
    }

    @Test
    void fromCreatetoEntity() {
        FunkoCreateDto dto = new FunkoCreateDto("test", 1.0, null, null);
        Funko funko = mapper.fromCreatetoEntity(dto);

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

        FunkoUpdateDto dto = new FunkoUpdateDto("testNuevo", null, null, null);
        Funko funkoUpdated = mapper.fromUpdateToEntity(funkoViejo, dto);

        assertAll(
                ()-> assertEquals(funkoViejo.getId(), funkoUpdated.getId()),
                ()-> assertEquals(dto.getNombre(), funkoUpdated.getNombre()),
                ()-> assertEquals(funkoViejo.getPrecio(), funkoUpdated.getPrecio()),
                ()-> assertEquals(funkoViejo.getFechaAlta(), funkoUpdated.getFechaAlta()),
                ()-> assertFalse(funkoUpdated.isBorrado())
        );

    }
}