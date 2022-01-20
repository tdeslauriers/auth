package world.deslauriers.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.registration.RegistrationDto;

@MicronautTest
public class ProfileControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    private static final String VALID_EMAIL = "tom@deslauriers.world";
    private static final String VALID_CLEAR_PASSWORD = "}Lk?!+MT1L&B'PTM8gRZ";
    private static final String VALID_FIRST = "tom";
    private static final String VALID_LAST = "deslauriers";

    @Test
    void testProfileRestCalls(){

        // register user
        var register = new RegistrationDto(
                VALID_EMAIL, VALID_CLEAR_PASSWORD, VALID_CLEAR_PASSWORD, VALID_FIRST, VALID_LAST);

        var request = HttpRequest.POST("/register", register);
        var response = client.toBlocking().exchange(request);

        // login
        var creds = HttpRequest.POST("/login", new UsernamePasswordCredentials(VALID_EMAIL, VALID_CLEAR_PASSWORD));
        var token = client
                .toBlocking()
                .exchange(creds, AccessRefreshToken.class);

        // get user profile
        var profile = client
                .toBlocking()
                .retrieve(HttpRequest.GET("/profile/user")
                        .header("Authorization", "Bearer " + token.body().getAccessToken()), ProfileDto.class);

        System.out.println(profile);
    }

}
