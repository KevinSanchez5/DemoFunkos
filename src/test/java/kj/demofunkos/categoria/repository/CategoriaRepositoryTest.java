package kj.demofunkos.categoria.repository;

import kj.demofunkos.categoria.models.Categoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoriaRepositoryTest {

    Categoria catgoriaTest = new Categoria(UUID.randomUUID(), "CategoriaTest", LocalDateTime.now(), LocalDateTime.now(), true, new ArrayList<>());

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.merge(catgoriaTest);
        entityManager.flush();
    }

    @Test
    void findByNombreIgnoreCase() {
        Categoria categoria = categoriaRepository.findByNombreIgnoreCase("CategoriaTEST").get();

        assertAll(
                () -> assertNotNull(categoria),
                () -> assertInstanceOf(UUID.class, categoria.getId()),
                () -> assertEquals(catgoriaTest.getNombre(), categoria.getNombre()),
                () -> assertEquals(catgoriaTest.getFechaAlta(), categoria.getFechaAlta()),
                () -> assertEquals(catgoriaTest.getFechaModificacion(), categoria.getFechaModificacion()),
                () -> assertTrue(catgoriaTest.isActiva())
        );
    }

    @Test
    void findByNombreIgnoreCaseEmptyOptional() {
        Optional<Categoria> emptyCategoria = categoriaRepository.findByNombreIgnoreCase("CategoriaInvisible");

        assertTrue(emptyCategoria.isEmpty());
    }

    @Test
    void existsByNombre() {
        boolean exists = categoriaRepository.existsByNombre("CategoriaTest");

        assertTrue(exists);
    }

    @Test
    void existsByNombreFalse() {
        boolean exists = categoriaRepository.existsByNombre("CategoriaInvisible");

        assertFalse(exists);
    }
}