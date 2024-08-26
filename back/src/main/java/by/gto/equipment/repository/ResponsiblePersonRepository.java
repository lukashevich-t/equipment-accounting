package by.gto.equipment.repository;

import by.gto.equipment.model.ResponsiblePerson;
import org.springframework.stereotype.Component;

@Component
public interface ResponsiblePersonRepository extends ReferenceBaseRepository<ResponsiblePerson, Integer> {
}
