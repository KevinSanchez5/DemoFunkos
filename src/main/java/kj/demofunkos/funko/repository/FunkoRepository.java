package kj.demofunkos.funko.repository;

import kj.demofunkos.funko.model.Funko;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FunkoRepository {

    private Map<Long,Funko> funkos = new HashMap<>();

    public List<Funko> findAll() {
        return List.copyOf(funkos.values());
    }

    public Funko findById(Long id) {
        return funkos.get(id);
    }

    public Funko save(Funko funko) {
        funkos.put(funko.getId(), funko);
        return funko;
    }

    public Funko update(Long id, Funko funko) {
        funkos.put(id, funko);
        return funko;
    }

    public Funko deleteById(Long id) {
        return funkos.remove(id);
    }


}
