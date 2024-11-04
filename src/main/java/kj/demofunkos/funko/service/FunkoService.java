package kj.demofunkos.funko.service;

import kj.demofunkos.categoria.exceptions.CategoriaNotFoundException;
import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.categoria.repository.CategoriaRepository;
import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.exceptions.FunkoNotFoundException;
import kj.demofunkos.funko.mapper.FunkoMapper;
import kj.demofunkos.funko.model.Funko;
import kj.demofunkos.funko.repository.FunkoRepository;
import kj.demofunkos.funko.validator.FunkoValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"funkos"})
public class FunkoService {

    private final FunkoRepository funkoRepository;
    private final CategoriaRepository categoriaRepository;
    private final FunkoMapper mapper;
    private final FunkoValidator funkoValidator;

    @Autowired
    public FunkoService(FunkoRepository funkoRepository, FunkoMapper mapper, FunkoValidator funkoValidator, CategoriaRepository categoriaRepository) {
        this.mapper = mapper;
        this.funkoRepository = funkoRepository;
        this.funkoValidator = funkoValidator;
        this.categoriaRepository = categoriaRepository;
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
                return funkoRepository.save(funkoEntity);
        }
        else {
            throw new FunkoNotFoundException(id);
        }
    }

    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        boolean funko = funkoRepository.existsById(id);
        if (!funko) {
            throw new FunkoNotFoundException(id);
        }
        funkoRepository.deleteById(id);
    }

    @CacheEvict(key = "#id")
    public void deleteLogically(Long id) {
        Optional<Funko> funko = funkoRepository.findById(id);
        if (funko.isEmpty()) {
            throw new FunkoNotFoundException(id);
        }
        funko.get().setBorrado(true);
        funkoRepository.save(funko.get());
    }

    @Cacheable(key = "#id")
    public Funko reactivateFunko(Long id) {
        Optional<Funko> funko = funkoRepository.findById(id);
        if (funko.isEmpty()) {
            throw new FunkoNotFoundException(id);
        }
        funko.get().setBorrado(false);
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

    public boolean existeCateogoriaConNombre(String nombreCategoria){
        String nombreFiltrado = nombreCategoria.trim().toUpperCase();
        if(categoriaRepository.existsByNombre(nombreFiltrado)){
            return true;
        }else{
            return false;
        }
    }
}