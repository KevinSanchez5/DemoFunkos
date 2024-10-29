package kj.demofunkos.funko.controller;


import jakarta.validation.Valid;
import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.model.Funko;
import kj.demofunkos.funko.service.FunkoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/funkos")
public class FunkoController {

    private final FunkoService funkoService;

    @Autowired
    public FunkoController(FunkoService funkoService) {
        this.funkoService = funkoService;
    }

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

    @DeleteMapping("/logicalDelete/{id}")
    public ResponseEntity<Void> logicalDeleteById(@PathVariable Long id) {
        funkoService.deleteLogically(id);
        return ResponseEntity.noContent().build();
    }
}
