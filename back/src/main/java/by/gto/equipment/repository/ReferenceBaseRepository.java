package by.gto.equipment.repository;

import by.gto.equipment.model.ReferenceBase;
import java.io.Serializable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ReferenceBaseRepository<T extends ReferenceBase, ID extends Serializable> extends JpaRepository<T, ID> {
    List<T> findByName(String name);
}