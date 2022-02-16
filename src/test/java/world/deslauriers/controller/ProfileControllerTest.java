package world.deslauriers.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.User;
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.registration.RegistrationDto;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class ProfileControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    private static final String VALID_EMAIL = "tom@deslauriers.world";
    private static final String VALID_CLEAR_PASSWORD = "}Lk?!+MT1L&B'PTM8gRZ";
    private static final String VALID_FIRST = "tom";
    private static final String VALID_LAST = "deslauriers";

    private static final String ADMIN_EMAIL = "admin@deslauriers.world";
    private static final String ADMIN_CLEAR_PASSWORD = "H~Z\\ysbY[fOg|4^86:BQ";

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
                .retrieve(HttpRequest.GET("/profiles/user")
                        .header("Authorization", "Bearer " + token.body().getAccessToken()), ProfileDto.class);

        assertNotNull(profile.id());
        assertEquals(VALID_EMAIL, profile.username());

        // regular user attempt to update must fail
        var userUpdateReq = HttpRequest.PUT("/profiles/edit", new ProfileDto(
                profile.id(),
                profile.username(),
                profile.firstname(),
                profile.lastname(),
                profile.dateCreated(),
                profile.enabled(),
                profile.accountExpired(),
                profile.accountLocked(),
                null,
                null)).header("Authorization", "Bearer " + token.body().getAccessToken());
        var thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(userUpdateReq);
        });
        assertEquals(HttpStatus.FORBIDDEN, thrown.getResponse().getStatus());

        // login as admin (created test data)
        creds = HttpRequest.POST("/login", new UsernamePasswordCredentials(ADMIN_EMAIL, ADMIN_CLEAR_PASSWORD));
        token = client.toBlocking().exchange(creds, AccessRefreshToken.class);

        // happy path
        var adminUpdateReq = HttpRequest.PUT("/profiles/edit", new ProfileDto(
                profile.id(),
                "bond.james@bond.com",
                "James",
                "Bond",
                profile.dateCreated(),
                false,
                profile.accountExpired(),
                profile.accountLocked(),
                null,
                null)).header("Authorization", "Bearer " + token.body().getAccessToken());
        var updated = client.toBlocking().exchange(adminUpdateReq);
        assertEquals(HttpStatus.NO_CONTENT, updated.getStatus());

        var jndi= HttpRequest.PUT("/profiles/edit", new ProfileDto(
                profile.id(),
                "${jndi:ldap//deslauriers.world/evil}@nope.com",
                profile.firstname(),
                profile.lastname(),
                profile.dateCreated(),
                profile.enabled(),
                profile.accountExpired(),
                profile.accountLocked(),
                null,
                null)).header("Authorization", "Bearer " + token.body().getAccessToken());
        assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(jndi);
        });
    }


}
