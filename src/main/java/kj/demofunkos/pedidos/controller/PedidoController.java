package kj.demofunkos.pedidos.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kj.demofunkos.pedidos.models.Pedido;
import kj.demofunkos.pedidos.service.PedidoService;
import kj.demofunkos.utils.pagination.PageResponse;
import kj.demofunkos.utils.pagination.PaginationLinksUtils;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/pedidos")
@Slf4j
public class PedidoController {

    private final PedidoService pedidoService;
    private final PaginationLinksUtils paginationLinksUtils;

    public PedidoController(PedidoService pedidoService, PaginationLinksUtils paginationLinksUtils) {
        this.pedidoService = pedidoService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping()
    public ResponseEntity<PageResponse<Pedido>> getAllPedidos(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        log.info("Recuperando todos los pedidos");
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Pedido> pageResult = pedidoService.findAll(PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> getPedidoById(@PathVariable("id") ObjectId id) {
        log.info("Recuperando pedido con id: {}", id);
        return ResponseEntity.ok(pedidoService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<Pedido> savePedido(@Valid @RequestBody Pedido pedido) {
        log.info("Guardando pedido");
        return ResponseEntity.ok(pedidoService.save(pedido));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable("id") ObjectId id, @Valid @RequestBody Pedido pedido) {
        log.info("Actualizando pedido con id: {}", id);
        return ResponseEntity.ok(pedidoService.update(id, pedido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable("id") ObjectId id) {
        log.info("Borrando pedido con id: {}", id);
        pedidoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
