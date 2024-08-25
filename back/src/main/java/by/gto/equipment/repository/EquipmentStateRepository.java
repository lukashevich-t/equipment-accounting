package by.gto.equipment.repository;

import by.gto.equipment.model.EquipmentState;
import org.springframework.stereotype.Component;

@Component
public interface EquipmentStateRepository extends ReferenceBaseRepository<EquipmentState, Integer> {
}
