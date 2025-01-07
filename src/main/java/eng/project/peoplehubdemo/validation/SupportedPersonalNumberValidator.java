package eng.project.peoplehubdemo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class SupportedPersonalNumberValidator implements ConstraintValidator<SupportedPersonalNumber, String> {

    @Override
    public void initialize(SupportedPersonalNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String personalNumber, ConstraintValidatorContext context) {
        if (personalNumber == null || personalNumber.length() != 11) {
            return false;
        }

        try {
            int year = Integer.parseInt(personalNumber.substring(0, 2));
            int month = Integer.parseInt(personalNumber.substring(2, 4));
            int day = Integer.parseInt(personalNumber.substring(4, 6));

            if (month >= 80 && month <= 92) {
                month -= 80;
                year += 1800;
            } else if (month >= 0 && month <= 12) {
                year += 1900;
            } else if (month >= 20 && month <= 32) {
                month -= 20;
                year += 2000;
            } else if (month >= 40 && month <= 52) {
                month -= 40;
                year += 2100;
            } else if (month >= 60 && month <= 72) {
                month -= 60;
                year += 2200;
            } else {
                return false;
            }

            LocalDate birthDate = LocalDate.of(year, month, day);
            if (birthDate.getYear() != year || birthDate.getMonthValue() != month || birthDate.getDayOfMonth() != day) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
