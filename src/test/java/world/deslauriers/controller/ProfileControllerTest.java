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
import world.deslauriers.model.profile.ProfileDto;
import world.deslauriers.model.registration.RegistrationDto;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.atomic.AtomicReference;

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
        AtomicReference<ProfileDto> profile = new AtomicReference<>(client
                .toBlocking()
                .retrieve(HttpRequest.GET("/profiles/user")
                        .header("Authorization", "Bearer " + token.body().getAccessToken()), ProfileDto.class));

        assertNotNull(profile.get().id());
        assertEquals(VALID_EMAIL, profile.get().username());

        // regular user attempt to update must fail on admin endpoint
        var userUpdateReq = HttpRequest.PUT("/profiles/edit", new ProfileDto(
                profile.get().id(),
                profile.get().username(),
                profile.get().firstname(),
                profile.get().lastname(),
                profile.get().dateCreated(),
                profile.get().enabled(),
                profile.get().accountExpired(),
                profile.get().accountLocked(),
                LocalDate.of(1979, Month.APRIL, 1),
                user.uuid(), null,
                null,
                null)).header("Authorization", "Bearer " + token.body().getAccessToken());
        var thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(userUpdateReq);
        });
        assertEquals(HttpStatus.FORBIDDEN, thrown.getResponse().getStatus());

        // user update own profile
        // user not allowed to specify id/username/datecreated/enabled/expired/locked
        // endpoint should dump those values
        var validUserUpdateReq = HttpRequest.PUT("/profiles/user", new ProfileDto(
                666L,
                "agent.smith@matrix.com",
                "Agent",
                "Smith",
                profile.get().dateCreated(),
                false,
                profile.get().accountExpired(),
                profile.get().accountLocked(),
                LocalDate.of(2001, Month.DECEMBER, 25),
                user.uuid(), null,
                null,
                null)).header("Authorization", "Bearer " + token.body().getAccessToken());
        var updated = client.toBlocking().exchange(validUserUpdateReq);
        assertEquals(HttpStatus.NO_CONTENT, updated.getStatus());
        profile.set(client
                .toBlocking()
                .retrieve(HttpRequest.GET("/profiles/user")
                        .header("Authorization", "Bearer " + token.body().getAccessToken()), ProfileDto.class));
        assertEquals("Agent", profile.get().firstname());
        assertEquals("Smith", profile.get().lastname());
        // endpoint must disregard/dump disallowed fields
        assertNotEquals(666L, profile.get().id());
        assertNotEquals("agent.smith@matrix.com", profile.get().username());
        assertEquals(VALID_EMAIL, profile.get().username());  // cant change your email or your token wont work
        assertTrue(profile.get().enabled());

        // login as admin (created test data)
        creds = HttpRequest.POST("/login", new UsernamePasswordCredentials(ADMIN_EMAIL, ADMIN_CLEAR_PASSWORD));
        token = client.toBlocking().exchange(creds, AccessRefreshToken.class);

        profile.set(client
                .toBlocking()
                .retrieve(HttpRequest.GET("/profiles/" + profile.get().id())
                        .header("Authorization", "Bearer " + token.body().getAccessToken()), ProfileDto.class));
        assertNotNull(profile);
        assertEquals(VALID_EMAIL, profile.get().username());

        // happy path
//        var adminUpdateReq = HttpRequest.PUT("/profiles/edit", new ProfileDto(
//                profile.get().id(),
//                "bond.james@bond.com",
//                "James",
//                "Bond",
//                profile.get().dateCreated(),
//                false,
//                profile.get().accountExpired(),
//                profile.get().accountLocked(),
//                null,
//                null,
//                null)).bearerAuth("Bearer " + token.body().getAccessToken());
//        updated = client.toBlocking().exchange(adminUpdateReq);
//        assertEquals(HttpStatus.OK, updated.getStatus());

        // for fun: using logback not log4j2
        var jndi = HttpRequest.PUT("/profiles/edit", new ProfileDto(
                profile.get().id(),
                "${jndi:ldap//deslauriers.world/evil}@nope.com",
                "${jndi:ldap//deslauriers.world/evil}",
                "jndi:ldap//deslauriers.world/evil",
                profile.get().dateCreated(),
                profile.get().enabled(),
                profile.get().accountExpired(),
                profile.get().accountLocked(),
                null,
                user.uuid(), null,
                null,
                null)).header("Authorization", "Bearer " + token.body().getAccessToken());
        thrown = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(jndi);
        });
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatus());

        // sad path
        io.micronaut.http.HttpResponse<AccessRefreshToken> finalToken = token;
        thrown = assertThrows(HttpClientResponseException.class, () -> {

            profile.set(client
                    .toBlocking()
                    .retrieve(HttpRequest.GET("/profiles/666")
                            .header("Authorization", "Bearer " + finalToken.body().getAccessToken()), ProfileDto.class));

        });
        assertEquals(HttpStatus.NOT_FOUND, thrown.getStatus());
    }
}
