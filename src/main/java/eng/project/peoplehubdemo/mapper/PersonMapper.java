package eng.project.peoplehubdemo.mapper;

import eng.project.peoplehubdemo.model.Person;
import eng.project.peoplehubdemo.model.dto.PersonDto;
import eng.project.peoplehubdemo.model.view.PersonView;
import eng.project.peoplehubdemo.strategy.PersonStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersonMapper {

    private final Map<String, PersonStrategy> personMapperStrategy;

    public PersonDto mapToDto(Person person) {
        PersonStrategy mapperStrategy = personMapperStrategy.get(person.getType());
        return mapperStrategy.mapToDto(person);
    }

    public PersonDto mapViewToDto(PersonView personView) {
        PersonStrategy mapperStrategy = personMapperStrategy.get(personView.getType());
        return mapperStrategy.mapViewToDto(personView);
    }
}
