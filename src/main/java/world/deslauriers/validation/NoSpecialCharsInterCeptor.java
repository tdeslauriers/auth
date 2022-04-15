package world.deslauriers.validation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

@Singleton
public class NoSpecialCharsInterCeptor implements ConstraintValidator<NoSpecialChars, String> {

    private static final Logger log = LoggerFactory.getLogger(NoSpecialCharsInterCeptor.class);

    @Override
    public boolean isValid(String value, AnnotationValue<NoSpecialChars> annotationMetadata, ConstraintValidatorContext context) {

        var regex = "[\\p{L}\\p{N}\\p{Zs}\\p{Pd}\\p{Pc}.,]+";
        var pattern = Pattern.compile(regex);
        var matcher = pattern.matcher(value);
        if (matcher.matches()){
            return true;
        }

        context.messageTemplate("Only common characters (letters, numbers, dashes, etc.) are allowed.");
        log.error("Attempt to enter special characters in field: " + context.getRootBean());
        return false;
    }

    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        return ConstraintValidator.super.isValid(value, context);
    }
}
