package by.gto.equipment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipment_state")
@NoArgsConstructor
public class EquipmentState extends ReferenceBase {
    public EquipmentState(int id, String name) {
        super(id, name);
    }
}
