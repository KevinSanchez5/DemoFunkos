package kj.demofunkos.categoria.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import kj.demofunkos.funko.model.Funko;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nombre", unique = true, nullable = false)
    @NotBlank(message = "El nombre no puede estar vacio" )
    private String nombre;

    @Column(name = "fecha_alta")
    private LocalDateTime fechaAlta;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "activa")
    private boolean activa;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("categoria")
    private List<Funko> funkos;

    public Categoria(String nombre) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.fechaAlta = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
        this.activa = true;
    }

    public Categoria() {}

}
