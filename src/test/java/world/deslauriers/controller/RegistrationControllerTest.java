package world.deslauriers.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.model.registration.RegistrationResponseDto;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class RegistrationControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    private static final String VALID_EMAIL = "tom@deslauriers.world";
    private static final String VALID_CLEAR_PASSWORD = "Qe`&3~Z+pbdI;*Faja.2";
    private static final String VALID_FIRST = "tom";
    private static final String VALID_LAST = "deslauriers";

    @Test
    void testRegisterUserMethods(){

        var register = new RegistrationDto(
                VALID_EMAIL, VALID_CLEAR_PASSWORD, VALID_CLEAR_PASSWORD, VALID_FIRST, VALID_LAST);

        var request = HttpRequest.POST("/register", register);
        var response = client.toBlocking().exchange(request);
        assertEquals(201, response.getStatus().getCode());
        assertEquals("Created", response.getStatus().getReason());


        // passwords don't match
        // email is same, but pw error should trigger first.
        var dontMatch = new RegistrationDto(
                VALID_EMAIL, VALID_CLEAR_PASSWORD, "2nd_Worst_password_ever", VALID_FIRST, VALID_LAST);
        var thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("/register", dontMatch));
        });
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getResponse().getStatus());
        assertEquals("Passwords do not match", thrown.getMessage());

        //existing user
        var existingUser = new RegistrationDto(
                VALID_EMAIL, VALID_CLEAR_PASSWORD, VALID_CLEAR_PASSWORD, VALID_FIRST, VALID_LAST);
        thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(HttpRequest.POST("/register", existingUser));
        });
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getResponse().getStatus());
        assertEquals("Username Unavailable", thrown.getMessage());
    }

}
