package kj.demofunkos.funko.service;

import kj.demofunkos.categoria.exceptions.CategoriaNotFoundException;
import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.categoria.repository.CategoriaRepository;
import kj.demofunkos.config.websockets.WebSocketConfig;
import kj.demofunkos.config.websockets.WebSocketHandler;
import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.exceptions.FunkoException;
import kj.demofunkos.funko.exceptions.FunkoNotFoundException;
import kj.demofunkos.funko.mapper.FunkoMapper;
import kj.demofunkos.funko.model.Funko;
import kj.demofunkos.funko.repository.FunkoRepository;
import kj.demofunkos.funko.validator.FunkoValidator;
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
class FunkoServiceTest {

    Categoria categoria = new Categoria(UUID.randomUUID(), "CategoriaTest", LocalDateTime.now(), LocalDateTime.now(), false, new ArrayList<>());

    Funko funko = new Funko("FunkoTest", 10.0, 10, categoria);
    FunkoCreateDto createDto = new FunkoCreateDto("FunkoTest", 10.0, 10, null, null, "CategoriaTest");
    FunkoUpdateDto updateDto = new FunkoUpdateDto("FunkoTest", 1.0, 10, null, null, "CategoriaTest");

    @Mock
    private FunkoRepository repository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private FunkoMapper mapper;

    @Mock
    private FunkoValidator funkoValidator;

    @Mock
    private WebSocketConfig webSocketConfig;

    @Mock
    private WebSocketHandler webSocketService;

    @InjectMocks
    private FunkoService service;

    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(List.of(funko));

        List<Funko> funkos = service.findAll();

        assertEquals(1, funkos.size());
        assertEquals(funko, funkos.get(0));
        assertNotNull(funkos);

        verify(repository,times(1)).findAll();
    }

    @Test
    void findById() {
        when(repository.findById(1L)).thenReturn(Optional.of(funko));

        Funko funkoBuscado = service.findById(1L);

        assertAll(
                () -> assertNotNull(funkoBuscado),
                () -> assertEquals(funko.getNombre(), funkoBuscado.getNombre()),
                () -> assertEquals(funko.getPrecio(), funkoBuscado.getPrecio()),
                () -> assertEquals(funko, funkoBuscado)
        );
        verify(repository,times(1)).findById(1L);
    }

    @Test
    void findByIdNotFound(){
        when(repository.findById(1L)).thenReturn(Optional.empty());

        FunkoNotFoundException e= assertThrows(FunkoNotFoundException.class, () -> service.findById(1L));
        assertEquals("Funko con id 1 no encontrado", e.getMessage());

        verify(repository,times(1)).findById(1L);
    }

    @Test
    void save() {
        when(categoriaRepository.findByNombreIgnoreCase("CategoriaTest")).thenReturn(Optional.of(categoria));
        when(mapper.fromCreatetoEntity(createDto, categoria)).thenReturn(funko);
        when(repository.save(funko)).thenReturn(funko);


        Funko funkoGuardado = service.save(createDto);

        assertAll(
                () -> assertNotNull(funkoGuardado),
                () -> assertEquals(funko.getNombre(), funkoGuardado.getNombre()),
                () -> assertEquals(funko.getPrecio(), funkoGuardado.getPrecio()),
                () -> assertEquals(funko.getId(), funkoGuardado.getId()),
                () -> assertEquals(funko.getFechaModificacion(), funkoGuardado.getFechaModificacion()),
                () -> assertEquals(funko.getFechaAlta(), funkoGuardado.getFechaAlta()),
                () -> assertFalse(funkoGuardado.isBorrado()),
                () -> assertEquals(funko.getCategoria(), funkoGuardado.getCategoria()),
                () -> assertEquals(funko.getDetalles(), funkoGuardado.getDetalles())
        );
        verify(mapper, times(1)).fromCreatetoEntity(createDto, categoria);
        verify(repository, times(1)).save(funko);
        verify(categoriaRepository, times(1)).findByNombreIgnoreCase("CategoriaTest");
    }

    @Test
    void saveCategoriaNotFound(){
        when(categoriaRepository.findByNombreIgnoreCase("CategoriaTest")).thenReturn(Optional.empty());

        CategoriaNotFoundException e = assertThrows(CategoriaNotFoundException.class, () -> service.save(createDto));
        assertEquals("Categoria con nombre CATEGORIATEST no encontrada", e.getMessage());

        verify(mapper, never()).fromCreatetoEntity(createDto, categoria);
        verify(repository, never()).save(funko);
        verify(categoriaRepository, times(1)).findByNombreIgnoreCase("CategoriaTest");
    }

    @Test
    void update() {
        doNothing().when(funkoValidator).validarFunkoUpdateDto(updateDto);
        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        when(categoriaRepository.findByNombreIgnoreCase("CATEGORIATEST")).thenReturn(Optional.of(categoria));
        when(mapper.fromUpdateToEntity(funko, updateDto, categoria)).thenReturn(funko);
        when(repository.save(funko)).thenReturn(funko);

        Funko funkoActualizado = service.update(1L, updateDto);

        assertAll(
                () -> assertNotNull(funkoActualizado),
                () -> assertEquals(funko.getNombre(), funkoActualizado.getNombre()),
                () -> assertEquals(funko.getPrecio(), funkoActualizado.getPrecio()),
                () -> assertEquals(funko.getId(), funkoActualizado.getId()),
                () -> assertEquals(funko.getFechaModificacion(), funkoActualizado.getFechaModificacion()),
                () -> assertEquals(funko.getFechaAlta(), funkoActualizado.getFechaAlta())
        );
        verify(funkoValidator, times(1)).validarFunkoUpdateDto(updateDto);
        verify(repository,times(1)).findById(1L);
        verify(mapper, times(1)).fromUpdateToEntity(funko, updateDto, categoria);
        verify(repository, times(1)).save(funko);
        verify(categoriaRepository, times(1)).findByNombreIgnoreCase("CATEGORIATEST");
    }

    @Test
    void updateFunkoNotFound() throws FunkoException {
        doNothing().when(funkoValidator).validarFunkoUpdateDto(updateDto);
        when(repository.findById(1L)).thenReturn(Optional.empty());


        FunkoNotFoundException e = assertThrows(FunkoNotFoundException.class, () -> service.update(1L, updateDto));
        assertEquals("Funko con id 1 no encontrado", e.getMessage());

        verify(funkoValidator).validarFunkoUpdateDto(updateDto);
        verify(repository).findById(1L);
        verify(mapper, never()).fromUpdateToEntity(any(), any(), any());
        verify(repository, never()).save(any());
        verify(categoriaRepository, never()).findByNombreIgnoreCase(any());
    }

    @Test
    void updateCategoriaNotFound() {
        doNothing().when(funkoValidator).validarFunkoUpdateDto(updateDto);
        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        when(categoriaRepository.findByNombreIgnoreCase("CATEGORIATEST")).thenReturn(Optional.empty());

        CategoriaNotFoundException e = assertThrows(CategoriaNotFoundException.class, () -> service.update(1L, updateDto));
        assertEquals("Categoria con nombre CATEGORIATEST no encontrada", e.getMessage());

        verify(funkoValidator).validarFunkoUpdateDto(updateDto);
        verify(repository).findById(1L);
        verify(mapper, never()).fromUpdateToEntity(any(), any(), any());
        verify(repository, never()).save(any());
        verify(categoriaRepository).findByNombreIgnoreCase("CATEGORIATEST");
    }

    @Test
    void deleteById() {
        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        doNothing().when(repository).deleteById(1L);

        service.deleteById(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void deleteByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        FunkoNotFoundException e = assertThrows(FunkoNotFoundException.class, () -> service.deleteById(1L));
        assertEquals("Funko con id 1 no encontrado", e.getMessage());

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void deleteLogically() {
        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        when(repository.save(funko)).thenReturn(funko);

        service.deleteLogically(1L);

        assertAll(
                () -> assertTrue(funko.isBorrado())
        );

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(funko);
    }

    @Test
    void deleteLogicallyFunkoNotFound() throws FunkoException {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        FunkoNotFoundException e = assertThrows(FunkoNotFoundException.class, () -> service.deleteLogically(1L));
        assertEquals("Funko con id 1 no encontrado", e.getMessage());

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any());
    }

    @Test
    void reactivateFunko() {
        when(repository.findById(1L)).thenReturn(Optional.of(funko));
        when(repository.save(funko)).thenReturn(funko);

        Funko funkoReactivado = service.reactivateFunko(1L);

        assertAll(
                () -> assertFalse(funkoReactivado.isBorrado())
        );

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(funko);
    }

    @Test
    void reactivateFunkoNotFound() throws FunkoException {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        FunkoNotFoundException e = assertThrows(FunkoNotFoundException.class, () -> service.reactivateFunko(1L));
        assertEquals("Funko con id 1 no encontrado", e.getMessage());

        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any());
    }

    @Test
    void findByNombreIgnoreCase() throws FunkoException{
        when(repository.findByNombreIgnoreCase("FUNKOTEST")).thenReturn(Optional.of(funko));

        Funko funkoBuscado = service.findByNombreIgnoreCase("FUNKOTEST");

        assertAll(
                ()-> assertEquals(funko.getNombre(), funkoBuscado.getNombre()),
                ()-> assertFalse(funkoBuscado.isBorrado()),
                ()-> assertEquals(funko.getPrecio(), funkoBuscado.getPrecio()),
                ()-> assertEquals(funko.getFechaAlta(), funkoBuscado.getFechaAlta()),
                ()-> assertEquals(funko.getFechaModificacion(), funkoBuscado.getFechaModificacion())
        );
        verify(repository, times(1)).findByNombreIgnoreCase("FUNKOTEST");
    }

    @Test
    void findByNombreIgnoreCaseNotFound() throws FunkoException {
        when(repository.findByNombreIgnoreCase("ALGO")).thenReturn(Optional.empty());

        FunkoNotFoundException e = assertThrows(FunkoNotFoundException.class, () -> service.findByNombreIgnoreCase("ALGO"));

        assertAll(
                ()-> assertEquals("Funko con nombre ALGO no encontrado", e.getMessage())
        );

        verify(repository, times(1)).findByNombreIgnoreCase(any());
    }

    @Test
    void findByPrecioGreaterThan() throws FunkoException {
        when(repository.findByPrecioGreaterThan(0.0)).thenReturn(List.of(funko));

        List<Funko> funkosEncontrados = service.findByPrecioGreaterThan(0.0);

        assertAll(
                () -> assertFalse(funkosEncontrados.isEmpty()),
                () -> assertEquals(funko.getNombre(), funkosEncontrados.get(0).getNombre()),
                () -> assertFalse(funkosEncontrados.get(0).isBorrado()),
                () -> assertEquals(1, funkosEncontrados.size())
        );
    }

    @Test
    void findByPrecioGreaterThanEmptyList() throws FunkoException {
        when(repository.findByPrecioGreaterThan(1000.0)).thenReturn(List.of());

        List<Funko> funkosEncontrados = service.findByPrecioGreaterThan(1000.0);

        assertAll(
                () -> assertTrue(funkosEncontrados.isEmpty())
        );
    }

    @Test
    void findFunkosByPrecioLessThan() throws FunkoException {
        when(repository.findByPrecioLessThan(1000.0)).thenReturn(List.of(funko));

        List<Funko> funkosEncontrados = service.findByPrecioLessThan(1000.0);

        assertAll(
                () -> assertFalse(funkosEncontrados.isEmpty()),
                () -> assertEquals(funko.getNombre(), funkosEncontrados.get(0).getNombre()),
                () -> assertFalse(funkosEncontrados.get(0).isBorrado()),
                () -> assertEquals(1, funkosEncontrados.size())
        );
    }

    @Test
    void findFunkosByPrecioLessThanEmptyList() throws FunkoException {
        when(repository.findByPrecioLessThan(0.0)).thenReturn(List.of());

        List<Funko> funkosEncontrados = service.findByPrecioLessThan(0.0);

        assertAll(
                () -> assertTrue(funkosEncontrados.isEmpty())
        );
    }

    @Test
    void findByNombreContainingIgnoreCase() throws FunkoException {
        when(repository.findByNombreContainingIgnoreCase("fun")).thenReturn(List.of(funko));

        List<Funko> funkosEncontrados = service.findByNombreContainingIgnoreCase("fun");

        assertAll(
                () -> assertFalse(funkosEncontrados.isEmpty()),
                () -> assertEquals(funko.getNombre(), funkosEncontrados.get(0).getNombre()),
                () -> assertFalse(funkosEncontrados.get(0).isBorrado()),
                () -> assertEquals(1, funkosEncontrados.size()),
                () -> assertTrue(funkosEncontrados.get(0).getNombre().toLowerCase().contains("fun"))
        );
    }

    @Test
    void findByNombreContainingIgnoreCaseEmptyList() throws FunkoException {
        when(repository.findByNombreContainingIgnoreCase("algo")).thenReturn(List.of());

        List<Funko> funkosEncontrados = service.findByNombreContainingIgnoreCase("algo");

        assertAll(
                () -> assertTrue(funkosEncontrados.isEmpty())
        );
    }

    @Test
    void findFunkosByPrecioBetween() throws FunkoException {
        when(repository.findByPrecioBetween(0.0, 10.0)).thenReturn(List.of(funko));

        List<Funko> funkosEncontrados = service.findByPrecioBetween(0.0, 10.0);

        assertAll(
                () -> assertFalse(funkosEncontrados.isEmpty()),
                () -> assertEquals(funko.getNombre(), funkosEncontrados.get(0).getNombre()),
                () -> assertFalse(funkosEncontrados.get(0).isBorrado()),
                () -> assertEquals(1, funkosEncontrados.size())
        );
    }

    @Test
    void findFunkosByPrecioBetweenEmptyList() throws FunkoException {
        when(repository.findByPrecioBetween(10.0, 20.0)).thenReturn(List.of());

        List<Funko> funkosEncontrados = service.findByPrecioBetween(10.0, 20.0);

        assertAll(
                () -> assertTrue(funkosEncontrados.isEmpty())
        );
    }
}