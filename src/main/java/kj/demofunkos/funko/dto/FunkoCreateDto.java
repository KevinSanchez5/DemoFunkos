package kj.demofunkos.funko.dto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FunkoCreateDto {

    @NotBlank(message = "El nombre no puede estar en blanco")
    @NotEmpty(message = "El nombre no puede estar vacio")
    @NotNull(message = "El nombre no puede ser nulo")
    private final String nombre;

    @Positive(message = "El precio debe ser mayor que 0")
    @NotNull(message = "El precio no puede ser nulo")
    private final Double precio;

    private String descripcion;

    @PastOrPresent(message = "La fecha de fabricación no puede ser futura")
    private LocalDate fechaDeFabricacion;

    @NotNull(message = "El nombre de la categoria debe ingresarse")
    @NotEmpty(message = "El nombre de la categoria no puede estar vacio")
    @NotBlank(message = "El nombre de la categoria no puede estar en blanco")
    private String nombreCategoria;



    public FunkoCreateDto(String name, Double price, String descripcion, LocalDate fechaDeFabricacion, String nombreCategoria) {
        this.nombre = name;
        this.precio = price;
        this.descripcion = descripcion;
        this.fechaDeFabricacion = fechaDeFabricacion;
        this.nombreCategoria = nombreCategoria;
    }
}
