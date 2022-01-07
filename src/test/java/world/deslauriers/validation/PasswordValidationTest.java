package world.deslauriers.validation;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.registration.ValidPasswordDto;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MicronautTest
public class PasswordValidationTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void testValidPasswordAnnotation(){

        // lots of these have more than one problem
        var bad_passwords = new ArrayList<>(Arrays.asList(
                "too_short",
                "has space",
                "has    tab",
                "no_upper_case",
                "NO_LOWER_CASE",
                "no_numbers",
                "no characters",
                "abcd",
                "123",
                "hjkl", // qwerty
                "AAA repeat chars"
        ));

        bad_passwords.forEach(password -> {

            var thrown = assertThrows(HttpClientResponseException.class, () -> {
                client.toBlocking().exchange(HttpRequest.POST("/register/valid-password", new ValidPasswordDto(password)));
            });
            assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());
        });


        var good_password = "b:xrq!=1vZXm\\n*(mg~f";
        var response = client
                .toBlocking()
                .exchange(HttpRequest.POST("/register/valid-password", new ValidPasswordDto(good_password)));

        assertEquals(HttpStatus.OK, response.getStatus());
    }
}
