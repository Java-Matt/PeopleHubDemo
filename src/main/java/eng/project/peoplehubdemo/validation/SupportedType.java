package eng.project.peoplehubdemo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = SupportedTypeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SupportedType {

    String message() default "TYPE_NOT_SUPPORTED";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
