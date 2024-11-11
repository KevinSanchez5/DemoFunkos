package kj.demofunkos.funko.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import kj.demofunkos.categoria.exceptions.CategoriaNotFoundException;
import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.categoria.repository.CategoriaRepository;
import kj.demofunkos.config.websockets.WebSocketConfig;
import kj.demofunkos.config.websockets.WebSocketHandler;
import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.exceptions.FunkoNotFoundException;
import kj.demofunkos.funko.mapper.FunkoMapper;
import kj.demofunkos.funko.model.Funko;
import kj.demofunkos.funko.repository.FunkoRepository;
import kj.demofunkos.funko.validator.FunkoValidator;
import kj.demofunkos.websocket.Notificacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"funkos"})
@Slf4j
public class FunkoService {

    private final FunkoRepository funkoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FunkoMapper mapper;
    private final FunkoValidator funkoValidator;
    private final WebSocketConfig webSocketConfig;
    private WebSocketHandler webSocketService;
    private final ObjectMapper jsonMapper;

    @Autowired
    public FunkoService(FunkoRepository funkoRepository, FunkoMapper mapper, FunkoValidator funkoValidator, CategoriaRepository categoriaRepository, WebSocketConfig webSocketConfig, kj.demofunkos.config.websockets.WebSocketHandler webSocketService) {
        this.mapper = mapper;
        this.funkoRepository = funkoRepository;
        this.funkoValidator = funkoValidator;
        this.categoriaRepository = categoriaRepository;
        this.webSocketConfig = webSocketConfig;
        this.webSocketService = webSocketConfig.webSocketFunkosHandler();
        this.jsonMapper = new ObjectMapper();
        jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS,false);
        jsonMapper.registerModule(new JavaTimeModule());
    }


    public Page<Funko> findAllPaged(Optional<String> nombre, Optional<Boolean> borrado, Optional<Double> precioMinimo, Optional<Double> precioMaximo, Pageable pageable ) {
        Specification<Funko> specNombreCategoria = (root, query, criteriaBuilder) ->
                nombre.map(nom -> criteriaBuilder.like(criteriaBuilder.upper(root.get("nombre")), "%" + nom.toUpperCase() + "%"))
                        .orElseGet(()-> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specActivaCategoria = (root, query, criteriaBuilder) ->
                borrado.map(bor -> criteriaBuilder.equal(root.get("borrado"), bor))
                        .orElseGet(()-> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specPrecioMinimo = (root, query, criteriaBuilder) ->
                precioMinimo.map(pMin -> criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), pMin))
                        .orElseGet(()-> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> specPrecioMaximo = (root, query, criteriaBuilder) ->
                precioMaximo.map(pMax -> criteriaBuilder.lessThanOrEqualTo(root.get("precio"), pMax))
                        .orElseGet(()-> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Funko> criterio = Specification.where(specNombreCategoria).and(specActivaCategoria)
                .and(specPrecioMinimo).and(specPrecioMaximo);

        return funkoRepository.findAll(criterio, pageable);
    }


    public List<Funko> findAll() {
        List<Funko> list;
        list = funkoRepository.findAll();
        return list;
    }

    @Cacheable(key = "#id")
    public Funko findById(Long id) {

        Optional<Funko> funko = funkoRepository.findById(id);
        if (funko.isEmpty()) {
            throw new FunkoNotFoundException(id);
        }
        return funko.get();
    }

    @CachePut(key = "#result.id")
    public Funko save(FunkoCreateDto funkoDto) {
        Optional<Categoria> categoriaParaFunko = categoriaRepository.findByNombreIgnoreCase(funkoDto.getNombreCategoria().trim());
        if(categoriaParaFunko.isEmpty()){
            throw new CategoriaNotFoundException(funkoDto.getNombreCategoria().toUpperCase());
        }else {
            Funko funkoEntity = mapper.fromCreatetoEntity(funkoDto, categoriaParaFunko.get());
            onChange(Notificacion.Tipo.CREATE, funkoEntity);
            return funkoRepository.save(funkoEntity);
        }
    }

    @CachePut(key = "#result.id")
    public Funko update(Long id, FunkoUpdateDto dto) {
        funkoValidator.validarFunkoUpdateDto(dto);
        Optional<Funko> funkoOptional = funkoRepository.findById(id);
        Categoria categoriaFinal= null;

        if (funkoOptional.isPresent()) {
            categoriaFinal = funkoOptional.get().getCategoria();
            if(dto.getNombreCategoria() != null) {
            String nombreCategoria = dto.getNombreCategoria().trim().toUpperCase();
            Optional<Categoria> categoria = categoriaRepository.findByNombreIgnoreCase(nombreCategoria);

                if (categoria.isEmpty()) {
                    throw new CategoriaNotFoundException(nombreCategoria.toUpperCase());
                }else {categoriaFinal = categoria.get();}
            }
            Funko funkoEntity = mapper.fromUpdateToEntity(funkoOptional.get(), dto, categoriaFinal);
            onChange(Notificacion.Tipo.UPDATE, funkoEntity);
                return funkoRepository.save(funkoEntity);
        }
        else {
            throw new FunkoNotFoundException(id);
        }
    }

    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        Optional<Funko> funko = funkoRepository.findById(id);
        if (funko.isEmpty()) {
            throw new FunkoNotFoundException(id);
        }
        onChange(Notificacion.Tipo.DELETE, funko.get());
        funkoRepository.deleteById(id);
    }

    @CacheEvict(key = "#id")
    public void deleteLogically(Long id) {
        Optional<Funko> funko = funkoRepository.findById(id);
        if (funko.isEmpty()) {
            throw new FunkoNotFoundException(id);
        }
        funko.get().setBorrado(true);
        onChange(Notificacion.Tipo.UPDATE, funko.get());
        funkoRepository.save(funko.get());
    }

    @Cacheable(key = "#id")
    public Funko reactivateFunko(Long id) {
        Optional<Funko> funko = funkoRepository.findById(id);
        if (funko.isEmpty()) {
            throw new FunkoNotFoundException(id);
        }
        funko.get().setBorrado(false);
        onChange(Notificacion.Tipo.UPDATE, funko.get());
        return funkoRepository.save(funko.get());
    }


    //BUSQUEDAS EXTRA

    @Cacheable
    public Funko findByNombreIgnoreCase(String nombre) {
        String nombreFiltrado = nombre.trim();
        Optional<Funko> funkito = funkoRepository.findByNombreIgnoreCase(nombreFiltrado);
        if (funkito.isEmpty()) {
            throw new FunkoNotFoundException(nombre);
        }
        return funkito.get();
    }

    public List<Funko> findByPrecioGreaterThan(Double precio) {
        List<Funko> funkosCaros;
        funkosCaros = funkoRepository.findByPrecioGreaterThan(precio);
        return funkosCaros;
    }

    public List<Funko> findByPrecioLessThan(Double precio) {
        List<Funko> funkosBaratos;
        funkosBaratos = funkoRepository.findByPrecioLessThan(precio);
        return funkosBaratos;
    }

    public List<Funko> findByNombreContainingIgnoreCase(String nombre){
        String nombreFiltrado = nombre.trim();
        return funkoRepository.findByNombreContainingIgnoreCase(nombreFiltrado);
    }

    public List<Funko> findByPrecioBetween(Double precioMinimo, Double precioMaximo){
        return funkoRepository.findByPrecioBetween(precioMinimo, precioMaximo);
    }

    void onChange(Notificacion.Tipo tipo, Funko data){
        if(webSocketService == null) {
            log.warn("No se pudo enviar la notificacion al WebSocket");
            webSocketService = this.webSocketConfig.webSocketFunkosHandler();
        }

        try{
            Notificacion<Funko> notificacion = new Notificacion<Funko>(
                    "FUNKO",
                    tipo,
                    data
            );
            String json = jsonMapper.writeValueAsString((notificacion));
            log.info("Enviando mensaje a los clientes ws");
            Thread senderThread = new Thread(() -> {
                try {
                    webSocketService.sendMessage(json);
                } catch (Exception e) {
                    log.error("Error al enviar mensaje por WebSocket", e);
                }
            });
            senderThread.start();
        } catch (JsonProcessingException e){
            log.error("Error al serializar la notificacion", e);
        }

    }

    public void setWebSocketService(WebSocketHandler webSocketService) {
        this.webSocketService = webSocketService;
    }

}