package by.gto.equipment.repository;

import by.gto.equipment.model.EquipmentType;
import org.springframework.stereotype.Component;

@Component
public interface EquipmentTypeRepository extends ReferenceBaseRepository<EquipmentType, Integer> {
}