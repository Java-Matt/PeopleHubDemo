package eng.project.peoplehubdemo.specification;

import eng.project.peoplehubdemo.model.SearchCriteria;
import eng.project.peoplehubdemo.model.view.PersonView;
import eng.project.peoplehubdemo.strategy.PersonStrategy;
import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class PersonSpecification implements Specification<PersonView> {
    private SearchCriteria criteria;
    private final Map<String, PersonStrategy> strategyMap;

    @Override
    public Predicate toPredicate(Root<PersonView> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        Predicate combinedPredicate = builder.conjunction();
        List<Specification<PersonView>> customSpecifications = collectCustomSpecifications();
        List<String> defaultFields = collectDefaultFields();

        if (criteria.getKey().equalsIgnoreCase("age")) {
            combinedPredicate = builder.and(combinedPredicate, handleAgePredicate(root, builder));
        }
        if (criteria.getKey().equalsIgnoreCase("gender")) {
            combinedPredicate = builder.and(combinedPredicate, handleGenderPredicate(root, builder));
        } else if (defaultFields.stream().anyMatch(field -> field.equalsIgnoreCase(criteria.getKey()))) {
            combinedPredicate = builder.and(combinedPredicate, handleDefaultPredicate(root, builder));
        } else {
            for (Specification<PersonView> spec : customSpecifications) {
                Predicate customPredicate = spec.toPredicate(root, query, builder);
                if (customPredicate != null) {
                    combinedPredicate = builder.and(combinedPredicate, customPredicate);
                }
            }
        }
        return combinedPredicate;
    }

    private List<Specification<PersonView>> collectCustomSpecifications() {
        List<Specification<PersonView>> specifications = new ArrayList<>();

        strategyMap.values().forEach(strategy -> {
            Specification<PersonView> specification = strategy.getSpecification(criteria);
            if (specification != null) {
                specifications.add(specification);
            }
        });
        return specifications;
    }

    private List<String> collectDefaultFields() {
        List<String> fields = new ArrayList<>(List.of("type", "id", "firstName", "lastName", "email", "personalNumber", "height", "weight"));
        strategyMap.values().forEach(strategy -> {
            List<String> fieldName = strategy.getPersonExtensionFields().keySet().stream().toList();
            fields.addAll(fieldName);
        });
        return fields;
    }

    private Predicate handleDefaultPredicate(Root<PersonView> root, CriteriaBuilder builder) {
        Predicate predicate = null;

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            if (isNumeric(criteria.getValue().toString())) {
                predicate = builder.greaterThanOrEqualTo(root.get(criteria.getKey()), Double.parseDouble(criteria.getValue().toString()));
            } else {
                predicate = builder.greaterThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            }
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            if (isNumeric(criteria.getValue().toString())) {
                predicate = builder.lessThanOrEqualTo(root.get(criteria.getKey()), Double.parseDouble(criteria.getValue().toString()));
            } else {
                predicate = builder.lessThanOrEqualTo(root.get(criteria.getKey()), criteria.getValue().toString());
            }
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                predicate = builder.like(builder.lower(root.get(criteria.getKey())), "%" + criteria.getValue().toString().toLowerCase() + "%");
            } else {
                predicate = builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return predicate;
    }

    private Predicate handleAgePredicate(Root<PersonView> root, CriteriaBuilder builder) {
        Expression<Integer> rawYear = builder.function("substring", Integer.class, root.get("personalNumber"), builder.literal(1), builder.literal(2));
        Expression<Integer> month = builder.function("substring", Integer.class, root.get("personalNumber"), builder.literal(3), builder.literal(2));
        Expression<Integer> day = builder.function("substring", Integer.class, root.get("personalNumber"), builder.literal(5), builder.literal(2));

        Expression<Integer> yearExpression = builder.coalesce(
                builder.sum(builder.literal(1900), rawYear),
                builder.sum(builder.literal(2000), rawYear)
        );

        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        int currentDay = currentDate.getDayOfMonth();

        Expression<Integer> ageExpression = builder.diff(builder.literal(currentYear), yearExpression);

        Predicate birthdayNotPassedThisYear = builder.or(
                builder.lessThan(month, currentMonth),
                builder.and(
                        builder.equal(month, currentMonth),
                        builder.lessThanOrEqualTo(day, currentDay)
                )
        );

        Expression<Integer> age = builder.selectCase()
                .when(birthdayNotPassedThisYear, ageExpression)
                .otherwise(builder.diff(ageExpression, 1))
                .as(Integer.class);

        if (criteria.getOperation().equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(age, Integer.valueOf(criteria.getValue().toString()));
        } else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(age, Integer.valueOf(criteria.getValue().toString()));
        } else if (criteria.getOperation().equalsIgnoreCase(":")) {
            return builder.equal(age, Integer.valueOf(criteria.getValue().toString()));
        } else {
            throw new RuntimeException("Operation not supported!");
        }
    }

    private Predicate handleGenderPredicate(Root<PersonView> root, CriteriaBuilder builder) {
        Predicate genderPredicate;
        if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (criteria.getValue().toString().equalsIgnoreCase("female")) {
                genderPredicate = builder.like(builder.lower(root.get("firstName")), "%a");
            } else {
                genderPredicate = builder.notLike(builder.lower(root.get("firstName")), "%a");
            }
        } else {
            throw new RuntimeException("Operation not supported for gender!");
        }
        return genderPredicate;
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}


