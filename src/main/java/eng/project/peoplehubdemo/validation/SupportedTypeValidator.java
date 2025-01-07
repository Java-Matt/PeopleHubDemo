package eng.project.peoplehubdemo.validation;

import eng.project.peoplehubdemo.strategy.PersonStrategy;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SupportedTypeValidator implements ConstraintValidator<SupportedType, String> {

    private final Set<PersonStrategy> strategies;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return supportedTypes().contains(s);
    }

    private Set<String> supportedTypes() {
        return strategies.stream()
                .map(strategy -> strategy.getClass()
                        .getAnnotation(Component.class)
                        .value())
                .collect(Collectors.toSet());
    }
}