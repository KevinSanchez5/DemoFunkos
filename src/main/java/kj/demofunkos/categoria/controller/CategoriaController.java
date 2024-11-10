package kj.demofunkos.categoria.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kj.demofunkos.categoria.dto.CategoriaCreateDto;
import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.categoria.service.CategoriaService;
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
import java.util.UUID;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public CategoriaController(CategoriaService categoriaService, PaginationLinksUtils paginationLinksUtils) {
        this.categoriaService = categoriaService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping("/paged")
    public ResponseEntity<PageResponse<Categoria>> findAllPaged(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false, defaultValue = "true") Optional<Boolean> activa,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nombre") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Categoria> pageResult = categoriaService.findAllPaged(nombre, activa, PageRequest.of(pageNumber, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
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

    @DeleteMapping("/realDelete/{id}")
    public ResponseEntity<Void> realDeleteById(@PathVariable UUID id) {
        categoriaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> deactivateCategoria(@PathVariable UUID id) {
        categoriaService.deactivateCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Categoria> reactivateCategoria(@PathVariable UUID id) {
        return ResponseEntity.ok(categoriaService.reactivateCategoria(id));
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Categoria> findByNombre(@PathVariable String nombre) {
        return ResponseEntity.ok(categoriaService.findByNombre(nombre));
    }
}
