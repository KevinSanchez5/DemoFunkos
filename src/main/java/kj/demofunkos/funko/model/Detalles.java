package kj.demofunkos.funko.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Embeddable
@Data
@AllArgsConstructor
public class Detalles {

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fecha_de_fabricacion")
    private LocalDate fechaDeFabricacion;

    public Detalles() {
        this.descripcion = null;
        this.fechaDeFabricacion = null;
    }

    public Detalles(String descripcion, String localDateString) {
        this.descripcion = descripcion;
        this.fechaDeFabricacion = LocalDate.parse(localDateString);
    }
}
