package by.gto.equipment.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * POJO для базовой записи справочника.
 */
@MappedSuperclass
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceBase {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    private String name;
}
