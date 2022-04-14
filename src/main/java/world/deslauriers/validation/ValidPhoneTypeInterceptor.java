package world.deslauriers.validation;


import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import world.deslauriers.validation.constants.PhoneType;



@Singleton
public class ValidPhoneTypeInterceptor implements ConstraintValidator<ValidPhoneType, String> {

    private static final Logger log = LoggerFactory.getLogger(UsStateInterceptor.class);

    @Override
    public boolean isValid(String type, AnnotationValue<ValidPhoneType> annotationMetadata, ConstraintValidatorContext context) {

        for (PhoneType t: PhoneType.values()) if (type.toUpperCase().equals(t.toString())) return true;

        context.messageTemplate("Incorrect phone type.");
        log.error("Attempt to enter incorrect phone type: " + context.getRootBean());
        return false;
    }

    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        return ConstraintValidator.super.isValid(value, context);
    }
}
