package eng.project.peoplehubdemo.service;

import eng.project.peoplehubdemo.exception.ExperienceOverlappingException;
import eng.project.peoplehubdemo.exception.PersonNotFoundException;
import eng.project.peoplehubdemo.mapper.ExperienceMapper;
import eng.project.peoplehubdemo.model.Employee;
import eng.project.peoplehubdemo.model.Experience;
import eng.project.peoplehubdemo.model.command.UpdateEmployeeExperienceCommand;
import eng.project.peoplehubdemo.model.dto.ExperienceDto;
import eng.project.peoplehubdemo.repository.EmployeeRepository;
import eng.project.peoplehubdemo.repository.ExperienceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @InjectMocks
    private EmployeeService service;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ExperienceRepository experienceRepository;
    @Mock
    private ExperienceMapper experienceMapper;

    private Employee employee;
    private UpdateEmployeeExperienceCommand command;
    private Experience experience;
    private ExperienceDto experienceDto;

    private int id;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    public void init() {
        String type = "EMPLOYEE";
        id = 1;
        String firstName = "Adam";
        String lastName = "Nowak";
        String personalNumber = "97051412345";
        int height = 180;
        int weight = 75;
        String email = "adam@nowak.com";
        String position = "Programmer";
        double salary = 3000.2;
        startDate = LocalDate.now().minusDays(10);
        endDate = LocalDate.now().plusDays(10);
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
                .experiences(new HashSet<>())
                .build();
        command = new UpdateEmployeeExperienceCommand()
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setPosition(position)
                .setSalary(salary);
        experience = new Experience()
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setPosition(position)
                .setSalary(salary)
                .setPerson(employee);
        experienceDto = new ExperienceDto()
                .setPersonId(id)
                .setPersonId(id)
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setPosition(position)
                .setSalary(salary);
    }

    @Test
    void testUpdateEmployeePosition_ResultsInEmployeePositionBeingChanged() {
        when(employeeRepository.findWithLockingById(id)).thenReturn(Optional.of(employee));
        when(experienceRepository.existsOverlappingExperiences(id, startDate, endDate)).thenReturn(false);
        when(experienceMapper.mapFromCommand(command, employee)).thenReturn(experience);
        when(experienceMapper.mapToDto(experience)).thenReturn(experienceDto);

        service.updateEmployeeExperience(id, command);

        verify(employeeRepository).findWithLockingById(id);
        verify(experienceRepository).existsOverlappingExperiences(id, startDate, endDate);
        verify(experienceRepository).save(experience);
        verify(experienceMapper).mapFromCommand(command, employee);
        verify(experienceMapper).mapToDto(experience);
    }

    @Test
    void testUpdateEmployeePosition_ShouldThrowPersonNotFoundException_WhenPersonWithGivenIdNotInDb() {
        when(employeeRepository.findWithLockingById(id)).thenReturn(Optional.empty());

        Exception exception = assertThrows(PersonNotFoundException.class, () -> service.updateEmployeeExperience(id, command));

        assertEquals(MessageFormat.format("Employee with id={0} not found", id), exception.getMessage());
        verify(employeeRepository).findWithLockingById(1);
    }

    @Test
    void testUpdateEmployeePosition_ShouldThrowPositionOverlappingException_WhenDatesOverlap() {
        when(employeeRepository.findWithLockingById(id)).thenReturn(Optional.of(employee));
        when(experienceRepository.existsOverlappingExperiences(id, startDate, endDate)).thenReturn(true);

        ExperienceOverlappingException exception = assertThrows(ExperienceOverlappingException.class, () -> service.updateEmployeeExperience(id, command));

        assertEquals("The provided dates overlap with existing Experiences.", exception.getMessage());
        verify(employeeRepository).findWithLockingById(id);
        verify(experienceRepository).existsOverlappingExperiences(id, startDate, endDate);
    }

    @Test
    void testUpdateEmployeePosition_ShouldCallSaveOnExperienceRepository() {
        when(employeeRepository.findWithLockingById(id)).thenReturn(Optional.of(employee));
        when(experienceRepository.existsOverlappingExperiences(id, startDate, endDate)).thenReturn(false);
        when(experienceMapper.mapFromCommand(command, employee)).thenReturn(experience);

        service.updateEmployeeExperience(id, command);

        verify(experienceRepository).save(experience);
    }

    @Test
    void testUpdateEmployeePosition_ShouldSucceed_WhenNoOverlappingAndCorrectDates() {
        when(employeeRepository.findWithLockingById(id)).thenReturn(Optional.of(employee));
        when(experienceRepository.existsOverlappingExperiences(id, startDate, endDate)).thenReturn(false);
        when(experienceMapper.mapFromCommand(command, employee)).thenReturn(experience);
        when(experienceMapper.mapToDto(experience)).thenReturn(experienceDto);

        ExperienceDto result = service.updateEmployeeExperience(id, command);

        assertEquals(experienceDto, result);
        verify(experienceRepository).save(experience);
    }

    @Test
    void testUpdateEmployeePosition_ShouldCallExperienceMapper() {
        when(employeeRepository.findWithLockingById(id)).thenReturn(Optional.of(employee));
        when(experienceRepository.existsOverlappingExperiences(id, startDate, endDate)).thenReturn(false);
        when(experienceMapper.mapFromCommand(command, employee)).thenReturn(experience);
        when(experienceMapper.mapToDto(experience)).thenReturn(experienceDto);

        service.updateEmployeeExperience(id, command);

        verify(experienceMapper).mapFromCommand(command, employee);
        verify(experienceMapper).mapToDto(experience);
    }

    @Test
    void testUpdateEmployeePosition_ShouldThrowException_WhenSaveFails() {
        when(employeeRepository.findWithLockingById(id)).thenReturn(Optional.of(employee));
        when(experienceRepository.existsOverlappingExperiences(id, startDate, endDate)).thenReturn(false);
        when(experienceMapper.mapFromCommand(command, employee)).thenReturn(experience);
        doThrow(new RuntimeException("Database error")).when(experienceRepository).save(experience);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.updateEmployeeExperience(id, command));

        assertEquals("Database error", exception.getMessage());
        verify(experienceRepository).save(experience);
    }
}