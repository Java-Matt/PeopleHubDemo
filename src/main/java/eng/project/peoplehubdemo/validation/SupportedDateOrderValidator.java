package eng.project.peoplehubdemo.validation;

import eng.project.peoplehubdemo.model.command.UpdateEmployeeExperienceCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SupportedDateOrderValidator implements ConstraintValidator<SupportedDateOrder, UpdateEmployeeExperienceCommand> {
    @Override
    public boolean isValid(UpdateEmployeeExperienceCommand command, ConstraintValidatorContext context) {
        if (command.getStartDate() == null || command.getEndDate() == null) {
            return true;
        }

        boolean isValid = !command.getEndDate().isBefore(command.getStartDate());

        if (!isValid) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("endDate must be after startDate!")
                    .addPropertyNode("endDate")
                    .addConstraintViolation();
        }

        return isValid;
    }
}
