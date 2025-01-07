package eng.project.peoplehubdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import eng.project.peoplehubdemo.model.Employee;
import eng.project.peoplehubdemo.model.command.UpdateEmployeeExperienceCommand;
import eng.project.peoplehubdemo.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;

    private int id;

    @BeforeEach
    public void init() {
        id = 1;
        String type = "EMPLOYEE";
        String firstName = "Adam";
        String lastName = "Nowak";
        String personalNumber = "97051412345";
        int height = 180;
        int weight = 75;
        String email = "adam@nowak.com";
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
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void testUpdateEmployeeExperience_ReturnsUpdatedEmployee() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(10);
        String newPosition = "Programmer";
        double salary = 30000;
        Employee returned = employeeRepository.save(employee);

        UpdateEmployeeExperienceCommand command = new UpdateEmployeeExperienceCommand()
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setPosition(newPosition)
                .setSalary(salary);

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(post("/api/employees/{id}/experiences", returned.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(returned.getId()))
                .andExpect(jsonPath("$.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.position").value(newPosition))
                .andExpect(jsonPath("$.salary").value(salary))
                .andExpect(jsonPath("$.personId").value(id));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "EMPLOYEE"})
    void testUpdateEmployeeExperience_ShouldThrowValidationError_WhenIncorrectDateOrder() throws Exception {
        LocalDate startDate = LocalDate.now().plusDays(15);
        LocalDate endDate = LocalDate.now().plusDays(10);
        String newPosition = "Programmer";
        double salary = 30000;
        employeeRepository.save(employee);

        UpdateEmployeeExperienceCommand command = new UpdateEmployeeExperienceCommand()
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setPosition(newPosition)
                .setSalary(salary);

        String content = objectMapper.writeValueAsString(command);

        mockMvc.perform(post("/api/employees/{id}/experiences", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("VALIDATION_ERROR"));
    }
}