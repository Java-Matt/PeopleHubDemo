package eng.project.peoplehubdemo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SupportedPersonalNumberValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedPersonalNumber {

    String message() default "Invalid PESEL number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
