package kj.demofunkos.funko.controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.model.Funko;
import kj.demofunkos.funko.service.FunkoService;
import kj.demofunkos.utils.pagination.PageResponse;
import kj.demofunkos.utils.pagination.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/funkos")
public class FunkoController {

    private final FunkoService funkoService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public FunkoController(FunkoService funkoService, PaginationLinksUtils paginationLinksUtils) {
        this.funkoService = funkoService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping("/paged")
    public ResponseEntity<PageResponse<Funko>> findAllPaged(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false, defaultValue = "false") Optional<Boolean> borrado,
            @RequestParam(required = false) Optional<Double> precioMinimo,
            @RequestParam(required = false) Optional<Double> precioMaximo,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Funko> pageResult = funkoService.findAllPaged(nombre, borrado, precioMinimo, precioMaximo, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
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
