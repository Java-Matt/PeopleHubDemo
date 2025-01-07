package eng.project.peoplehubdemo.service;

import eng.project.peoplehubdemo.exception.DataAlreadyModifiedException;
import eng.project.peoplehubdemo.exception.PersonNotFoundException;
import eng.project.peoplehubdemo.mapper.PersonMapper;
import eng.project.peoplehubdemo.model.Person;
import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.model.dto.PersonDto;
import eng.project.peoplehubdemo.model.view.PersonView;
import eng.project.peoplehubdemo.repository.PersonRepository;
import eng.project.peoplehubdemo.repository.PersonViewRepository;
import eng.project.peoplehubdemo.specification.PersonSpecificationBuilder;
import eng.project.peoplehubdemo.strategy.PersonStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersonService {
    private final PersonRepository<Person> personRepository;
    private final PersonViewRepository personViewRepository;
    private final Map<String, PersonStrategy> strategyMap;
    private final PersonMapper personMapper;

    @Transactional(readOnly = true)
    public Page<PersonDto> findAll(String search, Pageable pageable) {
        PersonSpecificationBuilder builder = new PersonSpecificationBuilder(strategyMap);
        Specification<PersonView> specification = builder.fromSearch(search).build();

        return personViewRepository.findAll(specification, pageable)
                .map(personMapper::mapViewToDto);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public PersonDto create(CreatePersonCommand command) {
        PersonStrategy creationStrategy = strategyMap.get(command.getType());
        Person person = creationStrategy.create(command);
        personRepository.save(person);
        return personMapper.mapToDto(person);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public PersonDto updateById(int id, UpdatePersonCommand command) {
        Person person = personRepository.findById(id).
                orElseThrow(() -> new PersonNotFoundException(MessageFormat
                        .format("Person with id={0} not found", id)));
        Person cloned = person.clone();
        PersonStrategy updateStrategy = strategyMap.get(cloned.getType());
        updateStrategy.update(cloned, command);
        try {
            return personMapper.mapToDto(personRepository.save(cloned));
        } catch (OptimisticLockingFailureException e) {
            throw new DataAlreadyModifiedException("Data Already Modified!");
        }
    }
}
