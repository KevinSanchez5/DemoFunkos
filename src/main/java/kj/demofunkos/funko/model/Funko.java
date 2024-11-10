package kj.demofunkos.funko.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import kj.demofunkos.categoria.models.Categoria;
import lombok.AllArgsConstructor;

import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Entity
@Table(name = "funkos")
public class Funko {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @Column(name = "precio")
    @Positive(message = "El precio debe ser mayor que 0")
    private Double precio;

    @Column(name = "stock")
    @PositiveOrZero(message = "El stock debe ser mayor o igual que 0")
    private Integer stock;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "borrado")
    private boolean borrado = false;

    @Embedded
    private Detalles detalles;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    @JsonIgnoreProperties("funkos")
    private Categoria categoria;

    public Funko() {}

    public Funko(String nombre, Double precio, Integer stock, Categoria categoria) {
        this.id = null;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.fechaAlta = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
        this.borrado = false;
        this.detalles = new Detalles();
        this.categoria = categoria;
    }
}
