package eng.project.peoplehubdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eng.project.peoplehubdemo.model.Employee;
import eng.project.peoplehubdemo.model.Pensioner;
import eng.project.peoplehubdemo.model.Person;
import eng.project.peoplehubdemo.model.Student;
import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.repository.PersonRepository;
import eng.project.peoplehubdemo.service.PersonService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Sql(scripts = "classpath:person-teardown.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class PersonControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonService personService;
    @Autowired
    private PersonRepository<Person> personRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    private Statistics hibernateStatistics;
    private Person employeeData;
    private Person studentData;
    private Person pensionerData;
    private Employee employee;

    private int height;
    private int weight;
    private String firstName;
    private String lastName;
    private String email;
    private String personalNumber;
    private String type;

    @BeforeEach
    public void init() {
        int id = 1;
        type = "EMPLOYEE";
        firstName = "Adam";
        lastName = "Nowak";
        personalNumber = "97051412345";
        height = 180;
        weight = 75;
        email = "adam@nowak.com";

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
        employeeData = Employee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
                .version(0)
                .build();
        studentData = Student.builder()
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
                .version(0)
                .fieldOfStudy("Something")
                .scholarship(123)
                .university("The Best")
                .build();
        pensionerData = Pensioner.builder()
                .firstName(firstName)
                .lastName(lastName)
                .personalNumber(personalNumber)
                .height(height)
                .weight(weight)
                .email(email)
                .version(0)
                .pension(123)
                .yearsOfWork(2)
                .build();

        hibernateStatistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        hibernateStatistics.setStatisticsEnabled(true);
        hibernateStatistics.clear();
        entityManager.clear();
    }

    @Test
    void testSearchPerson_ReturnsPersonPage() throws Exception {
        personRepository.save(employee);
        personRepository.save(Pensioner.builder()
                .type("PENSIONER")
                .firstName("Stefan")
                .lastName(lastName)
                .personalNumber("98052300000")
                .height(height)
                .weight(weight)
                .email(email)
                .yearsOfWork(3)
                .pension(2345)
                .version(0).build());

        mockMvc.perform(get("/api/persons")
                        .param("search", "firstName:Adam")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value(firstName))
                .andExpect(jsonPath("$.content[1]").doesNotExist())
                .andExpect(jsonPath("$.content[0].lastName").value(lastName))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchPersons_NoNPlusOneProblemForEmployee() throws Exception {
        personRepository.saveAndFlush(employeeData);

        personRepository.saveAndFlush(Employee.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .personalNumber("96051412345")
                .height(170)
                .weight(60)
                .email("Jan@Kowalski.com")
                .version(0)
                .build());

        hibernateStatistics.clear();

        mockMvc.perform(get("/api/persons")
                        .param("search", "type:employee")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value(firstName))
                .andExpect(jsonPath("$.content[1].firstName").value("Jan"));

        long queryCount = hibernateStatistics.getPrepareStatementCount();
        assertEquals(1, queryCount);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchPersons_NoNPlusOneProblemForStudent() throws Exception {
        personRepository.saveAndFlush(studentData);

        personRepository.saveAndFlush(Student.builder()
                .firstName("Anna")
                .lastName("Kowalska")
                .personalNumber("96051412345")
                .height(170)
                .weight(60)
                .email("Anna@kowalska.com")
                .version(0)
                .build());

        hibernateStatistics.clear();

        mockMvc.perform(get("/api/persons")
                        .param("search", "type:student")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value(firstName))
                .andExpect(jsonPath("$.content[1].firstName").value("Anna"));

        long queryCount = hibernateStatistics.getPrepareStatementCount();
        assertEquals(1, queryCount);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSearchPersons_NoNPlusOneProblemForPensioner() throws Exception {
        personRepository.saveAndFlush(pensionerData);

        personRepository.saveAndFlush(Pensioner.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .personalNumber("96051412345")
                .height(170)
                .weight(60)
                .email("Jan@kowalski.com")
                .version(0)
                .build());

        hibernateStatistics.clear();

        mockMvc.perform(get("/api/persons")
                        .param("search", "type:pensioner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value(firstName))
                .andExpect(jsonPath("$.content[1].firstName").value("Jan"));

        long queryCount = hibernateStatistics.getPrepareStatementCount();
        assertEquals(1, queryCount);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreatePerson_ResultsInClientBeingSaved() throws Exception {
        CreatePersonCommand command = new CreatePersonCommand()
                .setType(type)
                .setParams(Map.of("firstName", firstName,
                        "lastName", lastName,
                        "height", String.valueOf(height),
                        "weight", String.valueOf(weight),
                        "personalNumber", personalNumber,
                        "email", email));

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.type").value(type))
                .andExpect(jsonPath("$.height").value(height))
                .andExpect(jsonPath("$.weight").value(weight))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreatePerson_ShouldThrowDataIntegrityViolationException_WhenPersonWithPersonalNumberAlreadyExists() throws Exception {
        CreatePersonCommand command = new CreatePersonCommand()
                .setType(type)
                .setParams(Map.of("firstName", firstName,
                        "lastName", lastName,
                        "height", String.valueOf(height),
                        "weight", String.valueOf(weight),
                        "personalNumber", personalNumber,
                        "email", email));

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/persons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePerson_ReturnsUpdatedPerson() throws Exception {
        String newName = "Albert";
        Person toSave = personRepository.save(employee);

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setVersion(0);
        command.setParams(Map.of("firstName", newName, "lastName", lastName));

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/api/persons/{id}", toSave.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(newName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.type").value(employee.getType()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePerson_IncrementsVersionOnSuccess() throws Exception {
        Person person = personRepository.save(employeeData);

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setVersion(0);
        command.setParams(Map.of("firstName", "John Updated"));

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/api/persons/{id}", person.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John Updated"));

        Person updatedPerson = personRepository.findById(person.getId()).orElseThrow();
        assertEquals(1, updatedPerson.getVersion());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePerson_VersionRemainsUnchangedOnFailure() throws Exception {
        Person person = personRepository.save(employeeData);

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setVersion(999);
        command.setParams(Map.of("firstName", "Jane Updated"));

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/api/persons/{id}", person.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isInternalServerError());

        Person unchangedPerson = personRepository.findById(person.getId()).orElseThrow();
        assertEquals(0, unchangedPerson.getVersion());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePerson_ThrowOptimisticLock_WhenPersonLocked() throws Exception {
        Person person = personRepository.save(employeeData);

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setVersion(0);
        command.setParams(Map.of("firstName", "Tom Updated"));

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/api/persons/{id}", person.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/persons/{id}", person.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePerson_SendsTwoDatabaseRequestsForEmployee() throws Exception {
        Person employee = personRepository.saveAndFlush(employeeData);

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setVersion(0);
        command.setParams(Map.of("firstName", "Alice Updated"));

        hibernateStatistics.clear();

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/api/persons/{id}", employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        long queryCount = hibernateStatistics.getPrepareStatementCount();
        assertEquals(2, queryCount);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePerson_SendsTwoDatabaseRequestsForStudent() throws Exception {
        Person student = personRepository.saveAndFlush(studentData);

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setVersion(0);
        command.setParams(Map.of("firstName", "Alice Updated", "fieldOfStudy", "UpdatedStudy"));

        hibernateStatistics.clear();

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/api/persons/{id}", student.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        long queryCount = hibernateStatistics.getPrepareStatementCount();
        assertEquals(2, queryCount);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePerson_SendsTwoDatabaseRequestsForPensioner() throws Exception {
        Person pensioner = personRepository.saveAndFlush(pensionerData);

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setVersion(0);
        command.setParams(Map.of("firstName", "Alice Updated", "pension", "8765"));

        hibernateStatistics.clear();

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/api/persons/{id}", pensioner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        long queryCount = hibernateStatistics.getPrepareStatementCount();
        assertEquals(2, queryCount);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdatePerson_SendsOneDatabaseRequestsIfNoChangesDetected() throws Exception {
        Person pensioner = personRepository.saveAndFlush(pensionerData);

        UpdatePersonCommand command = new UpdatePersonCommand();
        command.setVersion(0);

        hibernateStatistics.clear();

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(patch("/api/persons/{id}", pensioner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        long queryCount = hibernateStatistics.getPrepareStatementCount();
        assertEquals(1, queryCount);
    }
}
