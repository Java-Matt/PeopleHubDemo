package eng.project.peoplehubdemo.mapper;

import eng.project.peoplehubdemo.model.Person;
import eng.project.peoplehubdemo.model.Student;
import eng.project.peoplehubdemo.model.dto.PersonDto;
import eng.project.peoplehubdemo.model.dto.StudentDto;
import eng.project.peoplehubdemo.model.view.PersonView;
import eng.project.peoplehubdemo.model.view.StudentView;
import eng.project.peoplehubdemo.strategy.PersonStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonMapperTest {
    @InjectMocks
    private PersonMapper personMapper;
    @Mock
    private Map<String, PersonStrategy> personMapperStrategy;
    @Mock
    private PersonStrategy personStrategy;

    private Student student;
    private StudentView studentView;
    private StudentDto studentDto;

    private String type;

    @BeforeEach
    public void init() {
        int id = 1;
        type = "STUDENT";
        String firstName = "Adam";
        String lastName = "Nowak";
        String personalNumber = "97051412345";
        int height = 180;
        int weight = 75;
        String email = "adam@nowak.com";
        student = Student.builder()
                .id(id)
                .type(type)
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
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
        studentView = StudentView.builder()
                .id(id)
                .type(type)
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
                .build();
    }

    @Test
    void testMapToDto() {
        when(personMapperStrategy.get(type)).thenReturn(personStrategy);
        when(personStrategy.mapToDto(any(Person.class))).thenReturn(studentDto);

        PersonDto result = personMapper.mapToDto(student);

        assertEquals(studentDto, result);
    }

    @Test
    void testMapViewToDto() {
        when(personMapperStrategy.get(type)).thenReturn(personStrategy);
        when(personStrategy.mapViewToDto(any(PersonView.class))).thenReturn(studentDto);

        PersonDto result = personMapper.mapViewToDto(studentView);

        assertEquals(studentDto, result);
    }
}