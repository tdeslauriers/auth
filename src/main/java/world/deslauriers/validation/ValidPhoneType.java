package world.deslauriers.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ANNOTATION_TYPE, FIELD, METHOD})
@Retention(RUNTIME)
@Constraint(validatedBy = ValidPhoneTypeInterceptor.class)
@Documented
public @interface ValidPhoneType {

    String message() default "";

    Class<?>[] group() default {};

    Class<? extends Payload>[] payload() default {};
}
