package kj.demofunkos.funko.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class Detalles {
    private String descripcion;

    private LocalDate fechaDeFabricacion;
}
