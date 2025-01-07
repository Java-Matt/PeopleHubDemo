package eng.project.peoplehubdemo.model.view;

import jakarta.persistence.Column;
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
@DiscriminatorValue("EMPLOYEE")
public class EmployeeView extends PersonView {
    @Column(name = "salary")
    private double salary;

    @Column(name = "numberOfProfessions")
    private int numberOfProfessions;
}
