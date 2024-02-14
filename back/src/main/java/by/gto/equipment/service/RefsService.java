package by.gto.equipment.service;

import by.gto.equipment.model.State;
import by.gto.equipment.repository.StateRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RefsService {

    private StateRepository stateRepository;
    public List<State> getStates() {
        return stateRepository.findAll();
    }
}
