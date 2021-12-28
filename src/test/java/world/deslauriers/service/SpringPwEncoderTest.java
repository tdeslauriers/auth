package world.deslauriers.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class SpringPwEncoderTest {

    @Inject
    private PasswordEncoderService passwordEncoderService;

    private static final String PLAIN_TEXT_PW = "plain_text_password";

    @Test
    void testPwEncodeMethods(){

        var hashed = passwordEncoderService.encode(PLAIN_TEXT_PW);

        assertNotNull(hashed);
        assertNotEquals(PLAIN_TEXT_PW, hashed);
        assertEquals("$2a$13", hashed.substring(0, 6)); // bcrypt prefix
        assertTrue(hashed.length() > 50);

        // test comparison
        assertTrue(passwordEncoderService.matches(PLAIN_TEXT_PW, hashed));
    }
}
