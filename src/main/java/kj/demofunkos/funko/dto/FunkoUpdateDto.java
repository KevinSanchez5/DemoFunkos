package kj.demofunkos.funko.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class FunkoUpdateDto {

    private  String nombre;

    private  Double precio;

    private String descripcion;

    @PastOrPresent(message = "La fecha de fabricación no puede ser futura")
    private LocalDate fechaDeFabricacion;

    public FunkoUpdateDto() {}


    public FunkoUpdateDto(String nombre, Double precio, String descripcion, LocalDate fechaDeFabricacion) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.fechaDeFabricacion = fechaDeFabricacion;
    }
}
