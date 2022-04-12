package world.deslauriers.validation;

import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;
import org.passay.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Singleton
public class PasswordInterceptor implements ConstraintValidator<PasswordComplexity, String> {

    private static final Logger log = LoggerFactory.getLogger(PasswordInterceptor.class);

    @Override
    public boolean isValid(String password, AnnotationValue<PasswordComplexity> annotationMetadata, ConstraintValidatorContext context) {

        Properties props = new Properties();
        InputStream inputStream = getClass()
                                    .getClassLoader()
                                    .getResourceAsStream("passay.properties");
        try {
            props.load(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        MessageResolver resolver = new PropertiesMessageResolver(props);

        List<Rule> rules = new ArrayList<>(Arrays.asList(
                new LengthRule(12, 64),
                new WhitespaceRule(),
                new CharacterRule(EnglishCharacterData.UpperCase, 1),
                new CharacterRule(EnglishCharacterData.LowerCase, 1),
                new CharacterRule(EnglishCharacterData.Digit, 1),
                new CharacterRule(EnglishCharacterData.Special, 1),
                new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 4, false),
                new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false),
                new IllegalSequenceRule(EnglishSequenceData.USQwerty, 4, false),
                new RepeatCharactersRule( 3)
        ));
        PasswordValidator validator = new PasswordValidator(resolver, rules);

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid())
            return true;

        context.messageTemplate(validator
                .getMessages(result)
                .stream()
                .collect(Collectors.joining(". ")));
        log.warn("Weak password/rule-breaking password entry.");

        return false;
    }

    @Override
    public boolean isValid(String value, javax.validation.ConstraintValidatorContext context) {
        return ConstraintValidator.super.isValid(value, context);
    }
}
