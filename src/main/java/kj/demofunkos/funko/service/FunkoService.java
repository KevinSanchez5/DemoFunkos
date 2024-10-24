package kj.demofunkos.funko.service;

import kj.demofunkos.funko.dto.FunkoCreateDto;
import kj.demofunkos.funko.dto.FunkoUpdateDto;
import kj.demofunkos.funko.exceptions.FunkoNotFoundException;
import kj.demofunkos.funko.mapper.FunkoMapper;
import kj.demofunkos.funko.model.Funko;
import kj.demofunkos.funko.repository.FunkoRepository;
import kj.demofunkos.funko.validator.Validador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = {"funkos"})
public class FunkoService {

    private final FunkoRepository funkoRepository;
    private final FunkoMapper mapper;
    private final Validador validador;

    @Autowired
    public FunkoService(FunkoRepository funkoRepository, FunkoMapper mapper, Validador validador) {
        this.mapper = mapper;
        this.funkoRepository = funkoRepository;
        this.validador = validador;
    }

    public List<Funko> findAll() {
         List<Funko> list;
            list = funkoRepository.findAll();
            return list;
    }

    @Cacheable(key = "#id")
    public Funko findById(Long id) {

        Funko funko = funkoRepository.findById(id);
        if (funko == null) {
            throw new FunkoNotFoundException(id.toString());
        }
        return funko;
    }

    @CachePut(key = "#result.id")
    public Funko save(FunkoCreateDto funko) {
        Funko funkoEntity = mapper.fromCreatetoEntity(funko);
        return funkoRepository.save(funkoEntity);
    }

    @CachePut(key = "#result.id")
    public Funko update(Long id, FunkoUpdateDto dto) {
        validador.validarFunkoUpdateDto(dto);
        Funko funkoOptional = funkoRepository.findById(id);
        if (funkoOptional == null ) {
            throw new FunkoNotFoundException(id.toString());
        }
        Funko funkoEntity = mapper.fromUpdateToEntity(funkoOptional, dto);
        return funkoRepository.update(id, funkoEntity);
    }

    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        Funko funko = funkoRepository.deleteById(id);
        if (funko == null) {
            throw new FunkoNotFoundException(id.toString());
        }
    }



}
