package kj.demofunkos.categoria.repository;

import kj.demofunkos.categoria.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    @Query("SELECT c FROM Categoria c WHERE upper(c.nombre) = upper(?1)")
    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    @Query("SELECT COUNT(c) > 0 FROM Categoria c WHERE c.nombre = :nombre")
    boolean existsByNombre(String nombre);

}
