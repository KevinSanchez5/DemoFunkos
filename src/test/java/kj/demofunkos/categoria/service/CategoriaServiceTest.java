package kj.demofunkos.categoria.service;

import kj.demofunkos.categoria.dto.CategoriaCreateDto;
import kj.demofunkos.categoria.exceptions.CategoriaBadRequestException;
import kj.demofunkos.categoria.exceptions.CategoriaNotFoundException;
import kj.demofunkos.categoria.mapper.CategoriaMapper;
import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.categoria.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {
    private final UUID id = UUID.randomUUID();

    private final Categoria categoria = new Categoria(id, "TEST", LocalDateTime.now(), LocalDateTime.now(), true, new ArrayList<>());

    private final CategoriaCreateDto categoriaCreateDto = new CategoriaCreateDto("TEST");

    @Mock
    private CategoriaRepository repository;

    @Mock
    private CategoriaMapper mapper;

    @InjectMocks
    private CategoriaService service;


    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> result = service.findAll();

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size()),
                () -> assertEquals(categoria, result.get(0))
        );

        verify(repository, times(1)).findAll();
    }

    @Test
    void findById(){
        when(repository.findById(id)).thenReturn(Optional.of(categoria));

        Categoria result = service.findById(id);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(categoria, result)
        );

        verify(repository, times(1)).findById(id);

    }

    @Test
    void findByIdNotFound(){
        when(repository.findById(id)).thenReturn(Optional.empty());

        CategoriaNotFoundException exception = assertThrows(CategoriaNotFoundException.class, () -> service.findById(id));

        assertEquals("Categoria con id " + id+ " no encontrada", exception.getMessage());

        verify(repository, times(1)).findById(id);
    }

    @Test
    void save(){
        when(mapper.fromDtoToEntity(categoriaCreateDto)).thenReturn(categoria);
        when(repository.existsByNombre("TEST")).thenReturn(false);
        when(repository.save(categoria)).thenReturn(categoria);

        Categoria result = service.save(categoriaCreateDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(categoria, result),
                () -> assertTrue(categoria.isActiva()),
                () -> assertEquals(categoria.getId(), result.getId()),
                () -> assertEquals(categoria.getNombre(), result.getNombre()),
                () -> assertEquals(categoria.getFechaAlta(), result.getFechaAlta()),
                () -> assertEquals(categoria.getFunkos(), result.getFunkos())
        );

        verify(mapper, times(1)).fromDtoToEntity(categoriaCreateDto);
        verify(repository, times(1)).existsByNombre("TEST");
        verify(repository, times(1)).save(categoria);
    }

    @Test
    void saveCategoriaExists() {
        when(repository.existsByNombre("TEST")).thenReturn(true);

        CategoriaBadRequestException exception = assertThrows(CategoriaBadRequestException.class, () -> service.save(categoriaCreateDto));

        assertEquals("La categoría con el nombre TEST ya existe", exception.getMessage());

        verify(mapper, never()).fromDtoToEntity(categoriaCreateDto);
        verify(repository, times(1)).existsByNombre("TEST");
        verify(repository, never()).save(any());
    }

    @Test
    void update(){
        when(repository.existsByNombre("TEST")).thenReturn(false);
        when(repository.findById(id)).thenReturn(Optional.of(categoria));
        when(mapper.updateCategoriaFromDtoToEntity(categoriaCreateDto, categoria)).thenReturn(categoria);
        when(repository.save(categoria)).thenReturn(categoria);

        Categoria result = service.update(id, categoriaCreateDto);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(categoria, result),
                () -> assertTrue(categoria.isActiva()),
                () -> assertEquals(categoria.getId(), result.getId()),
                () -> assertEquals(categoria.getNombre(), result.getNombre()),
                () -> assertEquals(categoria.getFechaAlta(), result.getFechaAlta()),
                () -> assertEquals(categoria.getFunkos(), result.getFunkos())
        );

        verify(mapper, times(1)).updateCategoriaFromDtoToEntity(categoriaCreateDto, categoria);
        verify(repository, times(1)).existsByNombre("TEST");
        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(categoria);
    }

    @Test
    void updateCategoriaExists(){
        when(repository.existsByNombre("TEST")).thenReturn(true);
        when(repository.findById(id)).thenReturn(Optional.of(categoria));

        CategoriaBadRequestException exception = assertThrows(CategoriaBadRequestException.class, () -> service.update(id, categoriaCreateDto));

        assertEquals("La categoría con el nombre TEST ya existe", exception.getMessage());

        verify(mapper, never()).updateCategoriaFromDtoToEntity(categoriaCreateDto, categoria);
        verify(repository, times(1)).existsByNombre("TEST");
        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    void updateCategoriaNotFound() {
        when(repository.existsByNombre("TEST")).thenReturn(false);
        when(repository.findById(id)).thenReturn(Optional.empty());

        CategoriaNotFoundException exception = assertThrows(CategoriaNotFoundException.class, () -> service.update(id, categoriaCreateDto));

        assertEquals("Categoria con id " + id + " no encontrada", exception.getMessage());

        verify(mapper, never()).updateCategoriaFromDtoToEntity(categoriaCreateDto, categoria);
        verify(repository, times(1)).existsByNombre("TEST");
        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    void deactivateCategoria(){
        when(repository.findById(id)).thenReturn(Optional.of(categoria));
        when(repository.save(categoria)).thenReturn(categoria);

        service.deactivateCategoria(id);

        assertFalse(categoria.isActiva());

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(categoria);
    }

    @Test
    void deactivateCategoriaNotFound(){
        when(repository.findById(id)).thenReturn(Optional.empty());

        CategoriaNotFoundException exception = assertThrows(CategoriaNotFoundException.class, () -> service.deactivateCategoria(id));

        assertEquals("Categoria con id " + id + " no encontrada", exception.getMessage());

        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    void reactivateCategoria() {
        when(repository.findById(id)).thenReturn(Optional.of(categoria));
        when(repository.save(categoria)).thenReturn(categoria);

        Categoria result = service.reactivateCategoria(id);

        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.isActiva())
        );

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).save(categoria);
    }

    @Test
    void reactivateCategoriaNotFound(){
        when(repository.findById(id)).thenReturn(Optional.empty());

        CategoriaNotFoundException exception = assertThrows(CategoriaNotFoundException.class, () -> service.reactivateCategoria(id));

        assertEquals("Categoria con id " + id + " no encontrada", exception.getMessage());

        verify(repository, times(1)).findById(id);
        verify(repository, never()).save(any());
    }

    @Test
    void deleteById(){
        when(repository.findById(id)).thenReturn(Optional.of(categoria));
        doNothing().when(repository).deleteById(id);

        service.deleteById(id);

        verify(repository, times(1)).findById(id);
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    void deleteByIdNotFound(){
        when(repository.findById(id)).thenReturn(Optional.empty());

        CategoriaNotFoundException exception = assertThrows(CategoriaNotFoundException.class, () -> service.deleteById(id));

        assertEquals("Categoria con id " + id + " no encontrada", exception.getMessage());

        verify(repository, times(1)).findById(id);
        verify(repository, never()).delete(any());
    }

    @Test
    void findByNombre(){
        when(repository.findByNombreIgnoreCase("TEST")).thenReturn(Optional.of(categoria));

        Categoria result = service.findByNombre("TEST");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(categoria, result),
                () -> assertTrue(result.isActiva()),
                () -> assertEquals(categoria.getId(), result.getId()),
                () -> assertEquals(categoria.getNombre(), result.getNombre()),
                () -> assertEquals(categoria.getFechaAlta(), result.getFechaAlta()),
                () -> assertEquals(categoria.getFunkos(), result.getFunkos())
        );

        verify(repository, times(1)).findByNombreIgnoreCase("TEST");
    }

    @Test
    void findByNombreNotFound(){
        when(repository.findByNombreIgnoreCase("TEST")).thenReturn(Optional.empty());

        CategoriaNotFoundException exception = assertThrows(CategoriaNotFoundException.class, () -> service.findByNombre("TEST"));

        assertEquals("Categoria con nombre TEST no encontrada", exception.getMessage());

        verify(repository, times(1)).findByNombreIgnoreCase("TEST");
    }
}