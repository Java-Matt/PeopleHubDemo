package eng.project.peoplehubdemo.model.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class PersonDto {
    private int id;
    private String type;
    private String firstName;
    private String lastName;
    private double height;
    private double weight;
    private String email;
}
