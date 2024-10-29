package kj.demofunkos.categoria.controller;

import jakarta.validation.Valid;
import kj.demofunkos.categoria.dto.CategoriaCreateDto;
import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.categoria.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @Autowired
    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<List<Categoria>> findAll() {
        return ResponseEntity.ok(categoriaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categoria> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(categoriaService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Categoria> save(@Valid @RequestBody CategoriaCreateDto categoria) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaService.save(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> update(@PathVariable UUID id, @Valid @RequestBody CategoriaCreateDto categoria) {
        return ResponseEntity.ok(categoriaService.update(id, categoria));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable UUID id) {
        categoriaService.deactivateCategoria(id);
        return ResponseEntity.noContent().build();
    }


    //Metodo deshabilitado temporalmente
//    @DeleteMapping("/realDelete/{id}")
//    public ResponseEntity<Void> realDeleteById(@PathVariable UUID id) {
//        categoriaService.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
}
