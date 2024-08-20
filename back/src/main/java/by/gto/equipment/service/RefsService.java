package by.gto.equipment.service;

import by.gto.equipment.model.EquipmentType;
import by.gto.equipment.model.EquipmentState;
import by.gto.equipment.model.ResponsiblePerson;
import by.gto.equipment.repository.EquipmentStateRepository;
import by.gto.equipment.repository.EquipmentTypeRepository;
import by.gto.equipment.repository.ResponsiblePersonRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefsService {

    private final EquipmentStateRepository stateRepository;
    private final EquipmentTypeRepository typeRepository;
    private final ResponsiblePersonRepository personRepository;

    @Autowired
    public RefsService(EquipmentStateRepository stateRepository, EquipmentTypeRepository typeRepository, ResponsiblePersonRepository personRepository) {
        this.stateRepository = stateRepository;
        this.typeRepository = typeRepository;
        this.personRepository = personRepository;
    }

    public List<EquipmentState> getStates() {
        return stateRepository.findAll();
    }

    public List<EquipmentType> getTypes() {
        return typeRepository.findAll();
    }

    public List<ResponsiblePerson> getPersons() {
        return personRepository.findAll();
    }
}
