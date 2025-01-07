package eng.project.peoplehubdemo.strategy;

import eng.project.peoplehubdemo.model.FieldType;
import eng.project.peoplehubdemo.model.Pensioner;
import eng.project.peoplehubdemo.model.Person;
import eng.project.peoplehubdemo.model.SearchCriteria;
import eng.project.peoplehubdemo.model.command.CreatePersonCommand;
import eng.project.peoplehubdemo.model.command.UpdatePersonCommand;
import eng.project.peoplehubdemo.model.dto.PensionerDto;
import eng.project.peoplehubdemo.model.view.PensionerView;
import eng.project.peoplehubdemo.model.view.PersonView;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component("PENSIONER")
public class PensionerStrategy implements PersonStrategy {
    @Override
    public Pensioner create(CreatePersonCommand command) {
        Map<String, String> params = command.getParams();
        return Pensioner.builder()
                .type(command.getType())
                .firstName(params.get("firstName"))
                .lastName(params.get("lastName"))
                .personalNumber(params.get("personalNumber"))
                .height(Double.parseDouble(params.get("height")))
                .weight(Double.parseDouble(params.get("weight")))
                .email(params.get("email"))
                .pension(Double.parseDouble(params.get("pension")))
                .yearsOfWork(Integer.parseInt(params.get("yearsOfWork")))
                .build();
    }

    @Override
    public PensionerDto mapToDto(Person person) {
        Pensioner pensioner = (Pensioner) person;
        return PensionerDto.builder()
                .id(pensioner.getId())
                .type(pensioner.getType())
                .firstName(pensioner.getFirstName())
                .lastName(pensioner.getLastName())
                .height(pensioner.getHeight())
                .weight(pensioner.getWeight())
                .email(pensioner.getEmail())
                .pension(pensioner.getPension())
                .yearsOfWork(pensioner.getYearsOfWork())
                .build();
    }

    @Override
    public PensionerDto mapViewToDto(PersonView personView) {
        PensionerView pensioner = (PensionerView) personView;
        return PensionerDto.builder()
                .id(pensioner.getId())
                .type(pensioner.getType())
                .firstName(pensioner.getFirstName())
                .lastName(pensioner.getLastName())
                .height(pensioner.getHeight())
                .weight(pensioner.getWeight())
                .email(pensioner.getEmail())
                .pension(pensioner.getPension())
                .yearsOfWork(pensioner.getYearsOfWork())
                .build();
    }

    @Override
    public void update(Person person, UpdatePersonCommand command) {
        person.setVersion(command.getVersion());
        Pensioner pensioner = (Pensioner) person;
        Map<String, String> params = command.getParams();
        if (params != null) {
            if (params.get("firstName") != null) {
                pensioner.setFirstName(params.get("firstName"));
            }
            if (params.get("lastName") != null) {
                pensioner.setLastName(params.get("lastName"));
            }
            if (params.get("height") != null) {
                pensioner.setHeight(Double.parseDouble(params.get("height")));
            }
            if (params.get("weight") != null) {
                pensioner.setWeight(Double.parseDouble(params.get("weight")));
            }
            if (params.get("email") != null) {
                pensioner.setEmail(params.get("email"));
            }
            if (params.get("pension") != null) {
                pensioner.setPension(Double.parseDouble(params.get("pension")));
            }
            if (params.get("yearsOfWork") != null) {
                pensioner.setYearsOfWork(Integer.parseInt(params.get("yearsOfWork")));
            }
        }
    }

    @Override
    public Map<String, FieldType> getPersonExtensionFields() {
        Map<String, FieldType> toReturn = new LinkedHashMap<>();
        toReturn.put("pension", FieldType.NUMBER);
        toReturn.put("yearsOfWork", FieldType.NUMBER);
        return toReturn;
    }

    @Override
    public Specification<PersonView> getSpecification(SearchCriteria criteria) {
        return null;
    }
}
