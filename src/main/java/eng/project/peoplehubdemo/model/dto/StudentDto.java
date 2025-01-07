package eng.project.peoplehubdemo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class StudentDto extends PersonDto {
    private String university;
    private int yearOfStudy;
    private String fieldOfStudy;
    private double scholarship;
}
