package eng.project.peoplehubdemo.model.view;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@DiscriminatorValue("STUDENT")
public class StudentView extends PersonView {
    private String university;
    private int yearOfStudy;
    private String fieldOfStudy;
    private double scholarship;
}
