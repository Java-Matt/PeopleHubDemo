package eng.project.peoplehubdemo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SupportedDateOrderValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedDateOrder {
    String message() default "StartDate must be before EndDate!";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
