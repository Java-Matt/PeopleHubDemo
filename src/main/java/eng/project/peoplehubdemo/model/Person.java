package eng.project.peoplehubdemo.model;

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
@Table(name = "person", uniqueConstraints = @UniqueConstraint(columnNames = "personalNumber"))
public abstract class Person implements Cloneable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "dtype", insertable = false, updatable = false)
    private String type;
    private String firstName;
    private String lastName;

    @Column(nullable = false, unique = true)
    private String personalNumber;
    private double height;
    private double weight;
    private String email;

    @Version
    private Integer version;

    @Override
    public Person clone() {
        try {
            Person clone = (Person) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
