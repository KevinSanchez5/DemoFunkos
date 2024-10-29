package kj.demofunkos.funko.service;

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

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = {"funkos"})
public class FunkoService {

    private final FunkoRepository funkoRepository;
    private final FunkoMapper mapper;
    private final FunkoValidator funkoValidator;

    @Autowired
    public FunkoService(FunkoRepository funkoRepository, FunkoMapper mapper, FunkoValidator funkoValidator) {
        this.mapper = mapper;
        this.funkoRepository = funkoRepository;
        this.funkoValidator = funkoValidator;
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
            throw new FunkoNotFoundException(id.toString());
        }
        return funko.get();
    }

    @CachePut(key = "#result.id")
    public Funko save(FunkoCreateDto funkoDto) {
        Funko funkoEntity = mapper.fromCreatetoEntity(funkoDto);
        return funkoRepository.save(funkoEntity);
    }

    @CachePut(key = "#result.id")
    public Funko update(Long id, FunkoUpdateDto dto) {
        funkoValidator.validarFunkoUpdateDto(dto);
        Optional<Funko> funkoOptional = funkoRepository.findById(id);
        if (funkoOptional.isEmpty()) {
            throw new FunkoNotFoundException(id.toString());
        }
        Funko funkoEntity = mapper.fromUpdateToEntity(funkoOptional.get(), dto);
        return funkoRepository.save(funkoEntity);
    }

    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        boolean funko = funkoRepository.existsById(id);
        if (!funko) {
            throw new FunkoNotFoundException(id.toString());
        }
        funkoRepository.deleteById(id);
    }

    @CacheEvict(key = "#id")
    public void deleteLogically(Long id){
        Optional<Funko> funko = funkoRepository.findById(id);
        if (funko.isEmpty()) {
            throw new FunkoNotFoundException(id.toString());
        }
        funko.get().setBorrado(true);
        funkoRepository.save(funko.get());
    }
}
