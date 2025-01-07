package eng.project.peoplehubdemo.specification;

import eng.project.peoplehubdemo.model.SearchCriteria;
import eng.project.peoplehubdemo.model.view.PersonView;
import eng.project.peoplehubdemo.strategy.PersonStrategy;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
public class PersonSpecificationBuilder {
    private final List<SearchCriteria> params = new ArrayList<>();
    private Map<String, PersonStrategy> strategyMap;

    public PersonSpecificationBuilder(Map<String, PersonStrategy> strategyMap) {
        this.strategyMap = strategyMap;
    }

    public PersonSpecificationBuilder fromSearch(String search) {
        Pattern pattern = Pattern.compile("(\\w+)(:|<|>)([^,]+),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            this.with(matcher.group(1), matcher.group(2), matcher.group(3));
        }
        return this;
    }

    private PersonSpecificationBuilder with(String key, String operation, Object value) {
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public Specification<PersonView> build() {
        if (params.isEmpty()) {
            return null;
        }

        List<Specification<PersonView>> specs = new ArrayList<>();

        for (SearchCriteria param : params) {
            specs.add(new PersonSpecification(param, strategyMap));
        }

        Specification<PersonView> result = specs.get(0);
        for (int i = 1; i < specs.size(); i++) {
            result = result.and(specs.get(i));
        }

        return result;
    }
}

