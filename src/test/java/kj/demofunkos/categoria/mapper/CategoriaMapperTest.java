package kj.demofunkos.categoria.mapper;

import kj.demofunkos.categoria.dto.CategoriaCreateDto;
import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.funko.model.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaMapperTest {

    CategoriaMapper mapper;

    Funko funkoViejo = new Funko(1L, "testViejo", 1.0, 10, LocalDateTime.now(), LocalDateTime.now(), false , null, null);
    Categoria categoriaVieja = new Categoria(UUID.randomUUID(), "testCategoria", LocalDateTime.now(), LocalDateTime.now(), true, new ArrayList<>());

    @BeforeEach
    void setUp() {
        mapper = new CategoriaMapper();
        categoriaVieja.getFunkos().add(funkoViejo);
    }

    @Test
    void fromDtoToEntity() {
        CategoriaCreateDto dto = new CategoriaCreateDto("   testCategoriaDto    ");
        Categoria categoria = mapper.fromDtoToEntity(dto);

        assertAll(
                ()-> assertEquals("TESTCATEGORIADTO", categoria.getNombre()),
                ()-> assertNotNull(categoria.getFechaAlta()),
                ()-> assertNotNull(categoria.getFechaModificacion()),
                ()-> assertTrue(categoria.isActiva()),
                ()-> assertInstanceOf(UUID.class, categoria.getId()),
                ()-> assertNull(categoria.getFunkos())
        );
    }

    @Test
    void updateCategoriaFromDtoToEntity() {
        CategoriaCreateDto createDto = new  CategoriaCreateDto("   testCategoriaDto    ");
        Categoria categoria = mapper.updateCategoriaFromDtoToEntity(createDto, categoriaVieja);

        assertAll(
                ()-> assertEquals(createDto.getNombre().trim().toUpperCase(), categoria.getNombre()),
                ()-> assertEquals(categoriaVieja.getFechaAlta(), categoria.getFechaAlta()),
                ()-> assertTrue(categoria.isActiva()),
                ()-> assertEquals(categoriaVieja.getId(), categoria.getId()),
                ()-> assertEquals(categoriaVieja.getFunkos(), categoria.getFunkos())
        );

    }
}