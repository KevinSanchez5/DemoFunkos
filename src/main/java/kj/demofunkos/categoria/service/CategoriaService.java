package kj.demofunkos.categoria.service;

import kj.demofunkos.categoria.dto.CategoriaCreateDto;
import kj.demofunkos.categoria.exceptions.CategoriaBadRequestException;
import kj.demofunkos.categoria.exceptions.CategoriaNotFoundException;
import kj.demofunkos.categoria.mapper.CategoriaMapper;
import kj.demofunkos.categoria.models.Categoria;
import kj.demofunkos.categoria.repository.CategoriaRepository;
import kj.demofunkos.funko.exceptions.FunkoNotFoundException;
import kj.demofunkos.funko.model.Funko;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@CacheConfig(cacheNames = {"categorias", "funkos"})
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper mapper;

    @Autowired
    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper mapper) {
        this.mapper = mapper;
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> findAll (){
        List<Categoria> list;
        list = categoriaRepository.findAll();
        return list;
    }

    @Cacheable(key = "#id")
    public Categoria findById(UUID id) {
        Optional<Categoria> categoria = categoriaRepository.findById(id);

        if (categoria.isEmpty()){
            throw new CategoriaNotFoundException(id);
        }
        return categoria.get();
    }

    @CachePut(key = "#result.id")
    public Categoria save(CategoriaCreateDto dto){
        String nombre = dto.getNombre().trim().toUpperCase();
        if(categoriaRepository.existsByNombre(nombre)){
            throw new CategoriaBadRequestException(nombre);
        }
        Categoria categoria = mapper.fromDtoToEntity(dto);
        return categoriaRepository.save(categoria);
    }

    @CachePut(key = "#result.id")
    public Categoria update (UUID id, CategoriaCreateDto dto){
        String nombre = dto.getNombre().trim().toUpperCase();
        Optional<Categoria> optionalCategoria = categoriaRepository.findById(id);
        if (categoriaRepository.existsByNombre(nombre)){
            throw new CategoriaBadRequestException(nombre);
        }else if(optionalCategoria.isEmpty()){
            throw new CategoriaNotFoundException(id);
        }
        Categoria categoriaVieja = optionalCategoria.get();
        Categoria categoriaNueva = mapper.updateCategoriaFromDtoToEntity(dto, categoriaVieja);
        return categoriaRepository.save(categoriaNueva);
    }

    @CacheEvict(key = "#id")
    public void deactivateCategoria(UUID id){
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isEmpty()){
            throw new CategoriaNotFoundException(id.toString());
        }
        categoria.get().setActiva(false);
        categoriaRepository.save(categoria.get());
    }

    @Cacheable(key = "#id")
    public Categoria reactivateCategoria(UUID id){
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isEmpty()){
            throw new CategoriaNotFoundException(id.toString());
        }
        categoria.get().setActiva(true);
        return categoriaRepository.save(categoria.get());
    }

    @CacheEvict(key = "#id")
    public void deleteById(UUID id){
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isEmpty()){
            throw new CategoriaNotFoundException(id);
        }
        for(Funko funko: categoria.get().getFunkos()){
            evictFunkoFromCache(funko.getId());
        }
        categoriaRepository.deleteById(id);

    }

    @Cacheable(key = "#id")
    public Categoria findByNombre(String nombre) {
        String nombreFiltrado = nombre.trim().toUpperCase();
        Optional<Categoria> categoria = categoriaRepository.findByNombreIgnoreCase(nombreFiltrado);

        if (categoria.isEmpty()){
            throw new CategoriaNotFoundException(nombreFiltrado.toUpperCase());
        }
        return categoria.get();
    }

    @CacheEvict(value = "funkos", key = "#funkoId")
    public void evictFunkoFromCache(Long funkoId) {
    }

}
