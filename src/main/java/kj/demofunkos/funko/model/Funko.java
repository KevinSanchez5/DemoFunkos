package kj.demofunkos.funko.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Funko {
    private static Long idCounter = 1L;
    private Long id;
    private String nombre;
    private Double precio;
    private LocalDateTime fechaAlta;
    private LocalDateTime fechaModificacion;

    public Funko() {}

    public Funko(String nombre, Double precio) {
        this.id = idCounter++;
        this.nombre = nombre;
        this.precio = precio;
        this.fechaAlta = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }
}
