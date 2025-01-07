package eng.project.peoplehubdemo.model.view;

import jakarta.persistence.*;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING)
@Table(name = "person_view")
public abstract class PersonView {
    @Id
    private int id;

    @Column(name = "dtype", insertable = false, updatable = false)
    private String type;

    private String firstName;
    private String lastName;
    private String personalNumber;
    private double height;
    private double weight;
    private String email;
    private Integer version;
}
