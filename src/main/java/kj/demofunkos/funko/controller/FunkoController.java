package kj.demofunkos.funko.controller;


import jakarta.validation.Valid;
import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.model.Funko;
import kj.demofunkos.funko.service.FunkoService;
import kj.demofunkos.utils.pagination.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funkos")
public class FunkoController {

    private final FunkoService funkoService;

    @Autowired
    public FunkoController(FunkoService funkoService) {
        this.funkoService = funkoService;
    }

//    @GetMapping("/paged")
//    public ResponseEntity<PageResponse<Funko>> findAllPaged(
//            @RequestParam(required = false) Optional<String> nombre,
//            @RequestParam(required = false, defaultValue = "false") Optional<Boolean> borrado,
//            @RequestParam(required = false) Optional<Double> precioMaximo,
//            @RequestParam(defaultValue = "0") Integer page,
//            @RequestParam(defaultValue = "10") Integer size
//    ) {
//        return ResponseEntity.ok(funkoService.findAllPaged(page, size));
//    }

    @GetMapping
    public ResponseEntity<List<Funko>> findAll() {
        return ResponseEntity.ok(funkoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funko> findById(@PathVariable Long id) {
        return ResponseEntity.ok(funkoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Funko> save(@Valid @RequestBody FunkoCreateDto funko) {
        return ResponseEntity.status(HttpStatus.CREATED).body(funkoService.save(funko));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Funko> update(@PathVariable Long id, @RequestBody FunkoUpdateDto funko) {
        return ResponseEntity.ok(funkoService.update(id, funko));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        funkoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("{id}/desactivar")
    public ResponseEntity<Void> logicalDeleteById(@PathVariable Long id) {
        funkoService.deleteLogically(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Funko> reactivateFunko(@PathVariable Long id) {
        return ResponseEntity.ok(funkoService.reactivateFunko(id));
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Funko> findByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(funkoService.findByNombreIgnoreCase(nombre));
    }

    @GetMapping("/precio-entre")
    public ResponseEntity<List<Funko>> findByPrecioBetween(@RequestParam Double minimo, @RequestParam Double maximo) {
        return ResponseEntity.ok(funkoService.findByPrecioBetween(minimo, maximo));
    }
}
