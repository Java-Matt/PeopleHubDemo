package eng.project.peoplehubdemo.strategy;

import eng.project.peoplehubdemo.model.FieldType;
import eng.project.peoplehubdemo.model.Pensioner;
import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.model.dto.PensionerDto;
import eng.project.peoplehubdemo.model.view.PensionerView;
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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PensionerStrategyTest {
    @InjectMocks
    private PensionerStrategy pensionerStrategy;

    private CreatePersonCommand command;
    private Pensioner pensioner;
    private PensionerView pensionerView;

    private int id;
    private int height;
    private int weight;
    private String firstName;
    private String lastName;
    private String type;
    private String personalNumber;
    private String email;
    private int pension;
    private int yearsOfWork;

    @BeforeEach
    void setUp() {
        id = 1;
        firstName = "Zygmunt";
        lastName = "Abel";
        personalNumber = "49051412345";
        height = 160;
        weight = 80;
        email = "Zygmunt@abel.com";
        type = "PENSIONER";
        pension = 777;
        yearsOfWork = 66;
        Map<String, String> params = new HashMap<>();
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("personalNumber", personalNumber);
        params.put("height", String.valueOf(height));
        params.put("weight", String.valueOf(weight));
        params.put("email", email);
        params.put("pension", String.valueOf(pension));
        params.put("yearsOfWork", String.valueOf(yearsOfWork));

        command = new CreatePersonCommand()
                .setType(type)
                .setParams(params);

        pensioner = Pensioner.builder()
                .id(id)
                .type(type)
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
                .pension(pension)
                .yearsOfWork(yearsOfWork)
                .version(0)
                .build();

        pensionerView = PensionerView.builder()
                .id(id)
                .type(type)
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
                .pension(pension)
                .yearsOfWork(yearsOfWork)
                .build();
    }

    @Test
    void testCreate_ResultsInPensionerBeingCreated() {
        Pensioner result = pensionerStrategy.create(command);

        assertNotNull(result);
        assertEquals(type, result.getType());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(personalNumber, result.getPersonalNumber());
        assertEquals(height, result.getHeight());
        assertEquals(weight, result.getWeight());
        assertEquals(email, result.getEmail());
        assertEquals(pension, result.getPension());
        assertEquals(yearsOfWork, result.getYearsOfWork());
    }

    @Test
    void testMapToDto_ResultsInPensionerBeingMappedToDto() {
        PensionerDto result = pensionerStrategy.mapToDto(pensioner);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(type, result.getType());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(height, result.getHeight());
        assertEquals(weight, result.getWeight());
        assertEquals(email, result.getEmail());
        assertEquals(pension, result.getPension());
        assertEquals(yearsOfWork, result.getYearsOfWork());
    }

    @Test
    void testMapViewToDto_ResultsInPensionerViewBeingMappedToDto() {
        PensionerDto result = pensionerStrategy.mapViewToDto(pensionerView);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(type, result.getType());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        assertEquals(height, result.getHeight());
        assertEquals(weight, result.getWeight());
        assertEquals(email, result.getEmail());
        assertEquals(pension, result.getPension());
        assertEquals(yearsOfWork, result.getYearsOfWork());
    }

    @Test
    void testUpdate_ResultsInPensionerBeingUpdated() {
        double newHeight = 190;
        double newPension = 50000;
        UpdatePersonCommand updatePersonCommand = new UpdatePersonCommand()
                .setVersion(1)
                .setParams(Map.of("height", String.valueOf(newHeight),
                        "pension", String.valueOf(newPension)));

        pensionerStrategy.update(pensioner, updatePersonCommand);

        assertEquals(firstName, pensioner.getFirstName());
        assertEquals(lastName, pensioner.getLastName());
        assertEquals(personalNumber, pensioner.getPersonalNumber());
        assertEquals(newHeight, pensioner.getHeight());
        assertEquals(weight, pensioner.getWeight());
        assertEquals(email, pensioner.getEmail());
        assertEquals(yearsOfWork, pensioner.getYearsOfWork());
        assertEquals(newPension, pensioner.getPension());
        assertEquals(1, pensioner.getVersion());
    }

    @Test
    void testGetPersonExtensionFields_ShouldReturnListOfPensionerExtensionFields() {
        Map<String, FieldType> expected = new LinkedHashMap<>();
        expected.put("pension", FieldType.NUMBER);
        expected.put("yearsOfWork", FieldType.NUMBER);

        Map<String, FieldType> fields = pensionerStrategy.getPersonExtensionFields();

        assertEquals(expected, fields);
    }

    @Test
    void testGetSpecification_ShouldReturnSpecificationForPositions() {
        Specification<PersonView> specification = pensionerStrategy.getSpecification(null);

        assertNull(specification);
    }
}