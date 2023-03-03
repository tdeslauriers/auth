package world.deslauriers.service;

import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import world.deslauriers.model.database.Refresh;
import world.deslauriers.model.database.User;
import world.deslauriers.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.HashMap;

import static io.micronaut.security.errors.IssuingAnAccessTokenErrorCode.INVALID_GRANT;

@Singleton
public class RefreshTokenPersistenceImpl implements RefreshTokenPersistence {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    public RefreshTokenPersistenceImpl(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    @Override
    public void persistToken(RefreshTokenGeneratedEvent event) {
        if (event != null &&
                event.getRefreshToken() != null &&
                event.getAuthentication() != null &&
                event.getAuthentication().getName() != null){
            refreshTokenRepository
                    .save(new Refresh(event.getAuthentication().getName(), event.getRefreshToken(), false, Instant.now()))
                    .subscribe();
        }
    }

    @Override
    public Publisher<Authentication> getAuthentication(String refreshToken) {
        return Flux.create(emitter -> {
            refreshTokenRepository.findByRefreshToken(refreshToken)
                    .switchIfEmpty(Mono.defer(() -> {
                        emitter.error(new OauthErrorResponseException(INVALID_GRANT, "Refresh token not found", null));
                        return Mono.empty();
                    }))
                    .subscribe(refresh -> {
                        if (refresh.revoked()){
                            emitter.error(new OauthErrorResponseException(INVALID_GRANT, "Refresh token revoked", null));
                        } else {
                            userService.getUserByUsername(refresh.username())
                                    .subscribe(u -> {
                                        emitter.next(buildAccessToken(u));
                                        emitter.complete();
                                    });
                        }
                    });
        }, FluxSink.OverflowStrategy.ERROR);
    }

    private Authentication buildAccessToken(User user){

        var authorities = user.userRoles()
                .stream()
                .map(userRole -> userRole.role().role())
                .toList();
        var attributes = new HashMap<String, Object>();
        attributes.put("firstname", user.firstname());
        attributes.put("lastname", user.lastname());
        attributes.put("user_uuid", user.uuid());

        return Authentication.build(user.username(), authorities, attributes);
    }
}
