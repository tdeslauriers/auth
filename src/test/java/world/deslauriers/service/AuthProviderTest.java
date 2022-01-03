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
    private static final String VALID_ROLE_1 = "GALLERY_READ";
    private static final String VALID_ROLE_2 = "GALLERY_EDIT";

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
    void testBadCredsLogin(){

        // set up
        var user = userRepository.save(new User(
                VALID_EMAIL, passwordEncoderService.encode(VALID_CLEAR_PASSWORD),
                VALID_FIRST, VALID_LAST, LocalDate.now(), true, false, false));

        var ur1 = userRoleRepository.save(new UserRole(user, roleRepository.save(new Role(VALID_ROLE_1))));
        var ur2 = userRoleRepository.save(new UserRole(user, roleRepository.save(new Role(VALID_ROLE_2))));

        // bad password
        HttpRequest request = HttpRequest
                .create(HttpMethod.POST, "/login")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .body(new UsernamePasswordCredentials(VALID_EMAIL, "bad_password"));

        assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(request);
        });
        try {
            var response = client.toBlocking().exchange(request);
        } catch (HttpClientResponseException e){
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals("User Not Found", e.getMessage());
        }

        // user not in system
        HttpRequest req2 = HttpRequest
                .create(HttpMethod.POST, "/login")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .body(new UsernamePasswordCredentials("not@here.com", "bad_password"));

        assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(req2);
        });

        try {
            var response = client.toBlocking().exchange(req2);
        } catch (HttpClientResponseException e){
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals("User Not Found", e.getMessage());
        }
    }

    @Test
    void testGoodCredsLogin(){

        // set up
        var user = userRepository.save(new User(
                VALID_EMAIL, passwordEncoderService.encode(VALID_CLEAR_PASSWORD),
                VALID_FIRST, VALID_LAST, LocalDate.now(), true, false, false));

        var ur1 = userRoleRepository.save(new UserRole(user, roleRepository.save(new Role(VALID_ROLE_1))));
        var ur2 = userRoleRepository.save(new UserRole(user, roleRepository.save(new Role(VALID_ROLE_2))));

        // good creds
        HttpRequest request = HttpRequest
                .create(HttpMethod.POST, "/login")
                .accept(MediaType.APPLICATION_JSON)
                .body(new UsernamePasswordCredentials(VALID_EMAIL, VALID_CLEAR_PASSWORD));

        HttpResponse<AccessRefreshToken> response = client
                .toBlocking()
                .exchange(request, AccessRefreshToken.class);
        assertEquals(200, response.status().getCode());
        assertNotNull(response.body());
        assertNotNull(response.body().getAccessToken());
    }
}
