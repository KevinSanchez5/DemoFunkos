package kj.demofunkos.funko.repository;

import kj.demofunkos.funko.model.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FunkoRepositoryTest {

    Funko funkoTest = new Funko(null, "Funko Test", 1.0, LocalDateTime.now(), LocalDateTime.now(), false);
    Funko funkoUpdate = new Funko("Funko Update", 2.0);

    @Autowired
    private FunkoRepository funkoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.merge(funkoTest);
        entityManager.flush();
    }


    @Test
    void findByNombreIgnoreCase() {
        Optional<Funko> funko = funkoRepository.findByNombreIgnoreCase("Funko TEST");

        assertAll(
                () -> assertTrue(funko.isPresent()),
                () -> assertEquals(funkoTest.getNombre(), funko.get().getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), funko.get().getPrecio()),
                () -> assertEquals(funkoTest.getFechaAlta(), funko.get().getFechaAlta()),
                () -> assertEquals(funkoTest.getFechaModificacion(), funko.get().getFechaModificacion()),
                () -> assertFalse(funkoTest.isBorrado())
        );
    }

    @Test
    void findByNombreIgnoreCaseEmptyOptional() {
        Optional<Funko> emptyFunko = funkoRepository.findByNombreIgnoreCase("funkoInvisible");

        assertTrue(emptyFunko.isEmpty());
    }


    @Test
    void findByPriceGreaterThan() {
        List<Funko> funkosBaratos = funkoRepository.findByPriceGreaterThan(0.1);

        assertAll(
                () -> assertFalse(funkosBaratos.isEmpty()),
                () -> assertEquals(1, funkosBaratos.size()),
                () -> assertEquals(funkoTest.getNombre(), funkosBaratos.getFirst().getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), funkosBaratos.getFirst().getPrecio()),
                () -> assertEquals(funkoTest.getFechaAlta(), funkosBaratos.getFirst().getFechaAlta()),
                () -> assertEquals(funkoTest.getFechaModificacion(), funkosBaratos.getFirst().getFechaModificacion()),
                () -> assertFalse(funkosBaratos.getFirst().isBorrado()),
                () -> assertTrue(funkosBaratos.getFirst().getPrecio() > 0.1)
        );
    }

    @Test
    void findByPriceGreaterThanEmptyList() {
        List<Funko> funkosCaros = funkoRepository.findByPriceGreaterThan(100.0);

        assertTrue(funkosCaros.isEmpty());
    }

    @Test
    void findByPriceLessThan() {
        List<Funko> funkosBaratos = funkoRepository.findByPriceLessThan(10.0);

        assertAll(
                () -> assertFalse(funkosBaratos.isEmpty()),
                () -> assertEquals(1, funkosBaratos.size()),
                () -> assertEquals(funkoTest.getNombre(), funkosBaratos.getFirst().getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), funkosBaratos.getFirst().getPrecio()),
                () -> assertEquals(funkoTest.getFechaAlta(), funkosBaratos.getFirst().getFechaAlta()),
                () -> assertEquals(funkoTest.getFechaModificacion(), funkosBaratos.getFirst().getFechaModificacion()),
                () -> assertFalse(funkosBaratos.getFirst().isBorrado()),
                () -> assertTrue(funkosBaratos.getFirst().getPrecio() < 10.0)
        );
    }

    @Test
    void findByPrecioBetweenEmptyList() {
        List<Funko> funkosRegalados = funkoRepository.findByPriceLessThan(0.00);

        assertTrue(funkosRegalados.isEmpty());
    }

    @Test
    void findByNombreContainingIgnoreCase() {
        List<Funko> funkos = funkoRepository.findByNombreContainingIgnoreCase("Test");

        assertAll(
                () -> assertFalse(funkos.isEmpty()),
                () -> assertEquals(1, funkos.size()),
                () -> assertEquals(funkoTest.getNombre(), funkos.getFirst().getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), funkos.getFirst().getPrecio()),
                () -> assertEquals(funkoTest.getFechaAlta(), funkos.getFirst().getFechaAlta()),
                () -> assertEquals(funkoTest.getFechaModificacion(), funkos.getFirst().getFechaModificacion()),
                () -> assertFalse(funkoTest.isBorrado()),
                () -> assertTrue(funkos.getFirst().getNombre().contains("Test"))
        );
    }

    @Test
    void findByNombreContainingIgnoreCaseEmptyList() {
        List<Funko> funkos = funkoRepository.findByNombreContainingIgnoreCase("Invisible");

        assertTrue(funkos.isEmpty());
    }

    @Test
    void findByPrecioBetween() {
        List<Funko> funkos = funkoRepository.findByPrecioBetween(0.0, 10.0);

        assertAll(
                () -> assertFalse(funkos.isEmpty()),
                () -> assertEquals(1, funkos.size()),
                () -> assertEquals(funkoTest.getNombre(), funkos.getFirst().getNombre()),
                () -> assertEquals(funkoTest.getPrecio(), funkos.getFirst().getPrecio()),
                () -> assertEquals(funkoTest.getFechaAlta(), funkos.getFirst().getFechaAlta()),
                () -> assertEquals(funkoTest.getFechaModificacion(), funkos.getFirst().getFechaModificacion()),
                () -> assertFalse(funkoTest.isBorrado()),
                () -> assertTrue(funkos.getFirst().getPrecio() > 0.0 && funkos.getFirst().getPrecio() < 10.0)
        );
    }

    @Test
    void findByPrecioBetween_EmptyList() {
        List<Funko> funkos = funkoRepository.findByPrecioBetween(100.0, 200.0);

        assertTrue(funkos.isEmpty());
    }
}