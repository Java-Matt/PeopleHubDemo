package eng.project.peoplehubdemo.strategy;

import eng.project.peoplehubdemo.model.Employee;
import eng.project.peoplehubdemo.model.FieldType;
import eng.project.peoplehubdemo.model.Person;
import eng.project.peoplehubdemo.model.SearchCriteria;
import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.model.dto.EmployeeDto;
import eng.project.peoplehubdemo.model.dto.PersonDto;
import eng.project.peoplehubdemo.model.view.EmployeeView;
import eng.project.peoplehubdemo.model.view.PersonView;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("EMPLOYEE")
public class EmployeeStrategy implements PersonStrategy {
    @Override
    public Employee create(CreatePersonCommand command) {
        Map<String, String> params = command.getParams();
        return Employee.builder()
                .type(command.getType())
                .firstName(params.get("firstName"))
                .lastName(params.get("lastName"))
                .personalNumber(params.get("personalNumber"))
                .height(Double.parseDouble(params.get("height")))
                .weight(Double.parseDouble(params.get("weight")))
                .email(params.get("email"))
                .build();
    }

    @Override
    public PersonDto mapToDto(Person person) {
        return PersonDto.builder()
                .id(person.getId())
                .type(person.getType())
                .firstName(person.getFirstName())
                .lastName(person.getLastName())
                .height(person.getHeight())
                .weight(person.getWeight())
                .email(person.getEmail())
                .build();
    }

    @Override
    public EmployeeDto mapViewToDto(PersonView personView) {
        EmployeeView employee = (EmployeeView) personView;
        return EmployeeDto.builder()
                .id(employee.getId())
                .type(employee.getType())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .height(employee.getHeight())
                .weight(employee.getWeight())
                .email(employee.getEmail())
                .numberOfProfessions(employee.getNumberOfProfessions())
                .build();
    }

    @Override
    public void update(Person person, UpdatePersonCommand command) {
        person.setVersion(command.getVersion());
        Employee employee = (Employee) person;
        Map<String, String> params = command.getParams();
        if (params != null) {
            if (params.get("firstName") != null) {
                employee.setFirstName(params.get("firstName"));
            }
            if (params.get("lastName") != null) {
                employee.setLastName(params.get("lastName"));
            }
            if (params.get("height") != null) {
                employee.setHeight(Double.parseDouble(params.get("height")));
            }
            if (params.get("weight") != null) {
                employee.setWeight(Double.parseDouble(params.get("weight")));
            }
            if (params.get("email") != null) {
                employee.setEmail(params.get("email"));
            }
        }
    }

    @Override
    public Map<String, FieldType> getPersonExtensionFields() {
        Map<String, FieldType> toReturn = new LinkedHashMap<>();
        return toReturn;
    }

    @Override
    public Specification<PersonView> getSpecification(SearchCriteria criteria) {
        return (root, query, builder) -> {
            Predicate combinedPredicate = builder.conjunction();

            Predicate employeePredicate = builder.equal(root.get("type"), "Employee");
            combinedPredicate = builder.and(combinedPredicate, employeePredicate);

            for (String key : customFiltrationEmployee()) {
                if (criteria.getKey().equalsIgnoreCase(key)) {
                    combinedPredicate = builder.and(combinedPredicate, handleDefaultPredicate(root, builder, criteria));
                }
            }
            return combinedPredicate;
        };
    }

    private List<String> customFiltrationEmployee() {
        return List.of("numberOfProfessions", "salary");
    }

    private Predicate handleDefaultPredicate(Root<PersonView> root, CriteriaBuilder builder, SearchCriteria criteria) {
        Predicate predicate = null;

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            predicate = builder.greaterThanOrEqualTo(root.get(criteria.getKey()), Double.parseDouble(criteria.getValue().toString()));
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            predicate = builder.lessThanOrEqualTo(root.get(criteria.getKey()), Double.parseDouble(criteria.getValue().toString()));
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                predicate = builder.like(builder.lower(root.get(criteria.getKey())), "%" + criteria.getValue().toString().toLowerCase() + "%");
            } else {
                predicate = builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return predicate;
    }
}
