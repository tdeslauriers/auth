package world.deslauriers.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.registration.RegistrationDto;

@MicronautTest
public class RegistrationControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    private static final String VALID_EMAIL = "tom@deslauriers.world";
    private static final String VALID_CLEAR_PASSWORD = "Worst_password_ever!";
    private static final String VALID_FIRST = "tom";
    private static final String VALID_LAST = "deslauriers";

    @Test
    void registerUserTest(){

        var register = new RegistrationDto(
                VALID_EMAIL, VALID_CLEAR_PASSWORD, VALID_CLEAR_PASSWORD, VALID_FIRST, VALID_LAST);

        var request = HttpRequest.POST("/register", register);
        var response = client.toBlocking().exchange(request);
    }

}
