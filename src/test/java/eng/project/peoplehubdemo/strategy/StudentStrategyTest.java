package eng.project.peoplehubdemo.strategy;

import eng.project.peoplehubdemo.model.FieldType;
import eng.project.peoplehubdemo.model.Student;
import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.model.dto.StudentDto;
import eng.project.peoplehubdemo.model.view.PersonView;
import eng.project.peoplehubdemo.model.view.StudentView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StudentStrategyTest {
    @InjectMocks
    private StudentStrategy studentStrategy;

    private CreatePersonCommand createPersonCommand;
    private Student student;
    private StudentView studentView;

    private int id;
    private int height;
    private int weight;
    private String firstName;
    private String lastName;
    private String type;
    private String personalNumber;
    private String email;
    private String university;
    private String fieldOfStudy;
    private double scholarship;
    private int yearOfStudy;

    @BeforeEach
    void setUp() {
        id = 1;
        firstName = "Adam";
        lastName = "Nowak";
        personalNumber = "97051412345";
        height = 180;
        weight = 75;
        email = "adam@nowak.com";
        type = "STUDENT";
        university = "University ABC";
        yearOfStudy = 2;
        fieldOfStudy = "Something Interesting";
        scholarship = 2345;
        Map<String, String> params = new HashMap<>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("personalNumber", personalNumber);
        params.put("height", String.valueOf(height));
        params.put("weight", String.valueOf(weight));
        params.put("email", email);
        params.put("university", university);
        params.put("yearOfStudy", String.valueOf(yearOfStudy));
        params.put("fieldOfStudy", fieldOfStudy);
        params.put("scholarship", String.valueOf(scholarship));

        createPersonCommand = new CreatePersonCommand()
                .setType(type)
                .setParams(params);

        student = Student.builder()
                .id(id)
                .type(type)
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
                .university(university)
                .yearOfStudy(yearOfStudy)
                .fieldOfStudy(fieldOfStudy)
                .scholarship(scholarship)
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
                .university(university)
                .yearOfStudy(yearOfStudy)
                .fieldOfStudy(fieldOfStudy)
                .scholarship(scholarship)
                .build();
    }

    @Test
    void testCreate_ResultsInStudentBeingCreated() {
        Student result = studentStrategy.create(createPersonCommand);

        assertNotNull(result);
        assertEquals(type, result.getType());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(personalNumber, result.getPersonalNumber());
        assertEquals(height, result.getHeight());
        assertEquals(weight, result.getWeight());
        assertEquals(email, result.getEmail());
        assertEquals(university, result.getUniversity());
        assertEquals(yearOfStudy, result.getYearOfStudy());
        assertEquals(fieldOfStudy, result.getFieldOfStudy());
        assertEquals(scholarship, result.getScholarship());
    }

    @Test
    void testMapToDto_ResultsInStudentBeingMappedToDto() {
        StudentDto result = studentStrategy.mapToDto(student);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(type, result.getType());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(height, result.getHeight());
        assertEquals(weight, result.getWeight());
        assertEquals(email, result.getEmail());
        assertEquals(university, result.getUniversity());
        assertEquals(yearOfStudy, result.getYearOfStudy());
        assertEquals(fieldOfStudy, result.getFieldOfStudy());
        assertEquals(scholarship, result.getScholarship());
    }

    @Test
    void testMapViewToDto_ResultsInStudentViewBeingMappedToDto() {
        StudentDto result = studentStrategy.mapViewToDto(studentView);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(type, result.getType());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(height, result.getHeight());
        assertEquals(weight, result.getWeight());
        assertEquals(email, result.getEmail());
        assertEquals(university, result.getUniversity());
        assertEquals(fieldOfStudy, result.getFieldOfStudy());
        assertEquals(yearOfStudy, result.getYearOfStudy());
        assertEquals(scholarship, result.getScholarship());
    }

    @Test
    void testUpdate_ResultsInStudentBeingUpdated() {
        int newYearOfStudy = 3;
        double newHeight = 190;
        double newScholarship = 1890;
        String newUniversity = "PJATK";
        String newFieldOfStudy = "Coding";
        UpdatePersonCommand updatePersonCommand = new UpdatePersonCommand()
                .setVersion(1)
                .setParams(Map.of("height", String.valueOf(newHeight),
                        "university", newUniversity,
                        "yearOfStudy", String.valueOf(newYearOfStudy),
                        "fieldOfStudy", newFieldOfStudy,
                        "scholarship", String.valueOf(newScholarship)));

        studentStrategy.update(student, updatePersonCommand);

        assertEquals(firstName, student.getFirstName());
        assertEquals(lastName, student.getLastName());
        assertEquals(personalNumber, student.getPersonalNumber());
        assertEquals(newHeight, student.getHeight());
        assertEquals(weight, student.getWeight());
        assertEquals(email, student.getEmail());
        assertEquals(newUniversity, student.getUniversity());
        assertEquals(newYearOfStudy, student.getYearOfStudy());
        assertEquals(newFieldOfStudy, student.getFieldOfStudy());
        assertEquals(newScholarship, student.getScholarship());
        assertEquals(1, student.getVersion());
    }

    @Test
    void testGetPersonExtensionFields_ShouldReturnListOfStudentExtensionFields() {
        Map<String, FieldType> expected = new LinkedHashMap<>();
        expected.put("university", FieldType.STRING);
        expected.put("yearOfStudy", FieldType.NUMBER);
        expected.put("fieldOfStudy", FieldType.STRING);
        expected.put("scholarship", FieldType.NUMBER);

        Map<String, FieldType> fields = studentStrategy.getPersonExtensionFields();

        assertEquals(fields, expected);
    }

    @Test
    void testGetSpecification_ShouldReturnSpecificationForPositions() {
        Specification<PersonView> specification = studentStrategy.getSpecification(null);

        assertNull(specification);
    }
}