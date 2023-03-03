package world.deslauriers.controller;

import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.generator.RefreshTokenGenerator;
import io.micronaut.security.token.jwt.endpoints.TokenRefreshRequest;
import io.micronaut.security.token.jwt.render.AccessRefreshToken;
import io.micronaut.security.token.jwt.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import world.deslauriers.model.database.Refresh;
import world.deslauriers.model.dto.RefreshTokenCmd;
import world.deslauriers.repository.RefreshTokenRepository;

import java.util.Map;
import java.util.stream.Collectors;

import static io.micronaut.http.HttpStatus.BAD_REQUEST;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
public class RefreshControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    RefreshTokenGenerator refreshTokenGenerator;

    @Inject
    RefreshTokenRepository refreshTokenRepository;

    @Test
    void testRefresh(){

        var unsigned = "unsigned";
        Argument<BearerAccessRefreshToken> bodyArgument = Argument.of(BearerAccessRefreshToken.class);
        Argument<Map> errorArgument = Argument.of(Map.class);

        var err = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking()
                    .exchange(HttpRequest.POST("/refresh",
                            new TokenRefreshRequest(unsigned)),
                            bodyArgument,
                            errorArgument);
        });
        assertEquals(BAD_REQUEST, err.getStatus());

        var errors = err.getResponse().getBody(Map.class);
        assertTrue(errors.isPresent());

        var body = errors.get();
        assertEquals("invalid_grant", body.get("error"));
        assertEquals("Refresh token is invalid", body.get("error_description"));

        // token that is not persisted in db generates error
        var vader = Authentication.build("darth.vader@empire.com");
        var refreshToken = refreshTokenGenerator.createKey(vader);
        var validRefresh = refreshTokenGenerator.generate(vader, refreshToken);
        assertTrue(validRefresh.isPresent());

        var req = HttpRequest.POST("/refresh", new RefreshTokenCmd("refresh_token", validRefresh.get()));
        err = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange(req, bodyArgument, errorArgument);
        });
        assertEquals(BAD_REQUEST, err.getStatus());

        errors = err.getResponse().getBody(Map.class);
        assertTrue(errors.isPresent());

        body = errors.get();
        assertEquals("invalid_grant", body.get("error"));
        assertEquals("Refresh token not found", body.get("error_description"));

        // happy path
        var refershTokenCount = refreshTokenRepository.count().block(); //baseline
        var creds = new UsernamePasswordCredentials("darth.vader@empire.com", "#1-Pod-Racer!");
        var request = HttpRequest.POST("/login", creds);
        var response = client.toBlocking().retrieve(request, BearerAccessRefreshToken.class);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals(refershTokenCount + 1, refreshTokenRepository.count().block());

        var success = client.toBlocking()
                .retrieve(HttpRequest.POST("/refresh",
                        new RefreshTokenCmd("refresh_token", response.getRefreshToken())),
                        AccessRefreshToken.class);
        assertNotNull(success.getAccessToken());
        assertNotEquals(response.getAccessToken(), success.getAccessToken());

        var token = refreshTokenRepository.findAll()
                .toStream()
                .toList()
                .stream()
                .filter(refresh -> refresh.username().equals("darth.vader@empire.com"))
                .findAny();
        assertTrue(token.isPresent());
        var revoke = refreshTokenRepository.update(
                new Refresh(token.get().id(), token.get().username(), token.get().refreshToken(), true, token.get().dateCreated()));

        err = assertThrows(HttpClientResponseException.class, () -> {
            client.toBlocking().exchange( HttpRequest.POST("/refresh", new RefreshTokenCmd("refresh_token", response.getRefreshToken())), bodyArgument, errorArgument);
        });
        assertEquals(BAD_REQUEST, err.getStatus());

        errors = err.getResponse().getBody(Map.class);
        assertTrue(errors.isPresent());

        body = errors.get();
        assertEquals("invalid_grant", body.get("error"));
        assertEquals("Refresh token revoked", body.get("error_description"));
    }
}
