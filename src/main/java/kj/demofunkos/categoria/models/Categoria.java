package kj.demofunkos.categoria.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
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

    public Categoria(String nombre) {
        this.id = UUID.randomUUID();
        this.nombre = nombre;
        this.fechaAlta = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
        this.activa = true;
    }

    public Categoria() {}
}
