package world.deslauriers.validation;


import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

@Singleton
public class LettersOnlyInterceptor implements ConstraintValidator<LettersOnly, String> {

    private static final Logger log = LoggerFactory.getLogger(LettersOnlyInterceptor.class);

    @Override
    public boolean isValid(String value, AnnotationValue<LettersOnly> annotationMetadata, ConstraintValidatorContext context) {

        var regex = "^[\\p{L}][\\p{L}\\p{Zs}\\p{Pd}'.]+[\\p{L}]$";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(value);
        if (matcher.matches()){
            return true;
        }

        context.messageTemplate("Only letters and common naming convention characters (spaces, dashes, apostrophes) are allowed.");
        log.error("Attempt to enter numbers or special characters in letter field: " + context.getRootBean());
        return false;
    }

    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        return ConstraintValidator.super.isValid(value, context);
    }
}
