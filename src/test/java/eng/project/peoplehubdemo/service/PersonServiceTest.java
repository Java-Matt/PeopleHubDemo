package eng.project.peoplehubdemo.service;

import eng.project.peoplehubdemo.exception.DataAlreadyModifiedException;
import eng.project.peoplehubdemo.exception.PersonNotFoundException;
import eng.project.peoplehubdemo.mapper.PersonMapper;
import eng.project.peoplehubdemo.model.Person;
import eng.project.peoplehubdemo.model.Student;
import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.model.dto.PersonDto;
import eng.project.peoplehubdemo.model.dto.StudentDto;
import eng.project.peoplehubdemo.model.view.PersonView;
import eng.project.peoplehubdemo.model.view.StudentView;
import eng.project.peoplehubdemo.repository.PersonRepository;
import eng.project.peoplehubdemo.repository.PersonViewRepository;
import eng.project.peoplehubdemo.strategy.PersonStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {
    @InjectMocks
    private PersonService service;
    @Mock
    private PersonRepository<Person> personRepository;
    @Mock
    private PersonViewRepository personViewRepository;
    @Mock
    private Map<String, PersonStrategy> creationStrategy;
    @Mock
    private PersonStrategy strategy;
    @Mock
    private PersonMapper personMapper;

    private Student student;
    private StudentView studentView;
    private CreatePersonCommand command;
    private StudentDto studentDto;

    private int id;
    private String firstName;
    private String type;

    @BeforeEach
    public void init() {
        int height = 180;
        int weight = 75;
        String lastName = "Nowak";
        String personalNumber = "97051412345";
        String email = "adam@nowak.com";
        id = 1;
        type = "STUDENT";
        firstName = "Adam";
        student = Student.builder()
                .id(id)
                .type(type)
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
                .version(0)
                .build();
        studentView = StudentView.builder()
                .id(id)
                .type(type)
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
                .version(0)
                .build();
        studentDto = StudentDto.builder()
                .id(id)
                .type(type)
                .firstName(firstName)
                .lastName(lastName)
                .height(height)
                .weight(weight)
                .email(email)
                .build();
        command = new CreatePersonCommand()
                .setType(type)
                .setParams(Map.of("firstName", firstName,
                        "lastName", lastName,
                        "height", String.valueOf(height),
                        "weight", String.valueOf(weight),
                        "personalNumber", personalNumber,
                        "email", email));
    }

    @Test
    void testFindAll_ResultsInFindingAllPersons() {
        String search = "firstName:\"ADAM\"";
        Pageable pageable = PageRequest.of(0, 10);
        Page<PersonView> personPage = new PageImpl<>(Collections.singletonList(studentView));
        when(personViewRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(personPage);
        when(personMapper.mapViewToDto(any(PersonView.class))).thenReturn(studentDto);

        Page<PersonDto> result = service.findAll(search, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(firstName, result.getContent().get(0).getFirstName());
        verify(personViewRepository).findAll(any(Specification.class), eq(pageable));
        verify(personMapper).mapViewToDto(studentView);
    }

    @Test
    void testCreate_ResultsInPersonBeingCreated() {
        when(strategy.create(any(CreatePersonCommand.class))).thenReturn(student);
        when(creationStrategy.get(command.getType())).thenReturn(strategy);
        when(personRepository.save(any(Person.class))).thenReturn(student);
        when(personMapper.mapToDto(any(Person.class))).thenReturn(studentDto);

        PersonDto saved = service.create(command);

        assertEquals(studentDto, saved);
        assertEquals(studentDto.getType(), saved.getType());
    }

    @Test
    void testUpdateById_ResultsInPersonDataBeingUpdated() {
        String newName = "Abraham";
        String newLastName = "Lincoln";
        UpdatePersonCommand updatePersonCommand = new UpdatePersonCommand()
                .setVersion(0)
                .setParams(Map.of("firstName", newName, "lastName", newLastName));

        Person clonedStudent = student.clone();
        clonedStudent.setFirstName(newName);
        clonedStudent.setLastName(newLastName);

        StudentDto updatedDto = studentDto;
        updatedDto.setFirstName(newName);
        updatedDto.setLastName(newLastName);

        when(creationStrategy.get(type)).thenReturn(strategy);
        when(personRepository.findById(id)).thenReturn(Optional.of(student));
        when(personRepository.save(any(Student.class))).thenReturn((Student) clonedStudent);
        when(personMapper.mapToDto(any(Student.class))).thenReturn(updatedDto);

        PersonDto result = service.updateById(id, updatePersonCommand);

        assertEquals(newName, result.getFirstName());
        assertEquals(newLastName, result.getLastName());
        verify(personRepository).findById(id);
        verify(personRepository).save(any(Student.class));
        verify(personMapper).mapToDto(any(Student.class));
    }

    @Test
    void testUpdateById_ShouldThrowPersonNotFoundException_WhenPersonWithGivenIdNotInDb() {
        String newName = "Abraham";
        UpdatePersonCommand updatePersonCommand = new UpdatePersonCommand()
                .setParams(Map.of("firstName", newName));
        when(personRepository.findById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PersonNotFoundException.class, () -> service.updateById(id, updatePersonCommand));

        assertEquals("Person with id=" + student.getId() + " not found", exception.getMessage());
    }

    @Test
    void updateById_shouldThrowConcurrentModificationException_whenOptimisticLockingFailureOccurs() {
        UpdatePersonCommand command = new UpdatePersonCommand();
        student.setId(3);
        student.setType("NEW");

        when(personRepository.findById(anyInt())).thenReturn(Optional.of(student));
        when(creationStrategy.get(any())).thenReturn(strategy);
        doThrow(new OptimisticLockingFailureException("")).when(personRepository).save(any(Person.class));

        Exception exception = assertThrows(DataAlreadyModifiedException.class, () -> service.updateById(id, command));

        assertEquals("Data Already Modified!", exception.getMessage());
    }
}

