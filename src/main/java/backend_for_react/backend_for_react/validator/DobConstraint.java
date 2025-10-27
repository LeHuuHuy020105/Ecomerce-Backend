package backend_for_react.backend_for_react.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/***
 * validate cho birthday
 * chi validate cho bien cua object -> field
 */

@Constraint(
        validatedBy = {DobValidator.class}
)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME) //xu li luc runtime
public @interface DobConstraint {
    String message() default "Invalid date of birth";

    //custom config
    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
