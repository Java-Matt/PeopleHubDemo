package eng.project.peoplehubdemo.validation;

import eng.project.peoplehubdemo.model.FieldType;
import eng.project.peoplehubdemo.strategy.PersonStrategy;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


@RequiredArgsConstructor
public class SupportedParamValidator implements ConstraintValidator<SupportedParam, Map<String, String>> {
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$");

    private final Map<String, PersonStrategy> strategyMap;
    private Map<String, FieldType> fieldTypes;

    @Override
    public void initialize(SupportedParam constraintAnnotation) {
        fieldTypes = new HashMap<>();
        initializePersonFields();
        initializePersonExtensionsMap();
    }

    @Override
    public boolean isValid(Map<String, String> params, ConstraintValidatorContext context) {
        SupportedPersonalNumberValidator personalNumberValidator = new SupportedPersonalNumberValidator();
        if (params == null) {
            return true;
        }
        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            FieldType fieldType = fieldTypes.get(key);

            if (fieldType != null) {
                isValid = validateField(key, value, fieldType, context, personalNumberValidator) && isValid;
            }
        }
        return isValid;
    }

    private boolean validateField(String key, String value, FieldType fieldType, ConstraintValidatorContext context, SupportedPersonalNumberValidator personalNumberValidator) {
        switch (fieldType) {
            case NUMBER:
                if (!isPositive(value)) {
                    addViolation(context, key, "must be positive");
                    return false;
                }
                break;
            case DATE:
                if (!isValidDate(value)) {
                    addViolation(context, key, "must be a valid date in format yyyy-MM-dd");
                    return false;
                }
                break;
            case STRING:
                if (key.equalsIgnoreCase("email")) {
                    if (!isValidEmail(value)) {
                        addViolation(context, key, "must be a valid email address");
                        return false;
                    }
                } else if (key.equalsIgnoreCase("personalNumber")) {
                    if (!personalNumberValidator.isValid(value, context)) {
                        addViolation(context, key, "must be a valid personal number");
                        return false;
                    }
                } else if (value == null || value.isEmpty()) {
                    addViolation(context, key, "must not be null or empty");
                    return false;
                }
                break;
            default:
                addViolation(context, key, "has an unsupported type");
                return false;
        }
        return true;
    }

    private void initializePersonFields() {
        fieldTypes.put("firstName", FieldType.STRING);
        fieldTypes.put("lastName", FieldType.STRING);
        fieldTypes.put("height", FieldType.NUMBER);
        fieldTypes.put("weight", FieldType.NUMBER);
        fieldTypes.put("personalNumber", FieldType.STRING);
        fieldTypes.put("email", FieldType.STRING);
    }

    private void initializePersonExtensionsMap() {
        strategyMap.values().forEach(strategy -> {
            Map<String, FieldType> extensionFields = strategy.getPersonExtensionFields();
            fieldTypes.putAll(extensionFields);
        });
    }

    private boolean isPositive(String value) {
        try {
            if (value.contains(".")) {
                double number = Double.parseDouble(value);
                return number > 0;
            } else {
                int number = Integer.parseInt(value);
                return number > 0;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidEmail(String value) {
        return EMAIL_PATTERN.matcher(value).matches();
    }

    private boolean isValidDate(String value) {
        if (!DATE_PATTERN.matcher(value).matches()) {
            return false;
        }
        try {
            LocalDate.parse(value, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void addViolation(ConstraintValidatorContext context, String key, String message) {
        context.buildConstraintViolationWithTemplate("Value for " + key + " " + message)
                .addPropertyNode(key)
                .addConstraintViolation();
    }
}