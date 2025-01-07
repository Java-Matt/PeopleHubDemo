package eng.project.peoplehubdemo.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class ExperienceDto {
    private int id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String position;
    private double salary;
    private int personId;
}
