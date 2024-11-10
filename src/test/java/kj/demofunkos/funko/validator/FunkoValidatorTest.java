package kj.demofunkos.funko.validator;

import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.exceptions.FunkoBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FunkoValidatorTest {

    FunkoValidator funkoValidator;

    @BeforeEach
    void setUp() {
         funkoValidator = new FunkoValidator();
    }

    @Test
    void validarFunkoUpdateDtoSuccess() {
        FunkoUpdateDto dto = new FunkoUpdateDto("test", 1.0, 10, null, null, null);

         assertDoesNotThrow(()-> funkoValidator.validarFunkoUpdateDto(dto));
    }

    @Test
    void validarFunkoUpdateAllAtributesNull() {
        FunkoUpdateDto dto = new FunkoUpdateDto(null, null, null, null, null,null);

        FunkoBadRequestException response = assertThrows(FunkoBadRequestException.class, () -> funkoValidator.validarFunkoUpdateDto(dto));

        assertAll(
                () -> assertEquals("El nombre y el precio no pueden estar vacíos", response.getMessage())
        );

    }

    @Test
    void validarFunkoPrecioMenorOIgualACero() {
        FunkoUpdateDto dto = new FunkoUpdateDto("test", -1.0, 10, null, null, null);

        FunkoBadRequestException response = assertThrows(FunkoBadRequestException.class, () -> funkoValidator.validarFunkoUpdateDto(dto));

        assertAll(
                () -> assertEquals("El precio no puede ser menor o igual a 0", response.getMessage())
        );


    }

    @Test
    void validarFunkoNombreVacio() {
        FunkoUpdateDto dto = new FunkoUpdateDto("" , 1.2, 10, null , null, null);

        FunkoBadRequestException response = assertThrows(FunkoBadRequestException.class, () -> funkoValidator.validarFunkoUpdateDto(dto));

        assertAll(
                () -> assertEquals("El nombre no puede estar vacio o ser espacios en blanco", response.getMessage())
        );

    }

    @Test
    void validarFunkoDescripcionVacia() {
        FunkoUpdateDto dto = new FunkoUpdateDto("test", 1.2, 10, "", null, null);

        FunkoBadRequestException response = assertThrows(FunkoBadRequestException.class, () -> funkoValidator.validarFunkoUpdateDto(dto));

        assertAll(
                () -> assertEquals("La descripcion no puede estar vacia o ser espacios en blanco", response.getMessage())
        );
    }

    @Test
    void validarFunkoStockMenorQueCero() {
        FunkoUpdateDto dto = new FunkoUpdateDto("test", 1.2, -1, null, null, null);

        FunkoBadRequestException response = assertThrows(FunkoBadRequestException.class, () -> funkoValidator.validarFunkoUpdateDto(dto));

        assertAll(
                () -> assertEquals("El stock no puede ser menor a 0", response.getMessage())
        );
    }

}