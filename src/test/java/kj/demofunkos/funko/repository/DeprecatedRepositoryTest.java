package kj.demofunkos.funko.repository;

import kj.demofunkos.funko.exceptions.FunkoException;
import kj.demofunkos.funko.model.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeprecatedRepositoryTest {

    DeprecatedRepository repository ;

    @BeforeEach
    void setUp() {
        repository = new DeprecatedRepository();
    }

    @Test
    void findAll() {
        List<Funko> funkos = repository.findAll();

        assertAll(
                () -> assertTrue(funkos.isEmpty()),
                () -> assertEquals(0, funkos.size())
         );
    }

    @Test
    void findById() {
        Funko funko = new Funko("Funko 1", 1.0);
        Funko funkoGuardado =repository.save(funko);

        Funko funkoEncontrado = repository.findById(funkoGuardado.getId());

        assertNotNull(funkoEncontrado);

        assertEquals(funkoGuardado.getId(), funkoEncontrado.getId());
        assertEquals(funko.getNombre(), funkoEncontrado.getNombre());
        assertEquals(funko.getPrecio(), funkoEncontrado.getPrecio());
        assertEquals(funkoGuardado.getFechaModificacion(), funkoEncontrado.getFechaModificacion());
        assertEquals(funkoGuardado.getFechaAlta(), funkoEncontrado.getFechaAlta());
    }

    @Test
    void findByIdNotFound() throws FunkoException {

        Funko funkoNotFound = repository.findById(1L);

        assertNull(funkoNotFound);

    }
    @Test
    void save() {
        Funko funko = new Funko("test", 1.0);

        Funko funkoSaved = repository.save(funko);
        System.out.println(funkoSaved.getId());
        assertAll(
                () -> assertNull(funkoSaved.getId()),
                () -> assertEquals(funko.getNombre(), funkoSaved.getNombre()),
                () -> assertEquals(funko.getPrecio(), funkoSaved.getPrecio()),
                () -> assertEquals(funko.getFechaAlta(), funkoSaved.getFechaAlta()),
                () -> assertEquals(funko.getFechaModificacion() ,funkoSaved.getFechaModificacion())
        );
    }

    @Test
    void update() {
        Funko funko = new Funko("test", 1.0);
        Funko funkoSaved = repository.save(funko);

        funko.setNombre("testUpdated");
        funko.setPrecio(2.0);

        Funko funkoUpdated = repository.update(funkoSaved.getId(), funko);

        assertAll(
                () -> assertEquals(funkoUpdated.getId(), funkoSaved.getId()),
                () -> assertEquals(funkoUpdated.getNombre(), "testUpdated"),
                () -> assertEquals(funkoUpdated.getPrecio(), 2.0),
                () -> assertEquals(funkoUpdated.getFechaAlta(), funkoSaved.getFechaAlta()),
                () -> assertEquals(funkoUpdated.getFechaModificacion(), funkoSaved.getFechaModificacion())
        );
    }

    @Test
    void deleteById() {
        Funko funko = new Funko("test", 1.0);
        Funko funkoSaved = repository.save(funko);

        repository.deleteById(funkoSaved.getId());

        Funko funkoDeleted = repository.findById(funkoSaved.getId());

        assertNull(funkoDeleted);
    }
}