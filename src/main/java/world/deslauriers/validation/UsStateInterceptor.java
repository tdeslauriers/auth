package world.deslauriers.validation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.validation.constants.State;

@Singleton
public class UsStateInterceptor implements ConstraintValidator<UsState, String> {

    private static final Logger log = LoggerFactory.getLogger(UsStateInterceptor.class);

    @Override
    public boolean isValid(String state, AnnotationValue<UsState> annotationMetadata, ConstraintValidatorContext context) {

        for (State s: State.values()) if (state.equals(s.toString())) return true;
        context.messageTemplate("Incorrect US state abbreviation.");
        log.error("Attempt to enter incorrect US state abbreviation.");
        return false;
    }

    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        return ConstraintValidator.super.isValid(value, context);
    }
}
