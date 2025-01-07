package eng.project.peoplehubdemo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class EmployeeDto extends PersonDto {
    private int numberOfProfessions;
}
