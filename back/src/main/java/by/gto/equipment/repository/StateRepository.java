package by.gto.equipment.repository;

import by.gto.equipment.model.State;
import java.util.List;
import org.springframework.data.repository.Repository;

public interface StateRepository extends Repository<State, Integer> {
    List<State> findAll();
    List<State> findByName(String name);
    State findById(Integer id);
    State save(State state);
    int delete(State state);
    int deleteById(Integer id);
}
