package world.deslauriers.validation;


import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

@Singleton
public class NumbersOnlyInterceptor implements ConstraintValidator<NumbersOnly, String> {

    private static final Logger log = LoggerFactory.getLogger(NumbersOnlyInterceptor.class);

    @Override
    public boolean isValid(String value, AnnotationValue<NumbersOnly> annotationMetadata, ConstraintValidatorContext context) {

        var regex = "[0-9]+";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(value);
        if (matcher.matches()){
            return true;
        }

        context.messageTemplate("Only number-characters are allowed.");
        log.error("Attempt to enter non-numeric characters in number field: " + context.getRootBean());
        return false;
    }

    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        return ConstraintValidator.super.isValid(value, context);
    }
}
