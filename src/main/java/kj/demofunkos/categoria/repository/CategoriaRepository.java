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

    @Query("SELECT c FROM Categoria c order by c.nombre asc")
    List<Categoria> findAllByOrderByNombreAsc();

    @Query("SELECT c FROM Categoria c WHERE c.nombre = ?1")
    Optional<Categoria> findByNombre(String nombre);

    @Query("SELECT COUNT(c) > 0 FROM Categoria c WHERE c.nombre = :nombre")
    boolean existsByNombre(String nombre);

}
