package eng.project.peoplehubdemo.controller;

import eng.project.peoplehubdemo.model.command.UpdateEmployeeExperienceCommand;
import eng.project.peoplehubdemo.model.dto.ExperienceDto;
import eng.project.peoplehubdemo.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/{id}/experiences")
    @ResponseStatus(HttpStatus.CREATED)
    public ExperienceDto updateEmployeeExperience(@PathVariable int id, @Valid @RequestBody UpdateEmployeeExperienceCommand command) {
        return employeeService.updateEmployeeExperience(id, command);
    }
}
