package eng.project.peoplehubdemo.strategy;

import eng.project.peoplehubdemo.model.FieldType;
import eng.project.peoplehubdemo.model.Person;
import eng.project.peoplehubdemo.model.SearchCriteria;
import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.model.dto.PersonDto;
import eng.project.peoplehubdemo.model.view.PersonView;
import org.springframework.data.jpa.domain.Specification;

import java.util.Map;

public interface PersonStrategy {
    Person create(CreatePersonCommand command);

    PersonDto mapToDto(Person person);

    PersonDto mapViewToDto(PersonView personView);

    void update(Person person, UpdatePersonCommand command);

    Map<String, FieldType> getPersonExtensionFields();

    Specification<PersonView> getSpecification(SearchCriteria criteria);
}
