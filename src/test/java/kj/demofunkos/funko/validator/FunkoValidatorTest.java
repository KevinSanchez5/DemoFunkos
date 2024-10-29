package kj.demofunkos.funko.validator;

import kj.demofunkos.funko.dto.FunkoUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class FunkoValidatorTest {

    FunkoValidator funkoValidator;

    @BeforeEach
    void setUp() {
         funkoValidator = new FunkoValidator();
    }

    @Test
    void validarFunkoUpdateDtoSuccess() {
        FunkoUpdateDto dto = new FunkoUpdateDto("test", 1.0);

         assertDoesNotThrow(()-> funkoValidator.validarFunkoUpdateDto(dto));
    }

    @Test
    void validarFunkoUpdateAllAtributesNull() {
        FunkoUpdateDto dto = new FunkoUpdateDto(null, null);

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> funkoValidator.validarFunkoUpdateDto(dto));

        assertAll(
                () -> assertEquals("400 BAD_REQUEST \"El nombre y el precio no pueden estar vacíos\"", response.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode())
        );

    }

    @Test
    void validarFunkoPrecioMenorOIgualACero() {
        FunkoUpdateDto dto = new FunkoUpdateDto("test", -1.0);

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> funkoValidator.validarFunkoUpdateDto(dto));

        assertAll(
                () -> assertEquals("400 BAD_REQUEST \"El precio no puede ser menor o igual a 0\"", response.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode())
        );


    }

    @Test
    void validarFunkoNombreVacio() {
        FunkoUpdateDto dto = new FunkoUpdateDto("" , 1.2);

        ResponseStatusException response = assertThrows(ResponseStatusException.class, () -> funkoValidator.validarFunkoUpdateDto(dto));

        assertAll(
                () -> assertEquals("400 BAD_REQUEST \"El nombre no puede estar vacio o ser espacios en blanco\"", response.getMessage()),
                () -> assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode())
        );

    }
}