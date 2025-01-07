package eng.project.peoplehubdemo.repository;

import eng.project.peoplehubdemo.model.view.PersonView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonViewRepository extends JpaRepository<PersonView, Integer>, JpaSpecificationExecutor<PersonView> {
}
