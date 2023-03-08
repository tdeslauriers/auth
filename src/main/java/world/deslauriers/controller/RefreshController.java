package world.deslauriers.controller;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.errors.IssuingAnAccessTokenErrorCode;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.handlers.LoginHandler;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import io.micronaut.security.token.validator.RefreshTokenValidator;
import io.micronaut.validation.Validated;
import reactor.core.publisher.Mono;
import world.deslauriers.model.dto.RefreshTokenCmd;

import javax.validation.Valid;

@Secured(SecurityRule.IS_ANONYMOUS)
@Validated
@Controller("/refresh")
public class RefreshController {

    private final RefreshTokenValidator refreshTokenValidator;
    private final RefreshTokenPersistence refreshTokenPersistence;
    private final LoginHandler loginHandler;

    public RefreshController(RefreshTokenValidator refreshTokenValidator, RefreshTokenPersistence refreshTokenPersistence, LoginHandler loginHandler) {
        this.refreshTokenValidator = refreshTokenValidator;
        this.refreshTokenPersistence = refreshTokenPersistence;
        this.loginHandler = loginHandler;
    }

    @Post
    Mono<MutableHttpResponse<?>> refreshToken(HttpRequest<?> request, @Body @Valid RefreshTokenCmd cmd){

        var validRefreshToken = refreshTokenValidator.validate(cmd.refreshToken());
        if (validRefreshToken.isEmpty()){
            throw new OauthErrorResponseException(IssuingAnAccessTokenErrorCode.INVALID_GRANT, "Refresh token is invalid", null);
        }
        return Mono.from(refreshTokenPersistence.getAuthentication(validRefreshToken.get()))
                .map(authentication -> {
                    System.out.println(authentication.toString());
                    return loginHandler.loginRefresh(authentication, cmd.refreshToken(), request);
                });
    }
}
