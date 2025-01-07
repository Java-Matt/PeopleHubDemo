package eng.project.peoplehubdemo.repository;

import eng.project.peoplehubdemo.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonRepository<T extends Person> extends JpaRepository<T, Integer>, JpaSpecificationExecutor<T> {
}
