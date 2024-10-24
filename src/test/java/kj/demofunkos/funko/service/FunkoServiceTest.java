package kj.demofunkos.funko.service;

import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.exceptions.FunkoException;
import kj.demofunkos.funko.exceptions.FunkoNotFoundException;
import kj.demofunkos.funko.mapper.FunkoMapper;
import kj.demofunkos.funko.model.Funko;
import kj.demofunkos.funko.repository.FunkoRepository;
import kj.demofunkos.funko.validator.Validador;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceTest {

    Funko funko = new Funko("FunkoTest", 10.0);
    FunkoCreateDto createDto = new FunkoCreateDto("FunkoTest", 10.0);
    FunkoUpdateDto updateDto = new FunkoUpdateDto("FunkoTest", null);

    @Mock
    private FunkoRepository repository;

    @Mock
    private FunkoMapper mapper;

    @Mock
    private Validador validador;

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
        when(repository.findById(1L)).thenReturn(funko);

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
        when(repository.findById(1L)).thenReturn(null);

        FunkoNotFoundException e= assertThrows(FunkoNotFoundException.class, () -> service.findById(1L));
        assertEquals("Funko con id 1 no encontrado", e.getMessage());

        verify(repository,times(1)).findById(1L);
    }

    @Test
    void save() {
        when(mapper.fromCreatetoEntity(createDto)).thenReturn(funko);
        when(repository.save(funko)).thenReturn(funko);

        Funko funkoGuardado = service.save(createDto);

        assertAll(
                () -> assertNotNull(funkoGuardado),
                () -> assertEquals(funko.getNombre(), funkoGuardado.getNombre()),
                () -> assertEquals(funko.getPrecio(), funkoGuardado.getPrecio()),
                () -> assertEquals(funko.getId(), funkoGuardado.getId()),
                () -> assertEquals(funko.getFechaModificacion(), funkoGuardado.getFechaModificacion()),
                () -> assertEquals(funko.getFechaAlta(), funkoGuardado.getFechaAlta())
        );
        verify(mapper, times(1)).fromCreatetoEntity(createDto);
        verify(repository, times(1)).save(funko);
    }

    @Test
    void update() {
        doNothing().when(validador).validarFunkoUpdateDto(updateDto);
        when(repository.findById(1L)).thenReturn(funko);
        when(mapper.fromUpdateToEntity(funko, updateDto)).thenReturn(funko);
        when(repository.update(1L,funko)).thenReturn(funko);

        Funko funkoActualizado = service.update(1L, updateDto);

        assertAll(
                () -> assertNotNull(funkoActualizado),
                () -> assertEquals(funko.getNombre(), funkoActualizado.getNombre()),
                () -> assertEquals(funko.getPrecio(), funkoActualizado.getPrecio()),
                () -> assertEquals(funko.getId(), funkoActualizado.getId()),
                () -> assertEquals(funko.getFechaModificacion(), funkoActualizado.getFechaModificacion()),
                () -> assertEquals(funko.getFechaAlta(), funkoActualizado.getFechaAlta())
        );
        verify(validador).validarFunkoUpdateDto(updateDto);
        verify(repository).findById(1L);
        verify(mapper).fromUpdateToEntity(funko, updateDto);
        verify(repository).update(1L, funko);
    }

    @Test
    void updateFunkoNotFound() throws FunkoException {
        doNothing().when(validador).validarFunkoUpdateDto(updateDto);
        when(repository.findById(1L)).thenReturn(null);

        FunkoNotFoundException e = assertThrows(FunkoNotFoundException.class, () -> service.update(1L, updateDto));
        assertEquals("Funko con id 1 no encontrado", e.getMessage());

        verify(validador).validarFunkoUpdateDto(updateDto);
        verify(repository).findById(1L);
        verify(mapper, never()).fromUpdateToEntity(any(), any());
        verify(repository, never()).update(anyLong(), any());
    }

    @Test
    void deleteById() {
        when(repository.deleteById(1L)).thenReturn(funko);

        service.deleteById(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteByIdNotFound() {
        when(repository.deleteById(1L)).thenReturn(null);

        FunkoNotFoundException e = assertThrows(FunkoNotFoundException.class, () -> service.deleteById(1L));
        assertEquals("Funko con id 1 no encontrado", e.getMessage());

        verify(repository).deleteById(1L);
    }
}