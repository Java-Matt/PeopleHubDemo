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
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ExperienceRepository experienceRepository;
    private final ExperienceMapper experienceMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN')or hasRole('ROLE_EMPLOYEE')")
    @Transactional
    public ExperienceDto updateEmployeeExperience(int id, UpdateEmployeeExperienceCommand command) {
        Employee employee = employeeRepository.findWithLockingById(id)
                .orElseThrow(() -> new PersonNotFoundException(MessageFormat
                        .format("Employee with id={0} not found", id)));
        if (experienceRepository.existsOverlappingExperiences(
                employee.getId(), command.getStartDate(), command.getEndDate())) {
            throw new ExperienceOverlappingException("The provided dates overlap with existing Experiences.");
        }
        Experience experience = experienceMapper.mapFromCommand(command, employee);
        experienceRepository.save(experience);
        return experienceMapper.mapToDto(experience);
    }
}
