package eng.project.peoplehubdemo.mapper;

import eng.project.peoplehubdemo.model.Employee;
import eng.project.peoplehubdemo.model.Experience;
import eng.project.peoplehubdemo.model.command.UpdateEmployeeExperienceCommand;
import eng.project.peoplehubdemo.model.dto.ExperienceDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ExperienceMapperTest {
    @InjectMocks
    private ExperienceMapper experienceMapper;

    private Experience experience;
    private Employee employee;
    private UpdateEmployeeExperienceCommand command;

    private int id;
    private String position;
    private double salary;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    public void init() {
        id = 1;
        startDate = LocalDate.of(2024, 7, 12);
        endDate = startDate.plusDays(10);
        position = "Programmer";
        salary = 5000;

        employee = Employee.builder()
                .id(id)
                .firstName("Eva")
                .lastName("Fast")
                .type("EMPLOYEE")
                .email("ewa@fast.com")
                .personalNumber("98052100000")
                .build();

        command = new UpdateEmployeeExperienceCommand()
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setSalary(salary)
                .setPosition(position);

        experience = new Experience()
                .setId(id)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setSalary(salary)
                .setPosition(position)
                .setPerson(employee);
    }

    @Test
    void testMapFromCommand_ResultsInExperienceBeingMappedFromCommand() {
        Experience result = experienceMapper.mapFromCommand(command, employee);

        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertEquals(salary, result.getSalary());
        assertEquals(position, result.getPosition());
        assertEquals(employee, result.getPerson());
    }

    @Test
    void testMapToDto_ResultsInExperienceBeingMappedToDto() {
        ExperienceDto experienceDto = experienceMapper.mapToDto(experience);

        assertEquals(id, experienceDto.getId());
        assertEquals(startDate, experienceDto.getStartDate());
        assertEquals(endDate, experienceDto.getEndDate());
        assertEquals(salary, experienceDto.getSalary());
        assertEquals(position, experienceDto.getPosition());
        assertEquals(id, experienceDto.getPersonId());
    }
}