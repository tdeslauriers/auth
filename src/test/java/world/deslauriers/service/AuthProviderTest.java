package world.deslauriers.service;

import io.micronaut.http.*;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import io.micronaut.security.token.jwt.validator.JwtTokenValidator;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Role;
import world.deslauriers.model.database.User;
import world.deslauriers.model.database.UserRole;
import world.deslauriers.model.registration.RegistrationDto;
import world.deslauriers.repository.RoleRepository;
import world.deslauriers.repository.UserRepository;
import world.deslauriers.repository.UserRoleRepository;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class AuthProviderTest {

    @Inject private UserRepository userRepository;
    @Inject private UserRoleRepository userRoleRepository;
    @Inject private RoleRepository roleRepository;
    @Inject private PasswordEncoderService passwordEncoderService;

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    JwtTokenValidator jwtTokenValidator;

    private static final String VALID_EMAIL = "tom@deslauriers.world";
    private static final String VALID_CLEAR_PASSWORD = "Worst_password_ever!";
    private static final String VALID_FIRST = "tom";
    private static final String VALID_LAST = "deslauriers";

    @Test
    void testNoCredsLogin(){

        HttpRequest request = HttpRequest
                .create(HttpMethod.POST, "/login")
                .accept(MediaType.APPLICATION_JSON_TYPE);
        assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request);
        });
    }


    @Test
    void testGoodCredsLogin(){

        // register user
        var register = new RegistrationDto(
                VALID_EMAIL, VALID_CLEAR_PASSWORD, VALID_CLEAR_PASSWORD, VALID_FIRST, VALID_LAST);

        var request = HttpRequest.POST("/register", register);
        var response = client.toBlocking().exchange(request);

        // good creds
        var loginReq = HttpRequest.POST("/login", new UsernamePasswordCredentials(VALID_EMAIL, VALID_CLEAR_PASSWORD));

        HttpResponse<AccessRefreshToken> loginResponse = client
                .toBlocking()
                .exchange(loginReq, AccessRefreshToken.class);
        assertEquals(201, response.status().getCode());
        assertNotNull(loginResponse.body());
        assertNotNull(loginResponse.body().getAccessToken());
        assertEquals("Bearer", loginResponse.body().getTokenType());

        //bad creds
        var badLoginReq = HttpRequest.POST("/login", new UsernamePasswordCredentials(VALID_EMAIL, "incorrect_password"));
        var thrown = assertThrows(HttpClientResponseException.class, () -> {

            client.toBlocking().exchange(badLoginReq, AccessRefreshToken.class);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, thrown.getStatus());
        assertEquals(401, thrown.getStatus().getCode());
    }
}
