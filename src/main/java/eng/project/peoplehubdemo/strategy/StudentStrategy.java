package eng.project.peoplehubdemo.strategy;

import eng.project.peoplehubdemo.model.FieldType;
import eng.project.peoplehubdemo.model.Person;
import eng.project.peoplehubdemo.model.SearchCriteria;
import eng.project.peoplehubdemo.model.Student;
import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.model.dto.StudentDto;
import eng.project.peoplehubdemo.model.view.PersonView;
import eng.project.peoplehubdemo.model.view.StudentView;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component("STUDENT")
public class StudentStrategy implements PersonStrategy {
    @Override
    public Student create(CreatePersonCommand command) {
        Map<String, String> params = command.getParams();
        return Student.builder()
                .type(command.getType())
                .firstName(params.get("firstName"))
                .lastName(params.get("lastName"))
                .personalNumber(params.get("personalNumber"))
                .height(Double.parseDouble(params.get("height")))
                .weight(Double.parseDouble(params.get("weight")))
                .email(params.get("email"))
                .university(params.get("university"))
                .yearOfStudy(Integer.parseInt(params.get("yearOfStudy")))
                .fieldOfStudy(params.get("fieldOfStudy"))
                .scholarship(Double.parseDouble(params.get("scholarship")))
                .build();
    }

    @Override
    public StudentDto mapToDto(Person person) {
        Student student = (Student) person;
        return StudentDto.builder()
                .id(student.getId())
                .type(student.getType())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .height(student.getHeight())
                .weight(student.getWeight())
                .email(student.getEmail())
                .university(student.getUniversity())
                .yearOfStudy(student.getYearOfStudy())
                .fieldOfStudy(student.getFieldOfStudy())
                .scholarship(student.getScholarship())
                .build();
    }

    @Override
    public StudentDto mapViewToDto(PersonView personView) {
        StudentView student = (StudentView) personView;
        return StudentDto.builder()
                .id(student.getId())
                .type(student.getType())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .height(student.getHeight())
                .weight(student.getWeight())
                .email(student.getEmail())
                .university(student.getUniversity())
                .yearOfStudy(student.getYearOfStudy())
                .fieldOfStudy(student.getFieldOfStudy())
                .scholarship(student.getScholarship())
                .build();
    }

    @Override
    public void update(Person person, UpdatePersonCommand command) {
        person.setVersion(command.getVersion());
        Student student = (Student) person;
        Map<String, String> params = command.getParams();
        if (params != null) {
            if (params.get("firstName") != null) {
                student.setFirstName(params.get("firstName"));
            }
            if (params.get("lastName") != null) {
                student.setLastName(params.get("lastName"));
            }
            if (params.get("height") != null) {
                student.setHeight(Double.parseDouble(params.get("height")));
            }
            if (params.get("weight") != null) {
                student.setWeight(Double.parseDouble(params.get("weight")));
            }
            if (params.get("email") != null) {
                student.setEmail(params.get("email"));
            }
            if (params.get("university") != null) {
                student.setUniversity(params.get("university"));
            }
            if (params.get("yearOfStudy") != null) {
                student.setYearOfStudy(Integer.parseInt(params.get("yearOfStudy")));
            }
            if (params.get("fieldOfStudy") != null) {
                student.setFieldOfStudy(params.get("fieldOfStudy"));
            }
            if (params.get("scholarship") != null) {
                student.setScholarship(Double.parseDouble(params.get("scholarship")));
            }
        }
    }

    @Override
    public Map<String, FieldType> getPersonExtensionFields() {
        Map<String, FieldType> toReturn = new LinkedHashMap<>();
        toReturn.put("university", FieldType.STRING);
        toReturn.put("yearOfStudy", FieldType.NUMBER);
        toReturn.put("fieldOfStudy", FieldType.STRING);
        toReturn.put("scholarship", FieldType.NUMBER);
        return toReturn;
    }

    @Override
    public Specification<PersonView> getSpecification(SearchCriteria criteria) {
        return null;
    }
}
