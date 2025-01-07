package eng.project.peoplehubdemo.strategy;

import eng.project.peoplehubdemo.model.Employee;
import eng.project.peoplehubdemo.model.FieldType;
import eng.project.peoplehubdemo.model.SearchCriteria;
import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.model.dto.EmployeeDto;
import eng.project.peoplehubdemo.model.dto.PersonDto;
import eng.project.peoplehubdemo.model.view.EmployeeView;
import eng.project.peoplehubdemo.model.view.PersonView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class EmployeeStrategyTest {
    @InjectMocks
    private EmployeeStrategy employeeStrategy;

    private CreatePersonCommand command;
    private Employee employee;
    private EmployeeView employeeView;

    private int id;
    private int height;
    private int weight;
    private String firstName;
    private String lastName;
    private String type;
    private String personalNumber;
    private String email;

    @BeforeEach
    void setUp() {
        id = 1;
        firstName = "Stefan";
        lastName = "Kowalski";
        personalNumber = "97051412345";
        height = 190;
        weight = 90;
        email = "stefan@kowalski.com";
        type = "EMPLOYEE";
        Map<String, String> params = new HashMap<>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("personalNumber", personalNumber);
        params.put("height", String.valueOf(height));
        params.put("weight", String.valueOf(weight));
        params.put("email", email);
        command = new CreatePersonCommand()
                .setType(type)
                .setParams(params);

        employee = Employee.builder()
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

        employeeView = EmployeeView.builder()
                .id(id)
                .type(type)
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
                .numberOfProfessions(id)
                .build();
    }

    @Test
    void testCreate_ResultsInEmployeeBeingCreated() {
        Employee result = employeeStrategy.create(command);

        assertNotNull(result);
        assertEquals(type, result.getType());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(personalNumber, result.getPersonalNumber());
        assertEquals(height, result.getHeight());
        assertEquals(weight, result.getWeight());
        assertEquals(email, result.getEmail());
    }

    @Test
    void testMapToDto_ResultsInEmployeeBeingMappedToDto() {
        PersonDto result = employeeStrategy.mapToDto(employee);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(type, result.getType());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(height, result.getHeight());
        assertEquals(weight, result.getWeight());
        assertEquals(email, result.getEmail());
    }

    @Test
    void testMapViewToDto_ResultsInEmployeeViewBeingMappedToDto() {
        EmployeeDto result = employeeStrategy.mapViewToDto(employeeView);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(type, result.getType());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(height, result.getHeight());
        assertEquals(weight, result.getWeight());
        assertEquals(email, result.getEmail());
        assertEquals(id, result.getNumberOfProfessions());
    }

    @Test
    void testUpdate_ResultsInEmployeeBeingUpdated() {
        double newWeight = 220;
        UpdatePersonCommand updatePersonCommand = new UpdatePersonCommand()
                .setVersion(1)
                .setParams(Map.of("weight", String.valueOf(newWeight)));

        employeeStrategy.update(employee, updatePersonCommand);

        assertEquals(firstName, employee.getFirstName());
        assertEquals(lastName, employee.getLastName());
        assertEquals(personalNumber, employee.getPersonalNumber());
        assertEquals(height, employee.getHeight());
        assertEquals(newWeight, employee.getWeight());
        assertEquals(email, employee.getEmail());
        assertEquals(1, employee.getVersion());
    }

    @Test
    void testGetPersonExtensionFields_ShouldReturnListOfEmployeeExtensionFields() {
        Map<String, FieldType> expected = new LinkedHashMap<>();

        Map<String, FieldType> fields = employeeStrategy.getPersonExtensionFields();

        assertEquals(expected, fields);
    }

    @Test
    void testGetSpecification_ShouldReturnSpecificationForSalary() {
        SearchCriteria criteria = new SearchCriteria("salary", ">", "5000");

        Specification<PersonView> specification = employeeStrategy.getSpecification(criteria);

        assertNotNull(specification);
    }

    @Test
    void testGetSpecification_ShouldReturnSpecificationForPositions() {
        SearchCriteria criteria = new SearchCriteria("numberOfProfessions", ">", "2");

        Specification<PersonView> specification = employeeStrategy.getSpecification(criteria);

        assertNotNull(specification);
    }
}