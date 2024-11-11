package kj.demofunkos.funko.repository;

import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.funko.model.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FunkoRepository extends JpaRepository<Funko, Long>, JpaSpecificationExecutor<Funko> {

    @Query("select f from Funko f where upper(f.nombre) = upper(?1)")
    Optional<Funko> findByNombreIgnoreCase(String nombre);

    @Query("SELECT f FROM Funko f WHERE f.precio > ?1")
    List<Funko> findByPrecioGreaterThan(Double price);

    @Query("SELECT f FROM Funko f WHERE f.precio < ?1")
    List<Funko> findByPrecioLessThan(Double price);

    @Query("SELECT f FROM Funko f WHERE upper(f.nombre) LIKE concat('%', upper(?1), '%') ")
    List<Funko> findByNombreContainingIgnoreCase(String nombre);

    @Query("SELECT f FROM Funko f WHERE f.precio > ?1 AND f.precio < ?2")
    List<Funko> findByPrecioBetween(Double minimo, Double maximo);
}
