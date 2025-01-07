package eng.project.peoplehubdemo.mapper;

import eng.project.peoplehubdemo.model.Employee;
import eng.project.peoplehubdemo.model.Experience;
import eng.project.peoplehubdemo.model.command.UpdateEmployeeExperienceCommand;
import eng.project.peoplehubdemo.model.dto.ExperienceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExperienceMapper {

    public Experience mapFromCommand(UpdateEmployeeExperienceCommand command, Employee employee) {
        return new Experience()
                .setStartDate(command.getStartDate())
                .setEndDate(command.getEndDate())
                .setPosition(command.getPosition())
                .setSalary(command.getSalary())
                .setPerson(employee);
    }

    public ExperienceDto mapToDto(Experience experience) {
        return new ExperienceDto()
                .setId(experience.getId())
                .setStartDate(experience.getStartDate())
                .setEndDate(experience.getEndDate())
                .setSalary(experience.getSalary())
                .setPosition(experience.getPosition())
                .setPersonId(experience.getPerson().getId());
    }
}
