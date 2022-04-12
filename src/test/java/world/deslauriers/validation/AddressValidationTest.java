package world.deslauriers.validation;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Address;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class AddressValidationTest {

    @Inject
    Validator validator;

    public static final String STREET = "123 Main Street";
    public static final String CITY = "Big City";
    public static final String ZIP = "55555";

    @Test
    void testUsStateAnnotation(){

        var fail = validator.validate(new Address(STREET, CITY, "QQ", ZIP));
        assertEquals(1, fail.size());

        var pass = validator.validate(new Address(STREET, CITY, "CA", ZIP));
        assertEquals(0, pass.size());

    }
}
