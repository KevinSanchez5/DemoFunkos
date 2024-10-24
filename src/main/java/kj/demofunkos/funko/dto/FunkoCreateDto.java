package kj.demofunkos.funko.dto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class FunkoCreateDto {
    @NotBlank(message = "El nombre no puede estar en blanco")
    @NotEmpty(message = "El nombre no puede estar vacio")
    @NotNull(message = "El nombre no puede ser nulo")
    private final String nombre;
    @Positive(message = "El precio debe ser mayor que 0")
    @NotNull(message = "El precio no puede ser nulo")
    private final Double precio;

    public FunkoCreateDto(String name, Double price) {
        this.nombre = name;
        this.precio = price;
    }
}
